package com.foru.freebe.schedule.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.foru.freebe.member.entity.ScheduleUnit;
import com.foru.freebe.schedule.dto.AvailableScheduleResponse;
import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.DailySchedule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleCalculator {

    private static final int SIXTY_MINUTES_CAPACITY = 24;
    private static final int THIRTY_MINUTES_CAPACITY = 48;
    private static final int SIXTY_MINUTES = 60;
    private static final int THIRTY_MINUTES = 30;
    private static final int INVALID_INDEX = -1;

    public AvailableScheduleResponse calculateAvailableSchedule(BaseSchedule baseSchedule,
        List<DailySchedule> openSchedules,
        List<DailySchedule> confirmedAndClosedSchedules,
        ScheduleUnit scheduleUnit) {

        int capacity = getCapacity(scheduleUnit);
        int timeUnit = getTimeUnit(scheduleUnit);

        List<Boolean> availability = new ArrayList<>(Collections.nCopies(capacity, false));

        applyBaseSchedule(baseSchedule, availability, timeUnit);
        applyDailySchedule(openSchedules, availability, true, timeUnit);
        applyDailySchedule(confirmedAndClosedSchedules, availability, false, timeUnit);

        return sliceAvailableSchedule(availability, timeUnit, scheduleUnit);
    }

    private int getCapacity(ScheduleUnit scheduleUnit) {
        return switch (scheduleUnit) {
            case THIRTY_MINUTES -> THIRTY_MINUTES_CAPACITY;
            case SIXTY_MINUTES -> SIXTY_MINUTES_CAPACITY;
        };
    }

    private int getTimeUnit(ScheduleUnit scheduleUnit) {
        return switch (scheduleUnit) {
            case THIRTY_MINUTES -> THIRTY_MINUTES;
            case SIXTY_MINUTES -> SIXTY_MINUTES;
        };
    }

    private void applyDailySchedule(List<DailySchedule> openSchedules, List<Boolean> availability,
        boolean open, int timeUnit) {
        for (DailySchedule schedule : openSchedules) {
            applySchedule(schedule.getStartTime(), schedule.getEndTime(), availability, timeUnit, open);
        }
    }

    private void applyBaseSchedule(BaseSchedule baseSchedule, List<Boolean> availability, int timeUnit) {
        if (baseSchedule != null) {
            applySchedule(baseSchedule.getStartTime(), baseSchedule.getEndTime(), availability, timeUnit,
                true);
        }
    }

    private void applySchedule(LocalTime startTime, LocalTime endTime, List<Boolean> availability, int scheduleUnit, boolean open) {
        int baseStartTimeIdx = (startTime.getHour() * SIXTY_MINUTES + startTime.getMinute()) / scheduleUnit;
        int baseEndTimeIdx = (endTime.getHour() * SIXTY_MINUTES + endTime.getMinute()) / scheduleUnit;

        for (int i=baseStartTimeIdx; i<baseEndTimeIdx; i++) {
            availability.set(i, open);
        }
    }

    private AvailableScheduleResponse sliceAvailableSchedule(List<Boolean> availability, int timeUnit,
        ScheduleUnit scheduleUnit) {

        int firstAvailableIndex = IntStream.range(0, availability.size())
            .filter(availability::get)
            .findFirst()
            .orElse(INVALID_INDEX);

        int lastAvailableIndex = IntStream.range(0, availability.size())
            .filter(availability::get)
            .reduce((first, second) -> second)
            .orElse(INVALID_INDEX);

        if (firstAvailableIndex != INVALID_INDEX) {
            return AvailableScheduleResponse.builder()
                .startTime(getLocalTime(firstAvailableIndex * timeUnit))
                .availableSchedule(availability.subList(firstAvailableIndex, lastAvailableIndex + 1))
                .scheduleUnit(scheduleUnit)
                .build();
        } else {
            return AvailableScheduleResponse.builder()
                .startTime(LocalTime.of(0,0))
                .availableSchedule(Collections.emptyList())
                .scheduleUnit(scheduleUnit)
                .build();
        }
    }

    private LocalTime getLocalTime(int minutes) {
        return LocalTime.of(minutes / SIXTY_MINUTES, minutes % SIXTY_MINUTES);
    }
}
