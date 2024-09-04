package com.foru.freebe.reservation.dto;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FormComponent {
    private Long reservationId;
    private ReservationStatus reservationStatus;
    private String customerName;
    private String productTitle;
    private PreferredDate shootingDate;
}
