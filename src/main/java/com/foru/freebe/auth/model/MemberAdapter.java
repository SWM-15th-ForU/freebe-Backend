package com.foru.freebe.auth.model;

import com.foru.freebe.member.entity.Member;

import lombok.Getter;

@Getter
public class MemberAdapter extends CustomUserDetails {
    private final Member member;

    public MemberAdapter(Member member) {
        super(member);
        this.member = member;
    }
}
