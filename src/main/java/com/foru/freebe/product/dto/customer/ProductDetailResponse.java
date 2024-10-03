package com.foru.freebe.product.dto.customer;

import java.util.List;

import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductDiscountDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDetailResponse {
    @NotBlank
    private String productTitle;

    private String productDescription;

    @NotNull
    private Long basicPrice;

    @NotNull
    private List<String> productImageUrls;

    @NotNull
    private List<ProductComponentDto> productComponents;

    private List<ProductOptionDto> productOptions;

    private List<ProductDiscountDto> productDiscounts;

    @Builder
    public ProductDetailResponse(String productTitle, String productDescription, Long basicPrice,
        List<String> productImageUrls, List<ProductComponentDto> productComponents,
        List<ProductOptionDto> productOptions, List<ProductDiscountDto> productDiscounts) {
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.basicPrice = basicPrice;
        this.productImageUrls = productImageUrls;
        this.productComponents = productComponents;
        this.productOptions = productOptions;
        this.productDiscounts = productDiscounts;
    }
}
