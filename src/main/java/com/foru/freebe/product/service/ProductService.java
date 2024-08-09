package com.foru.freebe.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.ProductComponentDto;
import com.foru.freebe.product.dto.ProductDiscountDto;
import com.foru.freebe.product.dto.ProductOptionDto;
import com.foru.freebe.product.dto.ProductRegisterRequestDto;
import com.foru.freebe.product.dto.RegisteredProductResponseDto;
import com.foru.freebe.product.dto.UpdateProductRequestDto;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final MemberRepository memberRepository;

    public ApiResponse<Void> registerProduct(ProductRegisterRequestDto productRegisterRequestDto) {
        Member member = getMember(productRegisterRequestDto.getMemberId());

        String productTitle = productRegisterRequestDto.getProductTitle();
        String productDescription = productRegisterRequestDto.getProductDescription();

        // Active 상태의 product 추가
        Product productAsActive;
        if (productDescription != null) {
            productAsActive = Product.createProductAsActive(productTitle, productDescription, member);
        } else {
            productAsActive = Product.createProductAsActiveWithoutDescription(productTitle, member);
        }
        productRepository.save(productAsActive);

        registerProductImage(productRegisterRequestDto.getProductImageUrls(), productAsActive);
        registerProductComponent(productRegisterRequestDto.getProductComponents(),
            productAsActive);

        // 상품에 옵션이 있을 때 option 추가
        if (productRegisterRequestDto.getProductOptions() != null) {
            registerProductOption(productRegisterRequestDto.getProductOptions(), productAsActive);
        }

        // 상품에 할인이 있을 때 discount 추가
        if (productRegisterRequestDto.getProductDiscounts() != null) {
            registerDiscount(productRegisterRequestDto.getProductDiscounts(), productAsActive);
        }

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Successfully added")
            .data(null)
            .build();
    }

    public ApiResponse<List<RegisteredProductResponseDto>> getRegisteredProductList(Long memberId) {
        Member member = getMember(memberId);
        List<Product> registeredProductList = productRepository.findByMember(member)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<RegisteredProductResponseDto> registeredProducts = registeredProductList.stream()
            .map(product -> RegisteredProductResponseDto.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .reservationCount(0) // TODO 예약체결 관련 로직 구현 후 추가
                .activeStatus(product.getActiveStatus())
                .build())
            .collect(Collectors.toList());

        return ApiResponse.<List<RegisteredProductResponseDto>>builder()
            .status(200)
            .message("Successfully retrieved list of registered products")
            .data(registeredProducts)
            .build();
    }

    @Transactional
    public ApiResponse<Void> updateProductActiveStatus(UpdateProductRequestDto requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        product.updateProductActiveStatus(requestDto.getActiveStatus());

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Successfully updated product active status")
            .data(null)
            .build();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private void registerProductImage(List<String> productImageUrls, Product product) {
        for (String productImageUrl : productImageUrls) {
            ProductImage productImage = ProductImage.createProductImage(null, productImageUrl, product);
            productImageRepository.save(productImage);
        }
    }

    private void registerProductComponent(List<ProductComponentDto> productComponentDtoList, Product product) {
        for (ProductComponentDto productComponentDto : productComponentDtoList) {
            ProductComponent productComponent = ProductComponent.builder()
                .title(productComponentDto.getTitle())
                .content(productComponentDto.getContent())
                .description(productComponentDto.getDescription())
                .product(product)
                .build();

            productComponentRepository.save(productComponent);
        }
    }

    private void registerProductOption(List<ProductOptionDto> productOptionDtoList, Product product) {
        for (ProductOptionDto productOptionDto : productOptionDtoList) {
            ProductOption productOption = ProductOption.builder()
                .title(productOptionDto.getTitle())
                .price(productOptionDto.getPrice())
                .description(productOptionDto.getDescription())
                .product(product)
                .build();

            productOptionRepository.save(productOption);
        }
    }

    private void registerDiscount(List<ProductDiscountDto> productDiscountDtoList, Product product) {
        for (ProductDiscountDto productDiscountDto : productDiscountDtoList) {
            ProductDiscount productDiscount = ProductDiscount.builder()
                .title(productDiscountDto.getTitle())
                .discountType(productDiscountDto.getDiscountType())
                .discountValue(productDiscountDto.getDiscountValue())
                .description(productDiscountDto.getDescription())
                .product(product)
                .build();
            productDiscountRepository.save(productDiscount);
        }
    }
}