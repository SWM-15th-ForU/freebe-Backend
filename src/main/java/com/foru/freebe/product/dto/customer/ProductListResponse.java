package com.foru.freebe.product.dto.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductListResponse {
    private Long productId;
    private String productTitle;
    private String productRepresentativeImageUrl;

    @Builder
    public ProductListResponse(Long productId, String productTitle, String productRepresentativeImageUrl) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productRepresentativeImageUrl = productRepresentativeImageUrl;
    }
}
