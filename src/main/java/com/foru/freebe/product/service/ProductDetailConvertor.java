package com.foru.freebe.product.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.product.dto.customer.ProductDetailResponse;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductDetailConvertor {

    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;

    public ProductDetailResponse convertProductToProductDetailResponse(Product product) {
        List<String> productImageUrls = getProductImageUrls(product);
        List<ProductComponentDto> productComponents = convertToProductComponentDtoList(product);
        List<ProductOptionDto> productOptions = convertToProductOptionDtoList(product);
        List<ProductDiscountDto> productDiscounts = convertToProductDiscountDtoList(product);

        return ProductDetailResponse.builder()
            .productTitle(product.getTitle())
            .productDescription(product.getDescription())
            .productImageUrls(productImageUrls)
            .productComponents(productComponents)
            .productOptions(productOptions)
            .productDiscounts(productDiscounts)
            .build();
    }

    private List<ProductDiscountDto> convertToProductDiscountDtoList(Product product) {
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

    public List<ProductOptionDto> convertToProductOptionDtoList(Product product) {
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

    public List<ProductComponentDto> convertToProductComponentDtoList(Product product) {
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
            productImageUrls.add(productImage.getThumbnailUrl());
        }
        return productImageUrls;
    }
}
