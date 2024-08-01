package com.foru.freebe.auth.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.foru.freebe.auth.model.JwtTokenModel;
import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.auth.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		KakaoUser kakaoUser = new KakaoUser(oAuth2User);

		JwtTokenModel token = jwtService.generateToken(kakaoUser);

		ResponseCookie accessTokenCookie = createCookie("accessToken", token.getAccessToken());
		ResponseCookie responseTokenCookie = createCookie("refreshToken", token.getRefreshToken());

		response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.addHeader(HttpHeaders.SET_COOKIE, responseTokenCookie.toString());

		response.sendRedirect("https://www.freebe.co.kr/login/redirect");
	}

	private ResponseCookie createCookie(String name, String value) {
		return ResponseCookie.from(name, value)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(Duration.ofDays(15)) // todo: 적절히 시간 조절 필요.
			.sameSite("None")
			.build();
	}
}