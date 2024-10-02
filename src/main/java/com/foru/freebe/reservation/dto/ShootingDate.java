package com.foru.freebe.reservation.dto;

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
    private TimeSlot newShootingDate;

    @Builder
    public ShootingDate(Long reservationFormId, TimeSlot newShootingDate) {
        this.reservationFormId = reservationFormId;
        this.newShootingDate = newShootingDate;
    }
}
