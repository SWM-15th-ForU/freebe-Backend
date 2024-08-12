package com.foru.freebe.reservation.dto;

import java.util.List;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormListViewResponse {
    private ReservationStatus reservationStatus;
    private List<FormComponent> formComponent;
}
