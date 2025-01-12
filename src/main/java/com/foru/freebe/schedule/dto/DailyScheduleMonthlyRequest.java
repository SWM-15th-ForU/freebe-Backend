package com.foru.freebe.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyScheduleMonthlyRequest {
    @NotNull(message = "Year must not be null")
    private int year;

    @NotNull(message = "Month value must not be null")
    private int monthValue;
}
