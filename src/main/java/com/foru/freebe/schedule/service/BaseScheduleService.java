package com.foru.freebe.schedule.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foru.freebe.schedule.dto.BaseScheduleDto;
import com.foru.freebe.schedule.dto.ScheduleUnitDto;
import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.DayOfWeek;
import com.foru.freebe.schedule.entity.OperationStatus;
import com.foru.freebe.schedule.repository.BaseScheduleRepository;
import com.foru.freebe.errors.errorcode.MemberErrorCode;
import com.foru.freebe.errors.errorcode.ScheduleErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BaseScheduleService {
    public static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0, 0);
    public static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0, 0);

    private final BaseScheduleRepository baseScheduleRepository;
    private final MemberRepository memberRepository;

    public List<BaseScheduleDto> getBaseSchedules(Long photographerId) {
        Member photographer = getMember(photographerId);

        List<BaseSchedule> baseSchedules = baseScheduleRepository.findByPhotographerId(photographer.getId());

        return convertBaseScheduleDto(baseSchedules);
    }

    @Transactional
    public void updateBaseSchedule(List<BaseScheduleDto> baseScheduleDtoList, Long photographerId) {
        Member photographer = getMember(photographerId);

        for (BaseScheduleDto baseScheduleDto : baseScheduleDtoList) {
            updateSchedule(baseScheduleDto, photographer);
        }
    }

    public void updateSchedule(BaseScheduleDto baseScheduleDto, Member photographer) {
        DayOfWeek dayOfWeek = baseScheduleDto.getDayOfWeek();
        LocalTime startTime = baseScheduleDto.getStartTime();
        LocalTime endTime = baseScheduleDto.getEndTime();

        validateScheduleTime(startTime, endTime);

        BaseSchedule baseSchedule = baseScheduleRepository.findByDayOfWeekAndPhotographerId(dayOfWeek,
            photographer.getId());

        baseSchedule.updateScheduleTime(startTime, endTime, baseScheduleDto.getOperationStatus());
    }

    public void createDefaultSchedule(Member photographer) {
        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            BaseSchedule baseSchedule = BaseSchedule.builder()
                .photographer(photographer)
                .dayOfWeek(dayOfWeek)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .operationStatus(OperationStatus.ACTIVE)
                .build();

            baseScheduleRepository.save(baseSchedule);
        }
    }

    public void deleteBaseSchedule(Member photographer) {
        List<BaseSchedule> baseSchedules = baseScheduleRepository.findByPhotographerId(photographer.getId());
        baseScheduleRepository.deleteAll(baseSchedules);
    }

    public ScheduleUnitDto getScheduleUnit(Long photographerId) {
        Member photographer = getMember(photographerId);
        return new ScheduleUnitDto(photographer.getScheduleUnit());
    }

    @Transactional
    public void updateScheduleUnit(Long photographerId, ScheduleUnitDto scheduleUnitDto) {
        Member photographer = getMember(photographerId);

        if (photographer.getScheduleUnit() == scheduleUnitDto.getScheduleUnit()) {
            throw new RestApiException(ScheduleErrorCode.CANNOT_CHANGE_SAME_SCHEDULE_UNIT);
        }

        initializeBaseSchedule(photographer);
        photographer.updateScheduleUnit(scheduleUnitDto.getScheduleUnit());
    }

    private List<BaseScheduleDto> convertBaseScheduleDto(List<BaseSchedule> baseSchedules) {

        return baseSchedules.stream()
            .map(baseSchedule -> BaseScheduleDto.builder()
                .dayOfWeek(baseSchedule.getDayOfWeek())
                .startTime(baseSchedule.getStartTime())
                .endTime(baseSchedule.getEndTime())
                .operationStatus(baseSchedule.getOperationStatus())
                .build())
            .collect(Collectors.toList());
    }

    private void validateScheduleTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new RestApiException(ScheduleErrorCode.START_TIME_AFTER_END_TIME);
        }
    }

    private Member getMember(Long photographerId) {
        return memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void initializeBaseSchedule(Member photographer) {
        List<BaseSchedule> baseScheduleList = baseScheduleRepository.findByPhotographerId(photographer.getId());

        for (BaseSchedule baseSchedule : baseScheduleList) {
            baseSchedule.initializeSchedule();
        }
    }
}
