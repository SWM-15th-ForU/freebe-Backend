package com.foru.freebe.auth.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.foru.freebe.member.entity.Member;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {
    private final Member member;
    private final Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public CustomUserDetails(Member member) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getAuthority()));
    }

    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getAuthority()));
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getMemberId() {
        return member.getId();
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

    /**
     * 필수 오버라이드 메서드입니다.
     * @return memberId를 리턴합니다.
     * @deprecated getMemberId 사용을 권장합니다.
     */
    @Deprecated
    @Override
    public String getName() {
        return member.getId().toString();
    }
}