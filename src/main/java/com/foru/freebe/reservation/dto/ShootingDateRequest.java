package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShootingDateRequest {
    @NotNull
    private ReservationStatus currentReservationStatus;

    @NotNull
    private TimeSlot newShootingDate;

    @Builder
    public ShootingDateRequest(ReservationStatus currentReservationStatus, TimeSlot newShootingDate) {
        this.currentReservationStatus = currentReservationStatus;
        this.newShootingDate = newShootingDate;
    }
}
