package com.foru.freebe.baseSchedule.dto;

import org.joda.time.DateTime;

import com.foru.freebe.baseSchedule.entity.DayOfWeek;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BaseScheduleDto {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private DateTime startTime;

    @NotNull
    private DateTime endTime;
}
