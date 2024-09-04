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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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

    public List<String> uploadOriginalImage(List<MultipartFile> images) throws IOException {
        List<String> originalImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originKey = "origin/" + image.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            metadata.setContentType(image.getContentType());

            amazonS3.putObject(bucketName, originKey, image.getInputStream(), metadata);

            String imageUrl = amazonS3.getUrl(bucketName, originKey).toString();
            originalImageUrls.add(imageUrl);
        }
        return originalImageUrls;
    }

    public List<String> uploadThumbnailImage(List<MultipartFile> images) throws IOException {
        List<String> thumbnailImageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            InputStream originalImageStream = image.getInputStream();

            ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImageStream)
                .size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
                .toOutputStream(thumbnailOutputStream);

            InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailOutputStream.toByteArray());

            ObjectMetadata thumbnailMetadata = new ObjectMetadata();
            thumbnailMetadata.setContentType("image/jpeg"); // 썸네일의 콘텐츠 타입 설정 (JPEG로 가정)

            String thumbnailKey = "thumbnail/" + image.getOriginalFilename();
            amazonS3.putObject(new PutObjectRequest(bucketName, thumbnailKey, thumbnailInputStream, thumbnailMetadata));

            String imageUrl = amazonS3.getUrl(bucketName, thumbnailKey).toString();
            thumbnailImageUrls.add(imageUrl);
        }
        return thumbnailImageUrls;
    }

    // TODO 추후 수정 및 삭제 API 티켓에서 사용할 예정
    public void deleteImageFromS3(String imageAddress) {
        String key = getKeyFromImageAddress(imageAddress);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (Exception e) {
            throw new RestApiException(CommonErrorCode.IO_EXCEPTION);
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
