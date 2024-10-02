package com.foru.freebe.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShootingDate {
    @NotNull
    private Long reservationFormId;

    @NotNull
    private PreferredDate newShootingDate;
}
