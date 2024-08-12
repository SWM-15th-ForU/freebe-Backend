package com.foru.freebe.reservation.dto;

import java.time.LocalDateTime;

import com.foru.freebe.reservation.entity.ReservationStatus;

public class Progress {
    LocalDateTime startTime;
    ReservationStatus status;
    Boolean isChecked;
}
