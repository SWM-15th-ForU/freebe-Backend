package com.foru.freebe.auth.entity;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class KakaoUser {
	private final Map<String, Object> attributes;
	private final Map<String, Object> kakao_account;
	private final Collection<? extends GrantedAuthority> authorities;
	private final Long name;

	public KakaoUser(OAuth2User oAuth2User) {
		this.attributes = oAuth2User.getAttributes();
		this.kakao_account = (Map<String, Object>)attributes.get("kakao_account");
		this.authorities = oAuth2User.getAuthorities();
		this.name = Long.valueOf(oAuth2User.getName());
	}

	public String getUserName() {
		return (String)kakao_account.get("name");
	}

	public String getEmail() {
		return (String)kakao_account.get("email");
	}

	public String getPhoneNumber() {
		return (String)kakao_account.get("phone_number");
	}

	public String getPhoneNumberFormatE164() {
		return "+" + getPhoneNumber().replaceAll("[^\\d]", "");
	}

	public Integer getBirthYear() {
		return (Integer)kakao_account.get("birth_year");
	}
}
