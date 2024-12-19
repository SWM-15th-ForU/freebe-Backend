package com.foru.freebe.schedule.dto;

import java.time.LocalTime;

import com.foru.freebe.schedule.entity.DayOfWeek;
import com.foru.freebe.schedule.entity.OperationStatus;

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

    @NotNull
    private OperationStatus operationStatus;

    @Builder
    public BaseScheduleDto(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
        OperationStatus operationStatus) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.operationStatus = operationStatus;
    }
}
