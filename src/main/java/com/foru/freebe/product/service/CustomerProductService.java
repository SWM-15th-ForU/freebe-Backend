package com.foru.freebe.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.customer.ProductListResponse;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.respository.ProductImageRepository;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProductService {
    private final ProfileRepository profileRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductDetailConvertor productDetailConvertor;

    public List<String> getReferenceImages(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);

        List<String> referenceImages = new ArrayList<>();
        for (ProductImage productImage : productImages) {
            referenceImages.add(productImage.getOriginUrl());
        }

        return referenceImages;
    }

    public ProductDetailResponse getDetailedInfoOfProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        return productDetailConvertor.convertProductToProductDetailResponse(product);
    }

    public List<ProductListResponse> getProductList(String profileName) {
        Profile photographerProfile = profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Member photographer = photographerProfile.getMember();

        List<Product> products = productRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<ProductListResponse> productListResponseList = new ArrayList<>();
        for (Product product : products) {
            List<ProductImage> productImage = productImageRepository.findByProduct(product);

            ProductListResponse productListResponse = ProductListResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productRepresentativeImageUrl(productImage.get(0).getOriginUrl())
                .build();

            productListResponseList.add(productListResponse);
        }

        return productListResponseList;
    }
}
