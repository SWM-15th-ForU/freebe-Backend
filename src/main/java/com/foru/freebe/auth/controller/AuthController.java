package com.foru.freebe.auth.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.RoleTypeRequest;
import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.service.MemberService;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final ProfileService profileService;
    private final JwtService jwtService;

    @PostMapping("/login/type")
    public ApiResponse<String> saveRoleType(@RequestBody RoleTypeRequest roleTypeRequest,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member member = memberAdapter.getMember();
        memberService.updateMemberRole(member.getId(), roleTypeRequest.getRole());
        String uniqueUrl = profileService.registerUniqueUrl(member.getId());

        return ApiResponse.<String>builder()
            .status(200)
            .message("Successfully save roleType")
            .data(uniqueUrl)
            .build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("refreshToken");

        JwtTokenModel token = jwtService.reissueRefreshToken(refreshToken);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());

        return new ResponseEntity<>(headers, HttpStatusCode.valueOf(200));
    }
}
