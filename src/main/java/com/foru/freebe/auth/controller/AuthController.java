package com.foru.freebe.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.LoginRequest;
import com.foru.freebe.auth.dto.LoginResponse;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.service.AuthService;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken(HttpServletRequest request) {

        String refreshToken = request.getHeader("refreshToken");

        JwtTokenModel token = jwtService.reissueToken(refreshToken);
        HttpHeaders headers = jwtService.setTokenHeaders(token);

        return ResponseEntity.status(HttpStatus.OK.value())
            .headers(headers)
            .body(null);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBody<?>> login(@RequestBody LoginRequest loginRequest) {

        String accessToken = authService.getToken(loginRequest.getCode());
        KakaoUser kakaoUser = authService.getUserInfo(accessToken);

        LoginResponse loginResponse = memberService.findOrRegisterMember(kakaoUser, loginRequest.getRoleType());

        ResponseBody<?> responseBody = ResponseBody.builder()
            .message(loginResponse.getMessage())
            .data(loginResponse.getUniqueUrl())
            .build();

        HttpHeaders headers = jwtService.setTokenHeaders(loginResponse.getToken());

        return ResponseEntity.status(HttpStatus.OK)
            .headers(headers)
            .body(responseBody);
    }
}
