package com.foru.freebe.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.respository.ProductImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProductService {
    private final ProductImageRepository productImageRepository;

    public ApiResponseDto<List<String>> getReferenceImages(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);

        List<String> referenceImages = new ArrayList<>();
        for (ProductImage productImage : productImages) {
            referenceImages.add(productImage.getOriginUrl());
        }

        return ApiResponseDto.<List<String>>builder()
            .status(200)
            .message("Good Response")
            .data(referenceImages)
            .build();
    }
}
