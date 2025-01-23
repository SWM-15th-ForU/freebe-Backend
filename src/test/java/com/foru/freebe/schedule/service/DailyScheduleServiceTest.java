package com.foru.freebe.schedule.service;

import static org.assertj.core.api.Assertions.*;
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

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.schedule.dto.DailyScheduleMonthlyRequest;
import com.foru.freebe.schedule.dto.DailyScheduleResponse;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.ScheduleStatus;
import com.foru.freebe.schedule.repository.DailyScheduleRepository;

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
        photographer = Member
            .builder(1L, Role.PHOTOGRAPHER, "tester", "test@email", "010-0000-0000")
            .build();
        photographer.initializeScheduleUnit();
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
}
