package com.foru.freebe.reservation.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PastReservationResponse {
    @NotNull
    public int totalPages;

    @NotNull
    public List<PastReservationFormComponent> pastReservationFormComponent;
}
