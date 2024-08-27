package com.foru.freebe.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.customer.ProductListResponse;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductDiscountDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;
import com.foru.freebe.product.entity.ProductDiscount;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductComponentRepository;
import com.foru.freebe.product.respository.ProductDiscountRepository;
import com.foru.freebe.product.respository.ProductImageRepository;
import com.foru.freebe.product.respository.ProductOptionRepository;
import com.foru.freebe.product.respository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProductService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;

    public ApiResponse<List<String>> getReferenceImages(Long photographerId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(photographerId);

        List<String> referenceImages = new ArrayList<>();
        for (ProductImage productImage : productImages) {
            referenceImages.add(productImage.getOriginUrl());
        }

        return ApiResponse.<List<String>>builder()
            .status(200)
            .message("Good Response")
            .data(referenceImages)
            .build();
    }

    public ApiResponse<ProductDetailResponse> getDetailedInfoOfProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<String> productImageUrls = getProductImageUrls(product);
        List<ProductComponentDto> productComponents = getProductComponents(product);
        List<ProductOptionDto> productOptions = getProductOptions(product);
        List<ProductDiscountDto> productDiscounts = getProductDiscounts(product);

        ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
            .productTitle(product.getTitle())
            .productDescription(product.getDescription())
            .productImageUrls(productImageUrls)
            .productComponents(productComponents)
            .productOptions(productOptions)
            .productDiscounts(productDiscounts)
            .build();

        return ApiResponse.<ProductDetailResponse>builder()
            .status(200)
            .message("Good Response")
            .data(productDetailResponse)
            .build();
    }

    public ApiResponse<List<ProductListResponse>> getProductList(Long photographerId) {
        Member photographer = memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

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
        return ApiResponse.<List<ProductListResponse>>builder()
            .status(200)
            .message("Good Request")
            .data(productListResponseList)
            .build();
    }

    private List<ProductDiscountDto> getProductDiscounts(Product product) {
        List<ProductDiscount> productDiscounts = productDiscountRepository.findByProduct(product);
        List<ProductDiscountDto> productDiscountResponse = new ArrayList<>();
        for (ProductDiscount productDiscount : productDiscounts) {
            ProductDiscountDto productDiscountDto = ProductDiscountDto.builder()
                .title(productDiscount.getTitle())
                .discountType(productDiscount.getDiscountType())
                .discountValue(productDiscount.getDiscountValue())
                .description(productDiscount.getDescription())
                .build();
            productDiscountResponse.add(productDiscountDto);
        }
        return productDiscountResponse;
    }

    private List<ProductOptionDto> getProductOptions(Product product) {
        List<ProductOption> productOptions = productOptionRepository.findByProduct(product);
        List<ProductOptionDto> productOptionResponse = new ArrayList<>();
        for (ProductOption productOption : productOptions) {
            ProductOptionDto productOptionDto = ProductOptionDto.builder()
                .title(productOption.getTitle())
                .price(productOption.getPrice())
                .description(productOption.getDescription())
                .build();
            productOptionResponse.add(productOptionDto);
        }
        return productOptionResponse;
    }

    private List<ProductComponentDto> getProductComponents(Product product) {
        List<ProductComponent> productComponents = productComponentRepository.findByProduct(product);
        List<ProductComponentDto> productComponentResponse = new ArrayList<>();
        for (ProductComponent productComponent : productComponents) {
            ProductComponentDto productComponentDto = ProductComponentDto.builder()
                .title(productComponent.getTitle())
                .content(productComponent.getContent())
                .description(productComponent.getDescription())
                .build();
            productComponentResponse.add(productComponentDto);
        }
        return productComponentResponse;
    }

    private List<String> getProductImageUrls(Product product) {
        List<ProductImage> productImages = productImageRepository.findByProduct(product);
        List<String> productImageUrls = new ArrayList<>();
        for (ProductImage productImage : productImages) {
            productImageUrls.add(productImage.getOriginUrl());
        }
        return productImageUrls;
    }
}
