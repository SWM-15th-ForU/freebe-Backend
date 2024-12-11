package com.foru.freebe.baseSchedule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.baseSchedule.dto.BaseScheduleDto;
import com.foru.freebe.baseSchedule.dto.ScheduleUnitDto;
import com.foru.freebe.baseSchedule.service.BaseScheduleService;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.dto.photographer.RegisteredProductResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class BaseScheduleController {

    private final BaseScheduleService baseScheduleService;

    @GetMapping("/schedule/base")
    public ResponseEntity<ResponseBody<List<BaseScheduleDto>>> getBaseSchedules(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member photographer = memberAdapter.getMember();
        List<BaseScheduleDto> responseData = baseScheduleService.getBaseSchedules(photographer.getId());

        ResponseBody<List<BaseScheduleDto>> responseBody = ResponseBody
            .<List<BaseScheduleDto>>builder()
            .message("Successfully retrieve basic schedules.")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/schedule/base")
    public ResponseEntity<ResponseBody<Void>> updateBaseSchedule(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestBody List<BaseScheduleDto> request) {

        Member photographer = memberAdapter.getMember();
        baseScheduleService.updateBaseSchedule(request, photographer.getId());

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Basic schedule successfully changed")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/schedule/unit")
    public ResponseEntity<ResponseBody<ScheduleUnitDto>> getScheduleUnit(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member photographer = memberAdapter.getMember();
        ScheduleUnitDto responseData = baseScheduleService.getScheduleUnit(photographer.getId());

        ResponseBody<ScheduleUnitDto> responseBody = ResponseBody
            .<ScheduleUnitDto>builder()
            .message("Successfully retrieve schedule unit.")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}
