package com.foru.freebe.auth.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.foru.freebe.auth.model.KakaoUser;

public class CustomOAuth2User implements OAuth2User {
    private final KakaoUser kakaoUser;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(KakaoUser kakaoUser) {
        this.kakaoUser = kakaoUser;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PENDING"));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return kakaoUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return kakaoUser.getName().toString();
    }
}
