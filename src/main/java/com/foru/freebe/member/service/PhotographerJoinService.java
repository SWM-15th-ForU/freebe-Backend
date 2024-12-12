package com.foru.freebe.member.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.auth.model.ServiceTermsAgreement;
import com.foru.freebe.auth.service.KakaoAuthService;
import com.foru.freebe.schedule.service.BaseScheduleService;
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
    private final KakaoAuthService kakaoAuthService;
    private final BaseScheduleService baseScheduleService;

    @Transactional
    public String joinPhotographer(Member member, PhotographerJoinRequest request) {
        Member photographer = completePhotographerSignup(member);

        ServiceTermsAgreement serviceTermsAgreement = kakaoAuthService.getAgreementStatus(photographer.getKakaoId());
        savePhotographerAgreements(photographer, serviceTermsAgreement);
        Profile profile = profileService.initialProfileSetting(photographer, request.getProfileName(),
            request.getContact());

        initializeScheduleInfo(member, photographer);

        return profile.getProfileName();
    }

    private void initializeScheduleInfo(Member member, Member photographer) {
        baseScheduleService.createDefaultSchedule(photographer);
        member.initializeScheduleUnit();
        memberRepository.save(member);
    }

    private Member completePhotographerSignup(Member member) {
        member.assignRole(Role.PHOTOGRAPHER);
        return memberRepository.save(member);
    }

    private void savePhotographerAgreements(Member member, ServiceTermsAgreement serviceTermsAgreement) {
        MemberTermAgreement memberTermAgreement = MemberTermAgreement.builder()
            .member(member)
            .marketingAgreement(serviceTermsAgreement.getMarketingServiceTermsAgreement())
            .build();
        memberTermAgreementRepository.save(memberTermAgreement);
    }
}
