package com.foru.freebe.auth.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.foru.freebe.member.entity.Member;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Member member;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Member member) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getAuthority()));
    }

    public Long getKakaoId() {
        return member.getKakaoId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return member.getKakaoId().toString();
    }
}