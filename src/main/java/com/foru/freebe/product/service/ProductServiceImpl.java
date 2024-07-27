package com.foru.freebe.product.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.product.dto.ProductComponentDto;
import com.foru.freebe.product.dto.ProductDiscountDto;
import com.foru.freebe.product.dto.ProductOptionDto;
import com.foru.freebe.product.dto.ProductRegisterRequestDto;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductDiscount;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductDiscountRepository;
import com.foru.freebe.product.respository.ProductOptionRepository;
import com.foru.freebe.product.respository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final ProductComponentService productComponentService;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;

    @Override
    public ApiResponseDto<Void> addProduct(ProductRegisterRequestDto productRegisterRequestDto) {
        String productTitle = productRegisterRequestDto.getProductTitle();
        String productDescription = productRegisterRequestDto.getProductDescription();

        List<ProductComponentDto> productComponentDtoList = productRegisterRequestDto.getProductComponents();
        List<ProductOptionDto> productOptionDtoList = productRegisterRequestDto.getProductOptions();
        List<ProductDiscountDto> productDiscountDtoList = productRegisterRequestDto.getProductDiscounts();

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

        if (productOptionDtoList != null) {
            for (ProductOptionDto productOptionDto : productOptionDtoList) {
                if (productOptionDto.getDescription() != null) {
                    ProductOption productOption = ProductOption.createProductOption(
                        productOptionDto.getTitle(),
                        productOptionDto.getPrice(),
                        productOptionDto.getDescription(),
                        productAsActive);
                    productOptionRepository.save(productOption);
                } else {
                    ProductOption productOption = ProductOption.createProductOptionWithoutDescription(
                        productOptionDto.getTitle(),
                        productOptionDto.getPrice(),
                        productAsActive);
                    productOptionRepository.save(productOption);
                }
            }
        }

        if (productDiscountDtoList != null) {
            for (ProductDiscountDto productDiscountDto : productDiscountDtoList) {
                ProductDiscount productDiscount = ProductDiscount.builder()
                    .title(productDiscountDto.getTitle())
                    .discountType(productDiscountDto.getDiscountType())
                    .discountValue(productDiscountDto.getDiscountValue())
                    .description(productDiscountDto.getDescription())
                    .product(productAsActive)
                    .build();
                productDiscountRepository.save(productDiscount);
            }
        }

        return ApiResponseDto.<Void>builder()
            .status(HttpStatus.CREATED)
            .message("Successfully added")
            .data(null)
            .build();
    }
}
