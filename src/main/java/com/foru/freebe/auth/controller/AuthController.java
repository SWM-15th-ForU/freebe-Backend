package com.foru.freebe.auth.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.LoginRequest;
import com.foru.freebe.auth.dto.TokenResponse;
import com.foru.freebe.auth.service.AuthService;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

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
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.exchangeToken(loginRequest.getCode());
        authService.getUserInfo(tokenResponse, loginRequest.getRoleType());
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
}
