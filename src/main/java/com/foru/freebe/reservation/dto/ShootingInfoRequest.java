package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShootingInfoRequest {
    @NotNull
    private ReservationStatus currentReservationStatus;

    @NotBlank
    @Size(max = 100, message = "촬영장소는 최대 100자까지 입력 가능합니다.")
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
