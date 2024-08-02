package com.foru.freebe.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductComponentDto {
    @NotNull
    private String title;
    @NotNull
    private String content;
    private String description;
}
