package com.foru.freebe.product.dto;

import com.foru.freebe.product.entity.ActiveStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisteredProductResponseDTO {
    private Long productId;
    private String productTitle;
    private Integer reservationCount;
    private ActiveStatus activeStatus;

    @Builder
    public RegisteredProductResponseDTO(Long productId, String productTitle, Integer reservationCount,
        ActiveStatus activeStatus) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.reservationCount = reservationCount;
        this.activeStatus = activeStatus;
    }
}
