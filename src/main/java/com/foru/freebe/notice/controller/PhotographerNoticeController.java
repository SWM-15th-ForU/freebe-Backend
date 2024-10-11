package com.foru.freebe.notice.controller;

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
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.notice.dto.NoticeDto;
import com.foru.freebe.notice.service.PhotographerNoticeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerNoticeController {
    private final PhotographerNoticeService noticeService;

    @PutMapping("/notice")
    public ResponseEntity<ResponseBody<Void>> updateNotice(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestBody List<NoticeDto> request) {

        Member photographer = memberAdapter.getMember();
        noticeService.updateNotice(photographer.getId(), request);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Updated successfully")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/notice")
    public ResponseEntity<ResponseBody<List<NoticeDto>>> getNotices(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member photographer = memberAdapter.getMember();
        List<NoticeDto> responseData = noticeService.getNotices(photographer.getId());

        ResponseBody<List<NoticeDto>> responseBody = ResponseBody.<List<NoticeDto>>builder()
            .message("Updated successfully")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}
