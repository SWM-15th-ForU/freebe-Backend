package com.foru.freebe.common.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.foru.freebe.errors.errorcode.AwsErrorCode;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3ImageService {
    private static final int THUMBNAIL_SIZE = 200;

    private final AmazonS3 amazonS3;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    @Value("${AWS_S3_ORIGIN_PATH}")
    private String originPath;

    @Value("${AWS_S3_THUMBNAIL_PATH}")
    private String thumbnailPath;

        List<String> originalImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originKey = originPath + image.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            metadata.setContentType(image.getContentType());

            uploadToS3(originKey, image.getInputStream(), metadata);
            addImageUrlFromS3(originKey, originalImageUrls);
        }
        return originalImageUrls;
    }

    public List<String> uploadThumbnailImage(List<MultipartFile> images) throws IOException {
        List<String> thumbnailImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String thumbnailKey = thumbnailPath + image.getOriginalFilename();
            InputStream originalImageStream = image.getInputStream();

            ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImageStream)
                .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .toOutputStream(thumbnailOutputStream);

            InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailOutputStream.toByteArray());

            String contentType = image.getContentType();
            ObjectMetadata thumbnailMetadata = new ObjectMetadata();
            thumbnailMetadata.setContentType(contentType);

            uploadToS3(thumbnailKey, thumbnailInputStream, thumbnailMetadata);
            addImageUrlFromS3(thumbnailKey, thumbnailImageUrls);
        }
        return thumbnailImageUrls;
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

    // TODO 추후 수정 및 삭제 API 티켓에서 사용할 예정
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

    private String getKeyFromImageAddress(String imageAddress) {
        try {
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1); // 맨 앞의 '/' 제거
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new RestApiException(CommonErrorCode.IO_EXCEPTION);
        }
    }
}
