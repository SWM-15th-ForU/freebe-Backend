package com.foru.freebe.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.dto.LoginRequest;
import com.foru.freebe.auth.dto.LoginResponse;
import com.foru.freebe.auth.dto.UnlinkRequest;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.auth.service.KakaoAuthService;
import com.foru.freebe.auth.service.KakaoLoginService;
import com.foru.freebe.auth.service.KakaoUnlinkService;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.jwt.model.JwtTokenModel;
import com.foru.freebe.jwt.service.JwtService;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.message.service.MessageSendService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final KakaoAuthService kakaoAuthService;
    private final KakaoLoginService kakaoLoginService;
    private final KakaoUnlinkService kakaoUnlinkService;
    private final MessageSendService messageSendService;

    @PostMapping("/login")
    public ResponseEntity<ResponseBody<?>> login(@RequestBody LoginRequest loginRequest) {

        String accessToken = kakaoAuthService.getToken(loginRequest.getCode());
        KakaoUser kakaoUser = kakaoAuthService.getUserInfo(accessToken);

        LoginResponse loginResponse = kakaoLoginService.findOrRegisterMember(kakaoUser, loginRequest.getRoleType());
        HttpHeaders headers = jwtService.setTokenHeaders(loginResponse.getToken());
        ResponseBody<?> responseBody = ResponseBody.builder()
            .message(loginResponse.getMessage())
            .data(loginResponse.getProfileName())
            .build();

        messageSendService.messageSendRequest(kakaoUser, loginResponse.isNewMember());

        return ResponseEntity.status(HttpStatus.OK)
            .headers(headers)
            .body(responseBody);
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken(HttpServletRequest request) {

        String refreshToken = request.getHeader("refreshToken");

        JwtTokenModel token = jwtService.reissueToken(refreshToken);
        HttpHeaders headers = jwtService.setTokenHeaders(token);

        return ResponseEntity.status(HttpStatus.OK.value())
            .headers(headers)
            .body(null);
    }

    @PostMapping("/unlink")
    public ResponseEntity<ResponseBody<Void>> unlinkKakao(@Valid @RequestBody UnlinkRequest request,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member member = memberAdapter.getMember();
        kakaoUnlinkService.unlinkKakaoAccount(member.getId(), request);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully delete member")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(responseBody);
    }
}
