package com.foru.freebe.schedule.service;

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
import com.foru.freebe.member.entity.ScheduleUnit;
import com.foru.freebe.schedule.dto.DailyScheduleRequest;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.ScheduleStatus;
import com.foru.freebe.schedule.repository.DailyScheduleRepository;

@ExtendWith(MockitoExtension.class)
public class DailyScheduleValidatorTest {
    @Mock
    private DailyScheduleRepository dailyScheduleRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private DailyScheduleValidator dailyScheduleValidator;

    private Member photographer;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2025-01-22T10:00:00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        given(clock.instant()).willReturn(fixedInstant);
        given(clock.getZone()).willReturn(zoneId);

        now = LocalDateTime.now(clock);
        photographer = Member
            .builder(1L, Role.PHOTOGRAPHER, "tester", "test@email", "010-0000-0000")
            .build();
        photographer.initializeScheduleUnit();
    }

    @Nested
    @DisplayName("날짜별 스케줄과 기본 스케줄의 단위 일치성 보장 테스트")
    class ValidateDailyScheduleUnit {
        @Test
        @DisplayName("기본 스케줄 단위가 60분일 때, 시작시간과 종료시간이 정시가 아니면 예외가 발생한다")
        void shouldThrowExceptionForInvalidTime() {
            // given
            ScheduleUnit basicScheduleUnit = ScheduleUnit.SIXTY_MINUTES;
            LocalTime invalidStartTime = LocalTime.parse("10:30:00");
            LocalTime invalidEndTime = LocalTime.parse("11:30:00");

            // when & then
            RestApiException exception = assertThrows(RestApiException.class, () -> {
                dailyScheduleValidator.validateScheduleUnit(basicScheduleUnit, invalidStartTime, invalidEndTime);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ScheduleErrorCode.INVALID_SCHEDULE_UNIT);
        }

        @Test
        @DisplayName("기본 스케줄 단위와 날짜별 스케줄 단위가 일치하지 않으면 예외가 발생한다")
        void shouldThrowExceptionForInvalidUnit() {
            // given
            ScheduleUnit basicScheduleUnit = ScheduleUnit.SIXTY_MINUTES;
            LocalTime invalidStartTime = LocalTime.parse("10:00:00");
            LocalTime invalidEndTime = LocalTime.parse("11:30:00");

            // when & then
            RestApiException exception = assertThrows(RestApiException.class, () -> {
                dailyScheduleValidator.validateScheduleUnit(basicScheduleUnit, invalidStartTime, invalidEndTime);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ScheduleErrorCode.INVALID_SCHEDULE_UNIT);
        }
    }

    @Nested
    @DisplayName("날짜별 스케줄 요청 객체의 유효성 검증 테스트")
    class ValidateDailyScheduleRequestDTO {
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
                dailyScheduleValidator.validateScheduleStartInFuture(request);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ScheduleErrorCode.DAILY_SCHEDULE_IN_PAST);
        }
    }

    @Nested
    @DisplayName("날짜별 스케줄 간 충돌 테스트")
    class ValidateDailyScheduleConflict {
        @Test
        @DisplayName("예약오픈, 예약중지 간 중복되는 스케줄이 있을 때 예외가 발생한다")
        void shouldThrowExceptionWhenSchedulesConflict() {
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
                dailyScheduleValidator.validateConflictingSchedules(photographer, request);
            });

            assertThat(exception.getErrorCode()).isEqualTo(ScheduleErrorCode.DAILY_SCHEDULE_OVERLAP);
        }

        @Test
        @DisplayName("중복되는 스케줄이 없으면 예외가 발생하지 않는다")
        void shouldNotThrowExceptionWhenNoConflictingSchedules() {
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

            // when & then
            assertDoesNotThrow(() -> dailyScheduleValidator.validateConflictingSchedules(photographer, request));
        }
    }
}
