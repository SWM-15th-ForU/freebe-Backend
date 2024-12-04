package com.foru.freebe.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.foru.freebe.schedule.entity.ScheduleStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DailyScheduleRequest {
    @NotNull(message = "ScheduleStatus must not be null")
    private ScheduleStatus scheduleStatus;

    @NotNull(message = "Date must not be null")
    private LocalDate date;

    @NotNull(message = "Start time must not be null")
    private LocalTime startTime;

    @NotNull(message = "End time must not be null")
    private LocalTime endTime;
}
