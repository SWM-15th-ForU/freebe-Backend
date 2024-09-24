package com.foru.freebe.member.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.dto.PhotographerJoinRequest;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JoinController {
    private final MemberService memberService;

    @PostMapping("/photographer/join")
    public ResponseEntity<ResponseBody<String>> joinPhotographer(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestPart(value = "request") PhotographerJoinRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        Member member = memberAdapter.getMember();
        String uniqueUrl = memberService.joinPhotographer(member, request, image);

        ResponseBody<String> responseBody = ResponseBody.<String>builder()
            .data(uniqueUrl)
            .message("Successfully joined")
            .build();

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(responseBody);
    }
}
