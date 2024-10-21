package com.foru.freebe.auth.model;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 필수 오버라이드 메서드입니다.
     * @return 빈 문자열을 리턴합니다.
     * @deprecated 사용하지 말아주세요.
     */
    @Deprecated
    @Override
    public String getPassword() {
        return "";
    }

    /**
     * 필수 오버라이드 메서드입니다. 인증 객체 로드 시 사용됩니다.
     * @deprecated getMemberId 사용을 권장합니다.
     */
    @Override
    public String getUsername() {
        return member.getId().toString();
    }
}