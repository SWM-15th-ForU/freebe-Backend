package com.foru.freebe.auth.handler;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.foru.freebe.auth.entity.KakaoUser;
import com.foru.freebe.auth.service.CognitoRegistrationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final CognitoRegistrationService cognitoRegistrationService;

	public CustomAuthenticationSuccessHandler(CognitoRegistrationService cognitoRegistrationService) {
		this.cognitoRegistrationService = cognitoRegistrationService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		KakaoUser kakaoUser = new KakaoUser(oAuth2User);

		cognitoRegistrationService.registerIfUserNotInCognito(kakaoUser);

		String accessToken = cognitoRegistrationService.generateToken(kakaoUser).accessToken();
		String refreshToken = cognitoRegistrationService.generateToken(kakaoUser).refreshToken();
		//todo: refreshToken 저장 로직 추가

		ResponseCookie accessTokenCookie = createCookie("accessToken", accessToken);
		ResponseCookie responseTokenCookie = createCookie("refreshToken", refreshToken);

		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, responseTokenCookie.toString());

		response.sendRedirect("/main");
	}

	private ResponseCookie createCookie(String name, String value) {
		return ResponseCookie.from(name, value)
			.httpOnly(true)
			// .secure(true) -> todo: https 환경이어야 함. 현재 로컬 테스트 중이므로 주석처리
			.path("/")
			.maxAge(Duration.ofDays(15)) // todo: 적절히 시간 조절 필요.
			.sameSite("None")
			.build();
	}
}