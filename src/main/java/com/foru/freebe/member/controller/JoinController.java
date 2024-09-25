package com.foru.freebe.member.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.dto.PhotographerJoinRequest;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JoinController {
    private final MemberService memberService;

    @PostMapping("/photographer/join")
    public ResponseEntity<ResponseBody<String>> joinPhotographer(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestBody PhotographerJoinRequest request) throws IOException {

        Member member = memberAdapter.getMember();
        String profileName = memberService.joinPhotographer(member, request);

        ResponseBody<String> responseBody = ResponseBody.<String>builder()
            .data(profileName)
            .message("Successfully joined")
            .build();

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(responseBody);
    }
}
