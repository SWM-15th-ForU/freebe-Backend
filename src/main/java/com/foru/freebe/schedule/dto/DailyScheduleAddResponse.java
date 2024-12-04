package com.foru.freebe.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyScheduleAddResponse {
    @NotNull
    private Long scheduleID;
}
