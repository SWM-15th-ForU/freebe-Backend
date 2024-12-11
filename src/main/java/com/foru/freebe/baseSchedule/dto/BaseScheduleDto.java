package com.foru.freebe.baseSchedule.dto;

import java.time.LocalTime;

import com.foru.freebe.baseSchedule.entity.DayOfWeek;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseScheduleDto {

    @NotNull
    private DayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @Builder
    public BaseScheduleDto(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
