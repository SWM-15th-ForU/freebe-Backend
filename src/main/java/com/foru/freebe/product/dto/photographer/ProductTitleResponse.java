package com.foru.freebe.product.dto.photographer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductTitleResponse {
    private String title;
    private Long productId;

    @Builder
    public ProductTitleResponse(String title, Long productId) {
        this.title = title;
        this.productId = productId;
    }
}
