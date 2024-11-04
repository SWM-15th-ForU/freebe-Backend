package com.foru.freebe.s3;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

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
import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.errors.errorcode.AwsErrorCode;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.s3.model.ImageSize;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Value("${cloud.aws.s3.base-path.banner}")
    private String bannerPath;

    @Value("${cloud.aws.s3.base-path.reservation}")
    private String reservationPath;

    public ImageLinkSet imageUploadToS3(List<MultipartFile> images, S3ImageType s3ImageType, Long memberId,
        boolean createThumbnail) throws IOException {

        List<String> originUrls = new ArrayList<>();
        List<String> thumbnailUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String originUrl = uploadOriginalImage(image, s3ImageType, memberId);
            originUrls.add(originUrl);
        }
        if (createThumbnail) {
            for (MultipartFile image : images) {
                int thumbnailSize = determineThumbnailSize(s3ImageType);
                String thumbnailUrl = uploadThumbnailImage(image, s3ImageType, memberId, thumbnailSize);
                thumbnailUrls.add(thumbnailUrl);
            }
        }

        return new ImageLinkSet(originUrls, thumbnailUrls);
    }

    public SingleImageLink imageUploadToS3(MultipartFile image, S3ImageType s3ImageType, Long memberId,
        Boolean createThumbnail) throws IOException {

        String originUrl = uploadOriginalImage(image, s3ImageType, memberId);
        String thumbnailUrl = null;

        if (createThumbnail) {
            int thumbnailSize = determineThumbnailSize(s3ImageType);
            thumbnailUrl = uploadThumbnailImage(image, s3ImageType, memberId, thumbnailSize);
        }

        return new SingleImageLink(originUrl, thumbnailUrl);
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
            throw new RestApiException(AwsErrorCode.DELETE_OBJECT_EXCEPTION);
        }
    }

    private int determineThumbnailSize(S3ImageType s3ImageType) {
        int size;

        switch (s3ImageType) {
            case PROFILE -> size = 100;
            case PRODUCT, RESERVATION -> size = 300;
            default -> throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        return size;
    }

    private String uploadOriginalImage(MultipartFile image, S3ImageType s3ImageType, Long memberId) throws IOException {
        String originUrl = null;
        String originKey = generateImagePath(image, s3ImageType, memberId, true);
        try {
            ObjectMetadata metadata = setImageMetadata(image);

            uploadToS3(originKey, image.getInputStream(), metadata);

            originUrl = amazonS3.getUrl(bucketName, originKey).toString();
        } catch (Exception e) {
            log.error("Failed to upload original image: {}", e.getMessage());
            throw e;
        }
        return originUrl;
    }

    private ObjectMetadata setImageMetadata(MultipartFile image) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());
        return metadata;
    }

    private String uploadThumbnailImage(MultipartFile image, S3ImageType s3ImageType, Long memberId,
        int thumbnailSize) throws IOException {

        String thumbnailKey = generateImagePath(image, s3ImageType, memberId, false);

        try (InputStream originalImageStream = image.getInputStream();
             ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream()) {

            ImageSize thumbnail = calculateThumbnailSize(image, thumbnailSize);

            resizeForThumbnail(thumbnail.getWidth(), thumbnail.getHeight(), originalImageStream, thumbnailOutputStream);

            try (InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailOutputStream.toByteArray())) {
                ObjectMetadata thumbnailMetadata = createMetadataForThumbnail(image, thumbnailOutputStream);

                uploadToS3(thumbnailKey, thumbnailInputStream, thumbnailMetadata);

                return amazonS3.getUrl(bucketName, thumbnailKey).toString();
            }
        }
    }

    private ImageSize calculateThumbnailSize(MultipartFile image, int thumbnailSize) throws IOException {
        ImageSize originalSize = getOriginalSize(image);

        int thumbnailWidth;
        int thumbnailHeight;

        if (originalSize.getWidth() < originalSize.getHeight()) {
            thumbnailWidth = thumbnailSize;
            thumbnailHeight = (int)((thumbnailSize / (double)originalSize.getWidth()) * originalSize.getHeight());
        } else {
            thumbnailHeight = thumbnailSize;
            thumbnailWidth = (int)((thumbnailSize / (double)originalSize.getHeight()) * originalSize.getWidth());
        }

        return new ImageSize(thumbnailWidth, thumbnailHeight);
    }

    private ImageSize getOriginalSize(MultipartFile image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        int originalWidth = bufferedImage.getWidth();
        int originalHeight = bufferedImage.getHeight();
        return new ImageSize(originalWidth, originalHeight);
    }

    private ObjectMetadata createMetadataForThumbnail(MultipartFile image, ByteArrayOutputStream outputStream) {
        ObjectMetadata thumbnailMetadata = new ObjectMetadata();
        thumbnailMetadata.setContentType(image.getContentType());
        thumbnailMetadata.setContentLength(outputStream.size());
        return thumbnailMetadata;
    }

    private void resizeForThumbnail(int width, int thumbnailSize, InputStream originalImageStream,
        ByteArrayOutputStream outputStream) throws IOException {
        Thumbnails.of(originalImageStream)
            .size(width, thumbnailSize)
            .toOutputStream(outputStream);
    }

    private String generateImagePath(MultipartFile image, S3ImageType s3ImageType, Long memberId, Boolean isOrigin) {
        String fileName = image.getOriginalFilename();
        String uniqueId = UUID.randomUUID().toString();
        String imageType = isOrigin ? originPath : thumbnailPath;

        String basePath;
        switch (s3ImageType) {
            case PRODUCT -> basePath = photographerPath + memberId + productPath;
            case PROFILE -> basePath = photographerPath + memberId + profilePath;
            case BANNER -> basePath = photographerPath + memberId + bannerPath;
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
