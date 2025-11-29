package com.foru.freebe.schedule.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.entity.ScheduleUnit;
import com.foru.freebe.schedule.dto.AvailableScheduleResponse;
import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.OperationStatus;
import com.foru.freebe.schedule.entity.ScheduleStatus;

@ExtendWith(MockitoExtension.class)
class ScheduleCalculatorTest {

    @InjectMocks
    private ScheduleCalculator scheduleCalculator;

    private Member photographer;
    private BaseSchedule baseSchedule;
    private List<DailySchedule> openSchedules;
    private List<DailySchedule> confirmedAndClosedSchedules;

    @BeforeEach
    void setUP() {
        photographer = Member
            .builder(1L, Role.PHOTOGRAPHER, "John Doe", "john@example.com",
                "01012341234")
            .scheduleUnit(ScheduleUnit.SIXTY_MINUTES)
            .build();

        baseSchedule = BaseSchedule.builder()
            .photographer(photographer)
            .dayOfWeek(DayOfWeek.MONDAY)
            .startTime(LocalTime.of(9,0))
            .endTime(LocalTime.of(17,0))
            .operationStatus(OperationStatus.ACTIVE)
            .build();

        DailySchedule dailySchedule1 = DailySchedule.builder()
            .member(photographer)
            .scheduleStatus(ScheduleStatus.CLOSED)
            .startTime(LocalTime.of(10, 0, 0))
            .endTime(LocalTime.of(11, 0, 0))
            .build();

        DailySchedule dailySchedule2 = DailySchedule.builder()
            .member(photographer)
            .scheduleStatus(ScheduleStatus.CONFIRMED)
            .startTime(LocalTime.of(14, 0, 0))
            .endTime(LocalTime.of(16, 0, 0))
            .build();

        confirmedAndClosedSchedules = List.of(dailySchedule1, dailySchedule2);
    }

    @Nested
    @DisplayName("예약 가능 스케줄 조회")
    class FindDailySchedule {
        @Test
        @DisplayName("기본 스케줄 사이에 예약 중지, 예약 확정 일정이 있는 시간을 제외하고 조회한다.")
        void getReservationAvailability_ShouldExcludeClosedOrConfirmedSchedules() {
            //given

            List<Boolean> expectedScheduleAvailability = List.of(
                true, false, true, true, true,
                false, false, true);

            AvailableScheduleResponse expectedResponse = AvailableScheduleResponse.builder()
                .startTime(LocalTime.of(9, 0))
                .availableSchedule(expectedScheduleAvailability)
                .scheduleUnit(ScheduleUnit.SIXTY_MINUTES)
                .build();

            //when
            AvailableScheduleResponse actualResponse = scheduleCalculator.calculateAvailableSchedule(
                baseSchedule, Collections.emptyList(), confirmedAndClosedSchedules, photographer.getScheduleUnit());

            //then
            assertEquals(expectedResponse.getAvailableSchedule(), actualResponse.getAvailableSchedule());
            assertEquals(expectedResponse.getScheduleUnit(), actualResponse.getScheduleUnit());
            assertEquals(expectedResponse.getStartTime(), actualResponse.getStartTime());
        }

        @Test
        @DisplayName("기본 스케줄 이전에 오픈 일정이 있는 경우, 오픈 일정을 포함하여 조회한다.")
        void getReservationAvailability_ShouldExcludeOpenSchedulesBeforeBaseSchedules() {
            //given
            DailySchedule openSchedule = DailySchedule.builder()
                .member(photographer)
                .scheduleStatus(ScheduleStatus.OPEN)
                .startTime(LocalTime.of(6, 0, 0))
                .endTime(LocalTime.of(7, 0, 0))
                .build();

            openSchedules = List.of(openSchedule);

            List<Boolean> expectedScheduleAvailability = List.of(
                true, false, false, true, false,
                true, true, true, false, false,
                true);

            AvailableScheduleResponse expectedResponse = AvailableScheduleResponse.builder()
                .startTime(LocalTime.of(6, 0))
                .availableSchedule(expectedScheduleAvailability)
                .scheduleUnit(ScheduleUnit.SIXTY_MINUTES)
                .build();

            //when
            AvailableScheduleResponse actualResponse = scheduleCalculator.calculateAvailableSchedule(
                baseSchedule, openSchedules, confirmedAndClosedSchedules, photographer.getScheduleUnit());

            //then
            assertEquals(expectedResponse.getAvailableSchedule(), actualResponse.getAvailableSchedule());
            assertEquals(expectedResponse.getScheduleUnit(), actualResponse.getScheduleUnit());
            assertEquals(expectedResponse.getStartTime(), actualResponse.getStartTime());
        }

        @Test
        @DisplayName("기본 스케줄 이후에 오픈 일정이 있는 경우, 오픈 일정을 포함하여 조회한다.")
        void getReservationAvailability_ShouldExcludeOpenSchedulesAfterBaseSchedules() {
            //given
            DailySchedule openSchedule = DailySchedule.builder()
                .member(photographer)
                .scheduleStatus(ScheduleStatus.OPEN)
                .startTime(LocalTime.of(19, 0, 0))
                .endTime(LocalTime.of(20, 0, 0))
                .build();

            openSchedules = List.of(openSchedule);

            List<Boolean> expectedScheduleAvailability = List.of(
                true, false, true, true, true,
                false, false, true, false, false,
                true);

            AvailableScheduleResponse expectedResponse = AvailableScheduleResponse.builder()
                .startTime(LocalTime.of(9, 0))
                .availableSchedule(expectedScheduleAvailability)
                .scheduleUnit(ScheduleUnit.SIXTY_MINUTES)
                .build();

            //when
            AvailableScheduleResponse actualResponse = scheduleCalculator.calculateAvailableSchedule(
                baseSchedule, openSchedules, confirmedAndClosedSchedules, photographer.getScheduleUnit());

            //then
            assertEquals(expectedResponse.getAvailableSchedule(), actualResponse.getAvailableSchedule());
            assertEquals(expectedResponse.getScheduleUnit(), actualResponse.getScheduleUnit());
            assertEquals(expectedResponse.getStartTime(), actualResponse.getStartTime());
        }
    }
}