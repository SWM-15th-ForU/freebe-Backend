package com.foru.freebe.product.dto.photographer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductOptionDto {
    @NotBlank
    @Size(max = 30, message = "Title cannot be longer than 30 characters")
    private String title;

    @NotNull
    @PositiveOrZero
    private Integer price;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    private String description;

    @Builder
    public ProductOptionDto(String title, Integer price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
    }
}
