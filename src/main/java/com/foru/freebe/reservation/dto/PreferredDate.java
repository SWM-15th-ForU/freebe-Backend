package com.foru.freebe.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;

@Getter
public class PreferredDate {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
