package com.foru.freebe.member.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.KakaoUser;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public Member registerKakaoUser(KakaoUser kakaoUser) {
        Optional<Member> member = memberRepository.findByKakaoId(kakaoUser.getKakaoId());
        if (member.isPresent()) {
            return member.get();
        }

        Member newMember = Member.builder(kakaoUser.getKakaoId(), Role.PENDING, kakaoUser.getUserName(),
            kakaoUser.getEmail(), kakaoUser.getPhoneNumber()).build();

        return memberRepository.save(newMember);
    }

    public void assignMemberRole(Long id, Role role) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        member.assignRole(role);
        memberRepository.save(member);
    }
}
