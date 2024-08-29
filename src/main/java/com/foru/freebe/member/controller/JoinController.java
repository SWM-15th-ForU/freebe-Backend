package com.foru.freebe.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
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
    public ApiResponse<String> joinPhotographer(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestBody PhotographerJoinRequest request) {
        Member member = memberAdapter.getMember();
        return memberService.joinPhotographer(member, request);
    }
}
