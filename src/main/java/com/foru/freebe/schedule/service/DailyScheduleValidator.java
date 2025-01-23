package com.foru.freebe.schedule.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.ScheduleErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.ScheduleUnit;
import com.foru.freebe.schedule.dto.DailyScheduleRequest;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.ScheduleStatus;
import com.foru.freebe.schedule.repository.DailyScheduleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyScheduleValidator {
    private final Clock clock;
    private final DailyScheduleRepository dailyScheduleRepository;

    public void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RestApiException(ScheduleErrorCode.START_TIME_AFTER_END_TIME);
        }
    }

    public void validateScheduleUnit(ScheduleUnit scheduleUnit, LocalTime startTime, LocalTime endTime) {
        switch (scheduleUnit) {
            case SIXTY_MINUTES -> {
                if (startTime.getMinute() != 0 || endTime.getMinute() != 0) {
                    throw new RestApiException(ScheduleErrorCode.INVALID_SCHEDULE_UNIT);
                }
            }
            case THIRTY_MINUTES -> {
                if (startTime.getMinute() != 0 && startTime.getMinute() != 30) {
                    throw new RestApiException(ScheduleErrorCode.INVALID_SCHEDULE_UNIT);
                } else if (endTime.getMinute() != 0 && endTime.getMinute() != 30) {
                    throw new RestApiException(ScheduleErrorCode.INVALID_SCHEDULE_UNIT);
                }
            }
        }
    }

    public void validateScheduleInFuture(DailyScheduleRequest request) {
        LocalDateTime requestDateTime = request.getDate().atTime(request.getStartTime());

        if (requestDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_IN_PAST);
        }
    }

    public void validateConflictingSchedules(Member member, DailyScheduleRequest request) {
        List<ScheduleStatus> scheduleStatuses = determineConflictingStatuses(request.getScheduleStatus());

        List<DailySchedule> overlappingSchedules = dailyScheduleRepository.findConflictingSchedulesByStatuses(member,
            request.getDate(), request.getStartTime(), request.getEndTime(), scheduleStatuses);

        if (!overlappingSchedules.isEmpty()) {
            throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
        }
    }

    public void validateConflictingSchedules(Member member, DailyScheduleRequest request, Long scheduleId) {
        List<ScheduleStatus> scheduleStatuses = determineConflictingStatuses(request.getScheduleStatus());

        List<DailySchedule> conflictingSchedules = dailyScheduleRepository.findConflictingSchedulesByStatuses(member,
            request.getDate(), request.getStartTime(), request.getEndTime(), scheduleStatuses);

        if (conflictingSchedules.isEmpty() || isSelfConflictOnly(scheduleId, conflictingSchedules)) {
            return;
        }

        throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
    }

    private List<ScheduleStatus> determineConflictingStatuses(ScheduleStatus scheduleStatus) {
        if (scheduleStatus == ScheduleStatus.CONFIRMED) {
            return List.of(ScheduleStatus.CONFIRMED);
        } else {
            return List.of(ScheduleStatus.OPEN, ScheduleStatus.CLOSED);
        }
    }

    private boolean isSelfConflictOnly(Long scheduleId, List<DailySchedule> conflictingSchedules) {
        return conflictingSchedules.size() == 1 && conflictingSchedules.get(0).getId().equals(scheduleId);
    }
}
