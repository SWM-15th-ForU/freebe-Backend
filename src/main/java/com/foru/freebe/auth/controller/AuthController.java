package com.foru.freebe.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.LoginRequest;
import com.foru.freebe.auth.model.KakaoToken;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.service.AuthService;
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
    private final JwtService jwtService;
    private final AuthService authService;
    private final MemberService memberService;
    private final ProfileService profileService;

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("refreshToken");

        JwtTokenModel token = jwtService.reissueRefreshToken(refreshToken);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());

        return new ResponseEntity<>(headers, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        KakaoToken kakaoToken = authService.getToken(loginRequest.getCode());
        KakaoUser kakaoUser = authService.getUserInfo(kakaoToken, loginRequest.getRoleType());

        Member member = memberService.findOrRegisterMember(kakaoUser, loginRequest.getRole());
        String uniqueUrl = profileService.getUniqueUrl(member.getId());

        JwtTokenModel token = jwtService.generateToken(member.getId());
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("accessToken", token.getAccessToken());
        headers.add("refreshToken", token.getRefreshToken());

        ApiResponse<String> body = ApiResponse.<String>builder()
            .status(200)
            .message("photographer login")
            .data(uniqueUrl)
            .build();

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
