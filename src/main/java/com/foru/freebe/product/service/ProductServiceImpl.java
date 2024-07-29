package com.foru.freebe.product.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.product.dto.ProductRegisterRequestDto;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final ProductComponentService productComponentService;
    private final ProductOptionService productOptionService;
    private final ProductDiscountService productDiscountService;

    @Override
    public ApiResponseDto<Void> registerProduct(ProductRegisterRequestDto productRegisterRequestDto) {
        String productTitle = productRegisterRequestDto.getProductTitle();
        String productDescription = productRegisterRequestDto.getProductDescription();

        Product productAsActive;
        if (productDescription != null) {
            productAsActive = Product.createProductAsActive(productTitle, productDescription);
        } else {
            productAsActive = Product.createProductAsActiveWithoutDescription(productTitle);
        }
        productRepository.save(productAsActive);

        productImageService.registerProductImage(productRegisterRequestDto.getProductImageUrls(), productAsActive);
        productComponentService.registerProductComponent(productRegisterRequestDto.getProductComponents(),
            productAsActive);
        if (productRegisterRequestDto.getProductOptions() != null) {
            productOptionService.registerProductOption(productRegisterRequestDto.getProductOptions(), productAsActive);
        }
        if (productRegisterRequestDto.getProductDiscounts() != null) {
            productDiscountService.registerDiscount(productRegisterRequestDto.getProductDiscounts(), productAsActive);
        }

        return ApiResponseDto.<Void>builder()
            .status(HttpStatus.CREATED)
            .message("Successfully added")
            .data(null)
            .build();
    }
}
