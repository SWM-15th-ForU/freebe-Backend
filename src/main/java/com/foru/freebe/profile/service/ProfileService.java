package com.foru.freebe.profile.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final LinkRepository linkRepository;

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

    public ProfileResponse getPhotographerProfile(Long photographerId) {
        Member photographer = memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        if (photographer.getRole() != Role.PHOTOGRAPHER) {
            throw new RestApiException(CommonErrorCode.INVALID_MEMBER_ROLE_TYPE);
        }

        Profile photographerProfile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<Link> links = linkRepository.findByProfile(photographerProfile);

        List<LinkInfo> linkInfos = links.stream()
            .map(link -> new LinkInfo(link.getTitle(), link.getUrl()))
            .collect(Collectors.toList());

        return new ProfileResponse(
            photographerProfile.getBannerImageUrl(),
            photographerProfile.getProfileImageUrl(),
            photographer.getInstagramId(),
            photographerProfile.getIntroductionContent(),
            linkInfos);
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
