package com.foru.freebe.reservation.dto.alimtalk;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerCancelInfo {
    private String photographerPhoneNumber;
    private String customerName;
    private String productTitle;
    private String cancellationReason;
    private String reservationId;

    @Builder
    public CustomerCancelInfo(String photographerPhoneNumber, String customerName, String productTitle,
        String cancellationReason, String reservationId) {
        this.photographerPhoneNumber = photographerPhoneNumber;
        this.customerName = customerName;
        this.productTitle = productTitle;
        this.cancellationReason = cancellationReason;
        this.reservationId = reservationId;
    }
}
