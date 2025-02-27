package com.foru.freebe.schedule.dto;

import java.time.LocalTime;
import java.util.List;

import com.foru.freebe.member.entity.ScheduleUnit;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AvailableScheduleResponse {

    @NotNull
    private LocalTime startTime;

    @NotNull
    private List<Boolean> availableSchedule;

    @NotNull
    private ScheduleUnit scheduleUnit;

    @Builder
    public AvailableScheduleResponse(LocalTime startTime, List<Boolean> availableSchedule, ScheduleUnit scheduleUnit) {
        this.startTime = startTime;
        this.availableSchedule = availableSchedule;
        this.scheduleUnit = scheduleUnit;
    }
}
