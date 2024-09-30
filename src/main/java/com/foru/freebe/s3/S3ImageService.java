package com.foru.freebe.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.foru.freebe.common.dto.ImageLinkSet;
import com.foru.freebe.errors.errorcode.AwsErrorCode;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.base-path.original}")
    private String originPath;

    @Value("${cloud.aws.s3.base-path.thumbnail}")
    private String thumbnailPath;

    @Value("${cloud.aws.s3.base-path.photographer}")
    private String photographerPath;

    @Value("${cloud.aws.s3.base-path.customer}")
    private String customerPath;

    @Value("${cloud.aws.s3.base-path.product}")
    private String productPath;

    @Value("${cloud.aws.s3.base-path.profile}")
    private String profilePath;

    @Value("${cloud.aws.s3.base-path.reservation}")
    private String reservationPath;

    public ImageLinkSet imageUploadToS3(List<MultipartFile> images, S3ImageType s3ImageType, Long memberId) throws
        IOException {

        List<String> originUrl = uploadOriginalImages(images, s3ImageType, memberId);
        return new ImageLinkSet(originUrl, null);
    }

    public ImageLinkSet imageUploadToS3(List<MultipartFile> images, S3ImageType s3ImageType, Long memberId,
        int thumbnailSize) throws IOException {

        List<String> originUrl = uploadOriginalImages(images, s3ImageType, memberId);
        List<String> thumbnailUrl = uploadThumbnailImages(images, s3ImageType, memberId, thumbnailSize);

        return new ImageLinkSet(originUrl, thumbnailUrl);
    }

    public String uploadOriginalImage(MultipartFile image, S3ImageType s3ImageType, Long memberId) throws IOException {
        String originKey = generateImagePath(image, s3ImageType, memberId, true);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        uploadToS3(originKey, image.getInputStream(), metadata);

        return amazonS3.getUrl(bucketName, originKey).toString();
    }

    public String uploadThumbnailImage(MultipartFile image, S3ImageType s3ImageType, Long memberId,
        int thumbnailSize) throws IOException {
        String thumbnailKey = generateImagePath(image, s3ImageType, memberId, false);
        try (InputStream originalImageStream = image.getInputStream();
             ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream()) {

            resizeForThumbnail(thumbnailSize, originalImageStream, thumbnailOutputStream);

            InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailOutputStream.toByteArray());

            ObjectMetadata thumbnailMetadata = createMetadataForThumbnail(image,
                thumbnailOutputStream);

            uploadToS3(thumbnailKey, thumbnailInputStream, thumbnailMetadata);

            return amazonS3.getUrl(bucketName, thumbnailKey).toString();
        }
    }

    private List<String> uploadOriginalImages(List<MultipartFile> images, S3ImageType s3ImageType, Long memberId) throws
        IOException {

        List<String> originalImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originKey = generateImagePath(image, s3ImageType, memberId, true);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            metadata.setContentType(image.getContentType());

            uploadToS3(originKey, image.getInputStream(), metadata);
            addImageUrlFromS3(originKey, originalImageUrls);
        }
        return originalImageUrls;
    }

    private List<String> uploadThumbnailImages(List<MultipartFile> images, S3ImageType s3ImageType, Long memberId,
        int thumbnailSize) throws IOException {

        List<String> thumbnailImageUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String thumbnailKey = generateImagePath(image, s3ImageType, memberId, false);

            try (InputStream originalImageStream = image.getInputStream();
                 ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream()) {

                resizeForThumbnail(thumbnailSize, originalImageStream, thumbnailOutputStream);

                InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailOutputStream.toByteArray());
                ObjectMetadata thumbnailMetadata = createMetadataForThumbnail(image, thumbnailOutputStream);

                uploadToS3(thumbnailKey, thumbnailInputStream, thumbnailMetadata);
                addImageUrlFromS3(thumbnailKey, thumbnailImageUrls);
            }
        }

        return thumbnailImageUrls;
    }

    private static ObjectMetadata createMetadataForThumbnail(MultipartFile image,
        ByteArrayOutputStream thumbnailOutputStream) {
        ObjectMetadata thumbnailMetadata = new ObjectMetadata();
        thumbnailMetadata.setContentType(image.getContentType());
        thumbnailMetadata.setContentLength(thumbnailOutputStream.size());
        return thumbnailMetadata;
    }

    private static void resizeForThumbnail(int thumbnailSize, InputStream originalImageStream,
        ByteArrayOutputStream thumbnailOutputStream) throws IOException {
        Thumbnails.of(originalImageStream)
            .size(thumbnailSize, thumbnailSize)
            .toOutputStream(thumbnailOutputStream);
    }

    public void deleteImageFromS3(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (AmazonS3Exception e) {
            throw new RestApiException(AwsErrorCode.AMAZON_S3_EXCEPTION);
        } catch (AmazonServiceException e) {
            throw new RestApiException(AwsErrorCode.AMAZON_SERVICE_EXCEPTION);
        } catch (Exception e) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateImagePath(MultipartFile image, S3ImageType s3ImageType, Long memberId, Boolean isOrigin) {
        String fileName = image.getOriginalFilename();
        String uniqueId = UUID.randomUUID().toString();
        String imageType = isOrigin ? originPath : thumbnailPath;

        String basePath;
        switch (s3ImageType) {
            case PRODUCT -> basePath = photographerPath + memberId + productPath;
            case PROFILE -> basePath = photographerPath + memberId + profilePath;
            case RESERVATION -> basePath = customerPath + memberId + reservationPath;
            default -> throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
        return basePath + imageType + uniqueId + fileName;
    }

    private void uploadToS3(String key, InputStream imageInputStream, ObjectMetadata metadata) {
        try {
            amazonS3.putObject(bucketName, key, imageInputStream, metadata);
        } catch (AmazonS3Exception e) {
            throw new RestApiException(AwsErrorCode.AMAZON_S3_EXCEPTION);
        } catch (AmazonServiceException e) {
            throw new RestApiException(AwsErrorCode.AMAZON_SERVICE_EXCEPTION);
        } catch (Exception e) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void addImageUrlFromS3(String key, List<String> originalImageUrls) {
        String imageUrl = amazonS3.getUrl(bucketName, key).toString();
        originalImageUrls.add(imageUrl);
    }

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException e) {
            throw new RestApiException(CommonErrorCode.IO_EXCEPTION);
        }
    }
}
