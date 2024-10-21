package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShootingInfoRequest {
    @NotNull
    private ReservationStatus currentReservationStatus;

    @NotBlank
    private String newShootingPlace;

    @NotNull
    private TimeSlot newShootingDate;

    @Builder
    public ShootingInfoRequest(ReservationStatus currentReservationStatus, String newShootingPlace,
        TimeSlot newShootingDate) {
        this.currentReservationStatus = currentReservationStatus;
        this.newShootingPlace = newShootingPlace;
        this.newShootingDate = newShootingDate;
    }
}
