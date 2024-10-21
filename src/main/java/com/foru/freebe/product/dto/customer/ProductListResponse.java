package com.foru.freebe.product.dto.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductListResponse {
    @NotNull
    private Long productId;

    @NotBlank
    private String productTitle;

    @NotNull
    private Long basicPrice;

    @NotBlank
    private String productRepresentativeImageUrl;

    @Builder
    public ProductListResponse(Long productId, String productTitle, Long basicPrice,
        String productRepresentativeImageUrl) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.basicPrice = basicPrice;
        this.productRepresentativeImageUrl = productRepresentativeImageUrl;
    }
}
