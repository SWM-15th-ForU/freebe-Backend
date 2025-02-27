package com.foru.freebe.schedule.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.ProfileErrorCode;
import com.foru.freebe.errors.errorcode.ScheduleErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.schedule.dto.AvailableScheduleResponse;
import com.foru.freebe.schedule.entity.BaseSchedule;
import com.foru.freebe.schedule.entity.DailySchedule;
import com.foru.freebe.schedule.entity.OperationStatus;
import com.foru.freebe.schedule.entity.ScheduleStatus;
import com.foru.freebe.schedule.repository.BaseScheduleRepository;
import com.foru.freebe.schedule.repository.DailyScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerScheduleService {

    private final ProfileRepository profileRepository;
    private final BaseScheduleRepository baseScheduleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final ScheduleCalculator scheduleCalculator;

    public AvailableScheduleResponse getAvailableSchedule(String profileName, LocalDate viewDate) {
        Member photographer = getMemberFromProfileName(profileName);

        validateViewDateInPast(viewDate);

        BaseSchedule baseSchedule = baseScheduleRepository.findByOperationStatusAndDayOfWeekAndPhotographer(
            OperationStatus.ACTIVE, viewDate.getDayOfWeek(), photographer).orElse(null);

        List<DailySchedule> openSchedules = dailyScheduleRepository.findByMemberAndStatusesOrderByStartTime(viewDate,
            photographer, List.of(ScheduleStatus.OPEN));

        List<DailySchedule> confirmedOrClosedSchedules =
            dailyScheduleRepository.findByMemberAndStatusesOrderByStartTime(viewDate, photographer,
                List.of(ScheduleStatus.CLOSED, ScheduleStatus.CONFIRMED));

        return scheduleCalculator.calculateReservationSchedule(
            baseSchedule, openSchedules, confirmedOrClosedSchedules, photographer.getScheduleUnit());

    }

    private static void validateViewDateInPast(LocalDate viewDate) {
        if (viewDate.isBefore(LocalDate.now())) {
            throw new RestApiException(ScheduleErrorCode.VIEW_DATE_IN_PAST);
        }
    }

    private Member getMemberFromProfileName(String profileName) {
        Profile photographerProfile = profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.PROFILE_NOT_FOUND));

        return photographerProfile.getMember();
    }
}
