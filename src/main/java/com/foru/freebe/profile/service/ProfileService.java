package com.foru.freebe.profile.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;

    @Value("${FREEBE_BASE_URL}")
    private String freebeBaseUrl;

    public String getUniqueUrl(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Profile profile = profileRepository.findByMember(member)
            .orElseGet(() -> createMemberProfile(member));

        if (profile.getUniqueUrl() == null) {
            saveUniqueUrl(profile);
        }

        return profile.getUniqueUrl();
    }

    private Profile createMemberProfile(Member member) {
        return Profile.builder()
            .uniqueUrl(null)
            .introductionContent(null)
            .bannerImageUrl(null)
            .member(member)
            .build();
    }

    private void saveUniqueUrl(Profile profile) {
        String uniqueUrl = generateUniqueUrl();
        profile.assignUniqueUrl(uniqueUrl);
        profileRepository.save(profile);
    }

    private String generateUniqueUrl() {
        String uniqueId = UUID.randomUUID().toString();
        return freebeBaseUrl + "/photographer/" + uniqueId;
    }
}
