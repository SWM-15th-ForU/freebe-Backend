package com.foru.freebe.product.dto.photographer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductTitleResponse {
    private String title;

    public ProductTitleResponse(String title) {
        this.title = title;
    }
}
