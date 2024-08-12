package com.foru.freebe.product.dto.photographer;

import com.foru.freebe.product.entity.DiscountType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDiscountDto {
    @NotNull
    private String title;
    @NotNull
    private DiscountType discountType;
    @NotNull
    @Min(value = 1, message = "상품 할인 값은 0보다 큰 자연수여야 합니다.")
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
