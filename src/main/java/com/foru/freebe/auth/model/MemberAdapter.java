package com.foru.freebe.auth.model;

import java.util.Map;

import com.foru.freebe.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberAdapter extends CustomUserDetails {
    private final Member member;
    private Map<String, Object> attributes;

    public MemberAdapter(Member member) {
        super(member);
        this.member = member;
    }

    public MemberAdapter(Member member, Map<String, Object> attributes) {
        super(member, attributes);
        this.member = member;
        this.attributes = attributes;
    }
}
