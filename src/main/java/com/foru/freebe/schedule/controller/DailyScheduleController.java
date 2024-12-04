package com.foru.freebe.schedule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.schedule.dto.DailyScheduleAddResponse;
import com.foru.freebe.schedule.dto.DailyScheduleRequest;
import com.foru.freebe.schedule.dto.DailyScheduleResponse;
import com.foru.freebe.schedule.service.DailyScheduleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer/schedule/daily")
public class DailyScheduleController {
    private final DailyScheduleService dailyScheduleService;

    @GetMapping("/")
    public ResponseEntity<ResponseBody<List<DailyScheduleResponse>>> getDailySchedules(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member photographer = memberAdapter.getMember();
        List<DailyScheduleResponse> responses = dailyScheduleService.getDailySchedules(photographer);

        ResponseBody<List<DailyScheduleResponse>> responseBody = ResponseBody.<List<DailyScheduleResponse>>builder()
            .message("Successfully get daily schedules")
            .data(responses)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PostMapping("/")
    public ResponseEntity<ResponseBody<DailyScheduleAddResponse>> addDailySchedule(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @Valid @RequestBody DailyScheduleRequest request) {

        Member photographer = memberAdapter.getMember();
        DailyScheduleAddResponse response = dailyScheduleService.addDailySchedule(photographer, request);

        ResponseBody<DailyScheduleAddResponse> responseBody = ResponseBody.<DailyScheduleAddResponse>builder()
            .message("Successfully add daily schedule")
            .data(response)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ResponseBody<Void>> updateDailySchedule(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Positive @PathVariable("scheduleId") Long scheduleId, @Valid @RequestBody DailyScheduleRequest request) {

        Member photographer = memberAdapter.getMember();
        dailyScheduleService.updateDailySchedule(photographer, scheduleId, request);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully update daily schedule")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ResponseBody<Void>> deleteDailySchedule(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @Positive @PathVariable("scheduleId") Long scheduleId) {

        Member photographer = memberAdapter.getMember();
        dailyScheduleService.deleteDailySchedule(photographer, scheduleId);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully delete daily schedule")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT.value())
            .body(responseBody);
    }
}
