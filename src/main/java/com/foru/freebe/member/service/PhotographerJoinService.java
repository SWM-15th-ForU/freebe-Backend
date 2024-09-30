package com.foru.freebe.member.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.member.dto.PhotographerJoinRequest;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.MemberTermAgreement;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.member.repository.MemberTermAgreementRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.service.ProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerJoinService {
    private final ProfileService profileService;
    private final MemberRepository memberRepository;
    private final MemberTermAgreementRepository memberTermAgreementRepository;

    @Transactional
    public String joinPhotographer(Member member, PhotographerJoinRequest request) {
        Member photographer = completePhotographerSignup(member);

        savePhotographerAgreements(photographer, request);
        Profile profile = profileService.initialProfileSetting(photographer, request.getProfileName());

        return profile.getProfileName();
    }

    private Member completePhotographerSignup(Member member) {
        member.assignRole(Role.PHOTOGRAPHER);
        return memberRepository.save(member);
    }

    private void savePhotographerAgreements(Member member, PhotographerJoinRequest request) {
        MemberTermAgreement memberTermAgreement = MemberTermAgreement.builder()
            .member(member)
            .termsOfServiceAgreement(request.getTermsOfServiceAgreement())
            .privacyPolicyAgreement(request.getPrivacyPolicyAgreement())
            .marketingAgreement(request.getMarketingAgreement())
            .build();
        memberTermAgreementRepository.save(memberTermAgreement);
    }
}
