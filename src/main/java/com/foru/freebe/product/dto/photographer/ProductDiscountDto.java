package com.foru.freebe.product.dto.photographer;

import com.foru.freebe.product.entity.DiscountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDiscountDto {
    @NotBlank
    private String title;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @Positive
    private Integer discountValue;

    private String description;

    @Builder
    public ProductDiscountDto(String title, DiscountType discountType, Integer discountValue, String description) {
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.description = description;
    }
}
