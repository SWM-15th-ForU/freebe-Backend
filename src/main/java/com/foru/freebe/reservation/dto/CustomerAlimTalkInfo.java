package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerAlimTalkInfo {
    private String customerPhoneNumber;
    private String productTitle;
    private String cancellationReason;
    private String reservationId;
    private ReservationStatus updatedStatus;

    private

    @Builder
    public CustomerAlimTalkInfo(String customerPhoneNumber, String productTitle, String cancellationReason,
        String reservationId, ReservationStatus updatedStatus) {
        this.customerPhoneNumber = customerPhoneNumber;
        this.productTitle = productTitle;
        this.cancellationReason = cancellationReason;
        this.reservationId = reservationId;
        this.updatedStatus = updatedStatus;
    }
}
