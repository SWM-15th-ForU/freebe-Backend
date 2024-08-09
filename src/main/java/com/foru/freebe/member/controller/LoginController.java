package com.foru.freebe.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.RoleTypeRequest;
import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.service.MemberService;
import com.foru.freebe.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final ProfileService profileService;

    @PostMapping("/login/type")
    public ApiResponse<String> saveRoleType(@RequestBody RoleTypeRequest roleTypeRequest,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member member = memberAdapter.getMember();
        memberService.assignMemberRole(member.getId(), roleTypeRequest.getRole());
        String uniqueUrl = profileService.registerUniqueUrl(member.getId());

        return ApiResponse.<String>builder()
            .status(200)
            .message("Successfully save roleType")
            .data(uniqueUrl)
            .build();
    }
}
