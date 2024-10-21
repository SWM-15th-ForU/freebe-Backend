package com.foru.freebe.reservation.dto.alimtalk;

import com.foru.freebe.reservation.dto.TimeSlot;
import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusUpdateNotice {
    private String customerPhoneNumber;
    private String photographerPhoneNumber;
    private String customerName;
    private String productTitle;
    private String cancellationReason;
    private String reservationId;
    private ReservationStatus updatedStatus;
    private TimeSlot shootingDate;
    private String profileName;

    @Builder
    public StatusUpdateNotice(String customerPhoneNumber, String photographerPhoneNumber, String customerName,
        String productTitle, String cancellationReason, String reservationId, ReservationStatus updatedStatus,
        TimeSlot shootingDate, String profileName) {
        this.customerPhoneNumber = customerPhoneNumber;
        this.photographerPhoneNumber = photographerPhoneNumber;
        this.customerName = customerName;
        this.productTitle = productTitle;
        this.cancellationReason = cancellationReason;
        this.reservationId = reservationId;
        this.updatedStatus = updatedStatus;
        this.shootingDate = shootingDate;
        this.profileName = profileName;
    }
}
