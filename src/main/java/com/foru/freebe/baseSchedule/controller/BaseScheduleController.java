package com.foru.freebe.baseSchedule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.baseSchedule.dto.BaseScheduleDto;
import com.foru.freebe.baseSchedule.service.BaseScheduleService;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class BaseScheduleController {

    private final BaseScheduleService baseScheduleService;

    @PutMapping("/schedule/update")
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
}
