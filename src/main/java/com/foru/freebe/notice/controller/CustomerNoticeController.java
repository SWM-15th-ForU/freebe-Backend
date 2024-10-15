package com.foru.freebe.notice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.notice.dto.NoticeDto;
import com.foru.freebe.notice.service.CustomerNoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerNoticeController {

    private final CustomerNoticeService customerNoticeService;

    @GetMapping("/notice/{profileName}")
    public ResponseEntity<ResponseBody<List<NoticeDto>>> getNotices(
        @PathVariable("profileName") String profileName) {

        List<NoticeDto> responseData = customerNoticeService.getNotices(profileName);

        ResponseBody<List<NoticeDto>> responseBody = ResponseBody.<List<NoticeDto>>builder()
            .message("Data successfully loaded")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}
