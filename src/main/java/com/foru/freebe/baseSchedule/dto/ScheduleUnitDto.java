package com.foru.freebe.baseSchedule.dto;

import com.foru.freebe.member.entity.ScheduleUnit;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleUnitDto {

    @NotNull
    ScheduleUnit scheduleUnit;

    public ScheduleUnitDto(ScheduleUnit scheduleUnit) {
        this.scheduleUnit = scheduleUnit;
    }
}
