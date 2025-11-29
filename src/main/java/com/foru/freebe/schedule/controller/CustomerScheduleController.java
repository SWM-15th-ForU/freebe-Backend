package com.foru.freebe.schedule.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.schedule.dto.AvailableScheduleResponse;
import com.foru.freebe.schedule.service.CustomerScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerScheduleController {

    private final CustomerScheduleService customerScheduleService;

    @GetMapping("/schedule/{profileName}")
    public ResponseEntity<ResponseBody<AvailableScheduleResponse>> getBaseSchedule(
        @PathVariable("profileName") String profileName, @RequestParam LocalDate viewDate) {

        AvailableScheduleResponse responseData =
            customerScheduleService.getAvailableSchedule(profileName, viewDate);

        ResponseBody<AvailableScheduleResponse> responseBody = ResponseBody
            .<AvailableScheduleResponse>builder()
            .message("Successfully retrieve available reservation time")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

}
