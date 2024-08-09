package com.foru.freebe.product.dto.photographer;

import com.foru.freebe.product.entity.ActiveStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisteredProductResponse {
    private Long productId;
    private String productTitle;
    private Integer reservationCount;
    private ActiveStatus activeStatus;

    @Builder
    public RegisteredProductResponse(Long productId, String productTitle, Integer reservationCount,
        ActiveStatus activeStatus) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.reservationCount = reservationCount;
        this.activeStatus = activeStatus;
    }
}
