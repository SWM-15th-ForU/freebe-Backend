package com.foru.freebe.schedule.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.ScheduleErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.schedule.dto.DailyScheduleAddResponse;
import com.foru.freebe.schedule.dto.DailyScheduleMonthlyRequest;
import com.foru.freebe.schedule.dto.DailyScheduleRequest;
import com.foru.freebe.schedule.dto.DailyScheduleResponse;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.repository.DailyScheduleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyScheduleService {
    private final DailyScheduleRepository dailyScheduleRepository;
    private final Clock clock;

    public List<DailyScheduleResponse> getDailySchedules(Member photographer, DailyScheduleMonthlyRequest request) {
        return dailyScheduleRepository.findByMember(photographer)
            .stream()
            .map(this::toDailyScheduleResponse)
            .filter(dailySchedule -> dailySchedule.getDate().getMonthValue() == request.getMonthValue())
            .collect(Collectors.toList());
    }

    public DailyScheduleAddResponse addDailySchedule(Member photographer, DailyScheduleRequest request) {
        validateScheduleInFuture(request);
        validateScheduleOverlap(photographer, request);

        DailySchedule dailySchedule = DailySchedule.builder()
            .member(photographer)
            .scheduleStatus(request.getScheduleStatus())
            .date(request.getDate())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .build();

        DailySchedule savedDailySchedule = dailyScheduleRepository.save(dailySchedule);
        return new DailyScheduleAddResponse(savedDailySchedule.getId());
    }

    public void updateDailySchedule(Member photographer, Long scheduleId, DailyScheduleRequest request) {
        DailySchedule dailySchedule = dailyScheduleRepository.findByMemberAndId(photographer, scheduleId)
            .orElseThrow(() -> new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_NOT_FOUND));

        validateScheduleInFuture(request);
        validateScheduleOverlap(photographer, request, scheduleId);

        dailySchedule.updateScheduleStatus(request.getScheduleStatus());
        dailySchedule.updateDate(request.getDate());
        dailySchedule.updateStartTime(request.getStartTime());
        dailySchedule.updateEndTime(request.getEndTime());
    }

    public void deleteDailySchedule(Member photographer, Long scheduleId) {
        dailyScheduleRepository.delete(dailyScheduleRepository.findByMemberAndId(photographer, scheduleId)
            .orElseThrow(() -> new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_NOT_FOUND)));
    }

    private void validateScheduleOverlap(Member member, DailyScheduleRequest request) {
        List<DailySchedule> overlappingSchedules = dailyScheduleRepository.findOverlappingSchedules(member,
            request.getDate(), request.getStartTime(), request.getEndTime());

        if (!overlappingSchedules.isEmpty()) {
            throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
        }
    }

    private void validateScheduleOverlap(Member member, DailyScheduleRequest request, Long scheduleId) {
        List<DailySchedule> overlappingSchedules = dailyScheduleRepository.findOverlappingSchedules(member,
            request.getDate(), request.getStartTime(), request.getEndTime());

        if (overlappingSchedules.size() == 1 && overlappingSchedules.get(0).getId().equals(scheduleId)) {
            return;
        }
        if (!overlappingSchedules.isEmpty()) {
            throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
        }
    }

    private void validateScheduleInFuture(DailyScheduleRequest request) {
        LocalDateTime requestDateTime = request.getDate().atTime(request.getStartTime());

        if (requestDateTime.isBefore(LocalDateTime.now(clock))) {
            throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_IN_PAST);
        }
    }

    private DailyScheduleResponse toDailyScheduleResponse(DailySchedule dailySchedule) {
        return DailyScheduleResponse.builder()
            .scheduleId(dailySchedule.getId())
            .scheduleStatus(dailySchedule.getScheduleStatus())
            .date(dailySchedule.getDate())
            .startTime(dailySchedule.getStartTime())
            .endTime(dailySchedule.getEndTime())
            .build();
    }
}
