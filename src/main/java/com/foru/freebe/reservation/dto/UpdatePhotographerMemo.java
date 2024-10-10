package com.foru.freebe.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePhotographerMemo {

    @NotNull
    private Long reservationFormId;
    private String photographerMemo;
}
