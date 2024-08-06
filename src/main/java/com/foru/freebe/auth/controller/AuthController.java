package com.foru.freebe.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.RoleTypeRequest;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.service.CustomUserDetails;
import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/login/type")
    public ApiResponseDto<Void> saveRoleType(@RequestBody RoleTypeRequest roleTypeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        KakaoUser kakaoUser = new KakaoUser(userDetails);
        memberService.updateMemberRole(kakaoUser, roleTypeRequest.getRole());

        return ApiResponseDto.<Void>builder()
            .status(HttpStatus.CREATED)
            .message("Successfully save roleType")
            .build();
    }
}
