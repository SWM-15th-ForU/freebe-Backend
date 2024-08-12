package com.foru.freebe.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductOptionDto {
    @NotNull
    private String title;
    @NotNull
    @Min(value = 1, message = "상품 옵션 가격은 0보다 큰 자연수여야 합니다.")
    private Integer price;
    private String description;

    @Builder
    public ProductOptionDto(String title, Integer price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
    }
}
