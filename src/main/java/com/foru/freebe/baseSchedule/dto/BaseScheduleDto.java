package com.foru.freebe.baseSchedule.dto;

import java.time.LocalTime;

import com.foru.freebe.baseSchedule.entity.DayOfWeek;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BaseScheduleDto {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
}
