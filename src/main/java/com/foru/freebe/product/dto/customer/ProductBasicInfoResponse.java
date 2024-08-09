package com.foru.freebe.product.dto.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductBasicInfoResponse {
    private Long productId;
    private String productTitle;
    private String productRepresentativeImageUrl;

    @Builder
    public ProductBasicInfoResponse(Long productId, String productTitle, String productRepresentativeImageUrl) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.productRepresentativeImageUrl = productRepresentativeImageUrl;
    }
}
