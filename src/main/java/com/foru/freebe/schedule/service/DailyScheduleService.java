package com.foru.freebe.schedule.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.ScheduleErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.schedule.dto.DailyScheduleAddResponse;
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

    public List<DailyScheduleResponse> getDailySchedules(Member photographer) {
        return dailyScheduleRepository.findByMember(photographer)
            .stream()
            .map(dailySchedule -> DailyScheduleResponse.builder()
                .scheduleId(dailySchedule.getId())
                .scheduleStatus(dailySchedule.getScheduleStatus())
                .date(dailySchedule.getDate())
                .startTime(dailySchedule.getStartTime())
                .endTime(dailySchedule.getEndTime())
                .build())
            .collect(Collectors.toList());
    }

    public DailyScheduleAddResponse addDailySchedule(Member photographer, DailyScheduleRequest request) {
        validScheduleOverlap(photographer, request);

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

        validScheduleOverlap(photographer, request);

        dailySchedule.updateScheduleStatus(request.getScheduleStatus());
        dailySchedule.updateDate(request.getDate());
        dailySchedule.updateStartTime(request.getStartTime());
        dailySchedule.updateEndTime(request.getEndTime());
    }

    public void deleteDailySchedule(Member photographer, Long scheduleId) {
        dailyScheduleRepository.delete(dailyScheduleRepository.findByMemberAndId(photographer, scheduleId)
            .orElseThrow(() -> new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_NOT_FOUND)));
    }

    private void validScheduleOverlap(Member member, DailyScheduleRequest request) {
        List<DailySchedule> overlappingSchedules = dailyScheduleRepository.findOverlappingSchedules(member,
            request.getDate(), request.getStartTime(), request.getEndTime());

        if (!overlappingSchedules.isEmpty()) {
            throw new RestApiException(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
        }
    }
}
