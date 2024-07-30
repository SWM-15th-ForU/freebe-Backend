package com.foru.freebe.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.foru.freebe.auth.entity.KakaoUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final CognitoUtil cognitoUtil;

	public CustomAuthenticationSuccessHandler(CognitoUtil cognitoUtil) {
		this.cognitoUtil = cognitoUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		KakaoUser kakaoUser = new KakaoUser(oAuth2User);

		cognitoUtil.registerIfUserNotInCognito(kakaoUser);

		String accessToken = cognitoUtil.generateToken(kakaoUser).accessToken();
		String refreshToken = cognitoUtil.generateToken(kakaoUser).refreshToken();
		//todo: refreshToken 저장 로직 추가

		//todo: 토큰을 쿠키에 담아 리다이렉트
		response.sendRedirect("/main");
	}
}