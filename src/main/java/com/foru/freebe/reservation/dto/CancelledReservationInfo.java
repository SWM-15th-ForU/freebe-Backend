package com.foru.freebe.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelledReservationInfo {
    private String photographerPhoneNumber;
    private String customerName;
    private String productTitle;
    private String cancellationReason;
    private String reservationId;

    @Builder
    public CancelledReservationInfo(String photographerPhoneNumber, String customerName, String productTitle,
        String cancellationReason, String reservationId) {
        this.photographerPhoneNumber = photographerPhoneNumber;
        this.customerName = customerName;
        this.productTitle = productTitle;
        this.cancellationReason = cancellationReason;
        this.reservationId = reservationId;
    }
}
