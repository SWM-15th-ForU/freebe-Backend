package com.foru.freebe.schedule;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foru.freebe.errors.errorcode.ScheduleErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.schedule.dto.DailyScheduleAddResponse;
import com.foru.freebe.schedule.dto.DailyScheduleMonthlyRequest;
import com.foru.freebe.schedule.dto.DailyScheduleRequest;
import com.foru.freebe.schedule.dto.DailyScheduleResponse;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.ScheduleStatus;
import com.foru.freebe.schedule.repository.DailyScheduleRepository;
import com.foru.freebe.schedule.service.DailyScheduleService;

@ExtendWith(MockitoExtension.class)
public class DailyScheduleServiceTest {
    @Mock
    private DailyScheduleRepository dailyScheduleRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private DailyScheduleService dailyScheduleService;

    private Member photographer;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2024-12-05T10:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        given(clock.instant()).willReturn(fixedInstant);
        given(clock.getZone()).willReturn(zoneId);

        now = LocalDateTime.now(clock);
        photographer = Member.builder(1L, Role.PHOTOGRAPHER, "tester", "test@email", "010-0000-0000").build();
    }

    @Nested
    @DisplayName("날짜별 스케줄 조회 테스트")
    class FindDailySchedule {
        @Test
        @DisplayName("날짜별 스케줄 조회 시 월별 데이터를 불러온다")
        void shouldFetchSchedulesAfterCurrentDate() {
            // given
            List<DailySchedule> dailySchedules = new ArrayList<>();
            DailySchedule dailySchedule1 = DailySchedule.builder()
                .member(photographer)
                .scheduleStatus(ScheduleStatus.OPEN)
                .date(now.toLocalDate())
                .startTime(LocalTime.of(10, 0, 0))
                .endTime(LocalTime.of(11, 0, 0))
                .build();
            DailySchedule dailySchedule2 = DailySchedule.builder()
                .member(photographer)
                .scheduleStatus(ScheduleStatus.OPEN)
                .date(now.toLocalDate().plusMonths(1L))
                .startTime(now.toLocalTime().minusSeconds(1L))
                .endTime(now.toLocalTime())
                .build();
            dailySchedules.add(dailySchedule1);
            dailySchedules.add(dailySchedule2);

            DailyScheduleMonthlyRequest request = new DailyScheduleMonthlyRequest(
                now.toLocalDate().getYear(), now.toLocalDate().getMonthValue());

            given(dailyScheduleRepository.findByMember(photographer)).willReturn(dailySchedules);

            // when
            List<DailyScheduleResponse> responses = dailyScheduleService.getDailySchedules(photographer, request);

            // then
            assertThat(responses).size().isEqualTo(1);
            assertThat(responses.get(0).getDate()).isEqualTo(now.toLocalDate());
        }
    }

    @Nested
    @DisplayName("날짜별 스케줄 추가 테스트")
    class AddDailySchedule {
        @Test
        @DisplayName("현시점 이전의 스케줄은 등록할 수 없다")
        void shouldNotAllowSchedulesInThePast() {
            // given
            DailyScheduleRequest request = DailyScheduleRequest.builder()
                .scheduleStatus(ScheduleStatus.OPEN)
                .date(now.toLocalDate())
                .startTime(now.toLocalTime().minusHours(1L))
                .endTime(now.toLocalTime().plusHours(1L))
                .build();

            //when & then
            RestApiException exception = assertThrows(RestApiException.class, () -> {
                dailyScheduleService.addDailySchedule(photographer, request);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ScheduleErrorCode.DAILY_SCHEDULE_IN_PAST);
        }

        @Test
        @DisplayName("예약오픈, 예약중지 간 중복되는 스케줄이 있을 때 예외가 발생한다")
        void shouldThrowExceptionWhenSchedulesOverlap() {
            // given
            DailyScheduleRequest request = DailyScheduleRequest.builder()
                .scheduleStatus(ScheduleStatus.OPEN)
                .date(now.toLocalDate())
                .startTime(now.toLocalTime().plusHours(1))
                .endTime(now.toLocalTime().plusHours(2))
                .build();

            List<DailySchedule> overlappingSchedules = new ArrayList<>();
            overlappingSchedules.add(DailySchedule.builder()
                .member(photographer)
                .scheduleStatus(ScheduleStatus.CLOSED)
                .date(now.toLocalDate())
                .startTime(now.toLocalTime().plusHours(1).plusMinutes(30))
                .endTime(now.toLocalTime().plusHours(3))
                .build());

            given(dailyScheduleRepository.findConflictingSchedulesByStatuses(photographer, request.getDate(),
                request.getStartTime(), request.getEndTime(), List.of(ScheduleStatus.OPEN, ScheduleStatus.CLOSED)))
                .willReturn(overlappingSchedules);

            // when & then
            RestApiException exception = assertThrows(RestApiException.class, () -> {
                dailyScheduleService.addDailySchedule(photographer, request);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
        }

        @Test
        @DisplayName("중복되는 스케줄이 없으면 예외가 발생하지 않는다")
        void shouldNotThrowExceptionWhenNoOverlappingSchedules() {
            // given
            DailyScheduleRequest request = DailyScheduleRequest.builder()
                .scheduleStatus(ScheduleStatus.OPEN)
                .date(now.toLocalDate())
                .startTime(now.toLocalTime().plusHours(1))
                .endTime(now.toLocalTime().plusHours(2))
                .build();

            List<DailySchedule> overlappingSchedules = new ArrayList<>();

            given(dailyScheduleRepository.findConflictingSchedulesByStatuses(photographer, request.getDate(),
                request.getStartTime(), request.getEndTime(), List.of(ScheduleStatus.OPEN, ScheduleStatus.CLOSED)))
                .willReturn(overlappingSchedules);

            given(dailyScheduleRepository.save(any(DailySchedule.class))).willReturn(
                DailySchedule.builder()
                    .member(photographer)
                    .scheduleStatus(ScheduleStatus.OPEN)
                    .date(now.toLocalDate())
                    .startTime(now.toLocalTime().plusHours(1))
                    .endTime(now.toLocalTime().plusHours(2))
                    .build()
            );

            // when
            DailyScheduleAddResponse response = dailyScheduleService.addDailySchedule(photographer, request);

            // then
            assertThat(response).isNotNull();
        }
    }
}
