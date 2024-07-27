package com.foru.freebe.product.dto;

import java.util.List;

import com.foru.freebe.productComponent.dto.ProductComponentDto;
import com.foru.freebe.productDiscount.dto.ProductDiscountDto;
import com.foru.freebe.productOption.dto.ProductOptionDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRegisterRequestDto {
    private String productTitle;
    private String productDescription;
    private List<String> productImageUrls;
    private List<ProductComponentDto> productComponents;
    private List<ProductOptionDto> productOptions;
    private List<ProductDiscountDto> productDiscounts;
}
