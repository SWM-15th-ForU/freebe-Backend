package com.foru.freebe.baseSchedule.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.baseSchedule.dto.BaseScheduleDto;
import com.foru.freebe.baseSchedule.dto.ScheduleUnitDto;
import com.foru.freebe.baseSchedule.entity.BaseSchedule;
import com.foru.freebe.baseSchedule.entity.DayOfWeek;
import com.foru.freebe.baseSchedule.repository.BaseScheduleRepository;
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

        List<BaseScheduleDto> baseScheduleDtoList = new ArrayList<>();
        for (BaseSchedule baseSchedule : baseSchedules) {
            BaseScheduleDto baseScheduleDto = BaseScheduleDto.builder()
                .dayOfWeek(baseSchedule.getDayOfWeek())
                .startTime(baseSchedule.getStartTime())
                .endTime(baseSchedule.getEndTime())
                .build();

            baseScheduleDtoList.add(baseScheduleDto);
        }
        return baseScheduleDtoList;
    }

    public void updateBaseSchedule(List<BaseScheduleDto> baseScheduleDtoList, Long photographerId) {
        Member photographer = getMember(photographerId);

        for (BaseScheduleDto baseScheduleDto : baseScheduleDtoList) {
            DayOfWeek dayOfWeek = baseScheduleDto.getDayOfWeek();
            LocalTime startTime = baseScheduleDto.getStartTime();
            LocalTime endTime = baseScheduleDto.getEndTime();

            validateScheduleTime(startTime, endTime);

            BaseSchedule baseSchedule = baseScheduleRepository.findByDayOfWeekAndPhotographerId(dayOfWeek,
                photographer.getId());

            baseSchedule.updateScheduleTime(startTime, endTime);
        }
    }

    public void createDefaultSchedule(Member photographer) {
        for(DayOfWeek dayOfWeek : DayOfWeek.values()) {
            BaseSchedule baseSchedule = BaseSchedule.builder()
                .photographer(photographer)
                .dayOfWeek(dayOfWeek)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
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

    private void validateScheduleTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new RestApiException(ScheduleErrorCode.START_TIME_AFTER_END_TIME);
        }
    }

    private Member getMember(Long photographerId) {
        return memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
