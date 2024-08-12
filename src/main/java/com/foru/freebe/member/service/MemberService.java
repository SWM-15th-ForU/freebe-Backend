package com.foru.freebe.member.service;

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

    public Member findOrRegisterMember(KakaoUser kakaoUser) {
        return memberRepository.findByKakaoId(kakaoUser.getKakaoId())
            .orElseGet(() -> registerNewMember(kakaoUser));
    }

    public void assignMemberRole(Long id, Role role) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        member.assignRole(role);
        memberRepository.save(member);
    }

    private Member registerNewMember(KakaoUser kakaoUser) {
        Member newMember = Member.builder(kakaoUser.getKakaoId(), Role.PENDING, kakaoUser.getUserName(),
                kakaoUser.getEmail(), kakaoUser.getPhoneNumber())
            .birthyear(kakaoUser.getBirthYear())
            .gender(kakaoUser.getGender())
            .build();
        return memberRepository.save(newMember);
    }
}
