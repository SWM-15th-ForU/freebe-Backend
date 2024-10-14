package com.foru.freebe.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerCancelledInfo {
    private String customerPhoneNumber;
    private String productTitle;
    private String cancellationReason;
    private String reservationId;

    @Builder
    public CustomerCancelledInfo(String customerPhoneNumber, String productTitle, String cancellationReason,
        String reservationId) {
        this.customerPhoneNumber = customerPhoneNumber;
        this.productTitle = productTitle;
        this.cancellationReason = cancellationReason;
        this.reservationId = reservationId;
    }
}
