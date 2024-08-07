package com.foru.freebe.member.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public void register(KakaoUser kakaoUser) {
        Member member = Member.builder(kakaoUser.getName(), Role.PENDING, kakaoUser.getUserName(),
                kakaoUser.getEmail(),
                kakaoUser.getPhoneNumber())
            .build();
        memberRepository.save(member);
    }

    public void updateMemberRole(Long id, Role role) {
        Optional<Member> member = memberRepository.findByKakaoId(id);
        if (member.isPresent()) {
            member.get().updateRole(role);
            memberRepository.save(member.get());
        }
    }
}
