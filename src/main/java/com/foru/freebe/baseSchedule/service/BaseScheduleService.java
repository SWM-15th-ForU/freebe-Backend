package com.foru.freebe.baseSchedule.service;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.foru.freebe.baseSchedule.dto.BaseScheduleDto;
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

    private final BaseScheduleRepository baseScheduleRepository;
    private final MemberRepository memberRepository;

    public void updateBaseSchedule(List<BaseScheduleDto> baseScheduleDtoList, Long photographerId) {
        Member photographer = getMember(photographerId);

        for (BaseScheduleDto baseScheduleDto : baseScheduleDtoList) {
            DayOfWeek dayOfWeek = baseScheduleDto.getDayOfWeek();
            DateTime startTime = baseScheduleDto.getStartTime();
            DateTime endTime = baseScheduleDto.getEndTime();

            validateScheduleTime(startTime, endTime);

            BaseSchedule baseSchedule = baseScheduleRepository.findByDayOfWeekAndPhotographerId(dayOfWeek,
                photographer.getId());

            baseSchedule.updateScheduleTime(startTime, endTime);
        }
    }

    private void validateScheduleTime(DateTime startTime, DateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new RestApiException(ScheduleErrorCode.INCORRECT_TIME);
        }
    }

    private Member getMember(Long photographerId) {
        return memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
