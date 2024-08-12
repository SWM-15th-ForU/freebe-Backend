package com.foru.freebe.auth.model;

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
    private final Long kakaoId;

    public KakaoUser(OAuth2User oAuth2User) {
        this.attributes = oAuth2User.getAttributes();
        this.kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        this.authorities = oAuth2User.getAuthorities();
        this.kakaoId = Long.valueOf(oAuth2User.getName());
    }

    public String getUserName() {
        return (String)kakaoAccount.get("name");
    }

    public String getEmail() {
        return (String)kakaoAccount.get("email");
    }

    public String getPhoneNumber() {
        String phoneNumber = (String)kakaoAccount.get("phone_number");
        return "0" + phoneNumber.replace("+82 ", "");
    }

    public Integer getBirthYear() {
        return (Integer)kakaoAccount.get("birth_year");
    }

    public String getGender() {
        return (String)kakaoAccount.get("gender");
    }
}
