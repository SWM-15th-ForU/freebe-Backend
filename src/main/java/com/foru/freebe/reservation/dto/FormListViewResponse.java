package com.foru.freebe.reservation.dto;

import java.util.List;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormListViewResponse {
    @NotNull
    private ReservationStatus reservationStatus;

    @NotNull
    private List<FormComponent> formComponent;
}
