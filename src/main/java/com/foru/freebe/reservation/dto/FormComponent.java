package com.foru.freebe.reservation.dto;

import java.time.LocalDateTime;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FormComponent {
    private ReservationStatus reservationStatus;
    private String customerName;
    private String productTitle;
    private LocalDateTime shootingDate;
}
