package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShootingDate {
    @NotNull
    private Long reservationFormId;

    @NotNull
    private ReservationStatus reservationStatus;

    @NotNull
    private TimeSlot newShootingDate;

    @Builder
    public ShootingDate(Long reservationFormId, ReservationStatus reservationStatus, TimeSlot newShootingDate) {
        this.reservationFormId = reservationFormId;
        this.reservationStatus = reservationStatus;
        this.newShootingDate = newShootingDate;
    }
}
