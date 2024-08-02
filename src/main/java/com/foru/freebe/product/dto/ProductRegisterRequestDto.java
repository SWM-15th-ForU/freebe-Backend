package com.foru.freebe.product.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRegisterRequestDto {
    @NotNull
    private String productTitle;
    private String productDescription;
    @NotNull
    private List<String> productImageUrls;
    @NotNull
    private List<ProductComponentDto> productComponents;
    private List<ProductOptionDto> productOptions;
    private List<ProductDiscountDto> productDiscounts;
}
