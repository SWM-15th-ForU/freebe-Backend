package com.foru.freebe.auth.entity;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class KakaoUser {
	private final Map<String, Object> attributes;
	private final Map<String, Object> kakaoAccount;
	private final Collection<? extends GrantedAuthority> authorities;
	private final Long name;

	public KakaoUser(OAuth2User oAuth2User) {
		this.attributes = oAuth2User.getAttributes();
		this.kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		this.authorities = oAuth2User.getAuthorities();
		this.name = Long.valueOf(oAuth2User.getName());
	}

	public String getUserName() {
		return (String)kakaoAccount.get("name");
	}

	public String getEmail() {
		return (String)kakaoAccount.get("email");
	}

	public String getPhoneNumber() {
		return (String)kakaoAccount.get("phone_number");
	}

	public String getPhoneNumberFormatE164() {
		return "+" + getPhoneNumber().replaceAll("\\D", "");
	}

	public Integer getBirthYear() {
		return (Integer)kakaoAccount.get("birth_year");
	}
}
