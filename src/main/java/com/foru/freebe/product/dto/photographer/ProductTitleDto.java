package com.foru.freebe.product.dto.photographer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductTitleDto {
    private String title;

    public ProductTitleDto(String title) {
        this.title = title;
    }
}
