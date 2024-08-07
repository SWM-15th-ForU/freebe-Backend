package com.foru.freebe.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.RoleTypeRequest;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.service.CustomUserDetails;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.service.MemberService;
import com.foru.freebe.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final ProfileService profileService;

    @PostMapping("/login/type")
    public ApiResponse<String> saveRoleType(@RequestBody RoleTypeRequest roleTypeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        KakaoUser kakaoUser = new KakaoUser(userDetails);

        memberService.updateMemberRole(kakaoUser.getName(), roleTypeRequest.getRole());
        String uniqueUrl = profileService.registerUniqueUrl(kakaoUser.getName());

        return ApiResponse.<String>builder()
            .status(200)
            .message("Successfully save roleType")
            .data(uniqueUrl)
            .build();
    }
}
