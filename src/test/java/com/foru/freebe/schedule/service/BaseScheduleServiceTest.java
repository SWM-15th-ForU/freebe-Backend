package com.foru.freebe.schedule.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.schedule.dto.BaseScheduleDto;
import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.DayOfWeek;
import com.foru.freebe.schedule.entity.OperationStatus;
import com.foru.freebe.schedule.repository.BaseScheduleRepository;

class BaseScheduleServiceTest {

    @Mock
    private BaseScheduleRepository baseScheduleRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BaseScheduleService baseScheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("기본 단일스케줄 시간 변경")
    @Test
    void updateScheduleForTime() {
        // given
        Member photographer = createNewMember();

        BaseSchedule existingBaseSchedule = new BaseSchedule(photographer, DayOfWeek.FRIDAY, LocalTime.of(9, 0, 0),
            LocalTime.of(15, 0, 0), OperationStatus.ACTIVE);

        BaseScheduleDto baseScheduleDto = new BaseScheduleDto().builder()
            .dayOfWeek(DayOfWeek.FRIDAY)
            .startTime(LocalTime.of(9, 0, 0))
            .endTime(LocalTime.of(21, 0, 0))
            .operationStatus(OperationStatus.ACTIVE)
            .build();

        when(baseScheduleRepository.findByDayOfWeekAndPhotographerId(baseScheduleDto.getDayOfWeek(), photographer.getId())).thenReturn(existingBaseSchedule);

        //when
        baseScheduleService.updateSchedule(baseScheduleDto, photographer);

        // then
        Assertions.assertEquals(LocalTime.of(9, 0), existingBaseSchedule.getStartTime());
        Assertions.assertEquals(LocalTime.of(21, 0), existingBaseSchedule.getEndTime());
        Assertions.assertEquals(OperationStatus.ACTIVE, existingBaseSchedule.getOperationStatus());

        verify(baseScheduleRepository, times(1)).findByDayOfWeekAndPhotographerId(baseScheduleDto.getDayOfWeek(), photographer.getId());
    }

    @DisplayName("기본 단일스케줄 활성화 변경")
    @Test
    void updateScheduleForActivation() {
        // when
        Member photographer = createNewMember();

        BaseSchedule existingBaseSchedule = new BaseSchedule(photographer, DayOfWeek.FRIDAY, LocalTime.of(9, 0, 0),
            LocalTime.of(15, 0, 0), OperationStatus.ACTIVE);

        BaseScheduleDto baseScheduleDto = new BaseScheduleDto().builder()
            .dayOfWeek(DayOfWeek.FRIDAY)
            .startTime(LocalTime.of(9, 0, 0))
            .endTime(LocalTime.of(15, 0, 0))
            .operationStatus(OperationStatus.INACTIVE)
            .build();

        when(baseScheduleRepository.findByDayOfWeekAndPhotographerId(baseScheduleDto.getDayOfWeek(), photographer.getId())).thenReturn(existingBaseSchedule);

        // when
        baseScheduleService.updateSchedule(baseScheduleDto, photographer);

        // then
        Assertions.assertEquals(OperationStatus.INACTIVE, existingBaseSchedule.getOperationStatus());
        Assertions.assertEquals(LocalTime.of(9, 0), existingBaseSchedule.getStartTime());
        Assertions.assertEquals(LocalTime.of(18, 0), existingBaseSchedule.getEndTime());

        verify(baseScheduleRepository, times(1)).findByDayOfWeekAndPhotographerId(baseScheduleDto.getDayOfWeek(), photographer.getId());
    }

    private Member createNewMember() {
        return new Member(1L, Role.PHOTOGRAPHER, "John Doe", "john@example.com",
            "1234567890", "1980", "0724", "Male", "johndoe");
    }

    private BaseScheduleDto createBaseScheduleDto(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, OperationStatus operationStatus) {
        return BaseScheduleDto.builder()
            .dayOfWeek(dayOfWeek)
            .startTime(startTime)
            .endTime(endTime)
            .operationStatus(operationStatus)
            .build();
    }




}