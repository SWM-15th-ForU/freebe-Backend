package com.foru.freebe.reservation.dto;

import java.time.LocalDate;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatusHistory {
    ReservationStatus reservationStatus;
    LocalDate statusUpdateDate;

    @Builder
    public StatusHistory(ReservationStatus reservationStatus, LocalDate statusUpdateDate) {
        this.reservationStatus = reservationStatus;
        this.statusUpdateDate = statusUpdateDate;
    }
}
