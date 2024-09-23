package com.foru.freebe.product.dto.photographer;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDetailRequest {
    @NotBlank
    @Size(max = 30, message = "Title cannot be longer than 30 characters")
    private String productTitle;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    private String productDescription;

    @NotNull
    private List<ProductComponentDto> productComponents;

    private List<ProductOptionDto> productOptions;

    private List<ProductDiscountDto> productDiscounts;
}
