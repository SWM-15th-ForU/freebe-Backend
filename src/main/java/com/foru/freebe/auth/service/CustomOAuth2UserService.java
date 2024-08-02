package com.foru.freebe.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.KakaoUser;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final KakaoUserRegistrationService kakaoUserRegistrationService;

	public CustomOAuth2UserService(KakaoUserRegistrationService abstractOAuth2UserService) {
		this.kakaoUserRegistrationService = abstractOAuth2UserService;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		kakaoUserRegistrationService.register(oAuth2User);
		KakaoUser kakaoUser = new KakaoUser(oAuth2User);

		return new CustomOAuth2User(kakaoUser);
	}
}
