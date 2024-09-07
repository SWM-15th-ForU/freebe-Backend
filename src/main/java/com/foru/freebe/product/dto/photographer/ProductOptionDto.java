package com.foru.freebe.product.dto.photographer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductOptionDto {
    @NotNull
    private String title;

    @NotNull
    @Positive
    private Integer price;

    private String description;

    @Builder
    public ProductOptionDto(String title, Integer price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
    }
}
