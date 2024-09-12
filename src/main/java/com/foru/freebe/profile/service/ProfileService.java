package com.foru.freebe.profile.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.dto.UpdateProfileRequest;
import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileRepository;

import jakarta.transaction.Transactional;
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

    public ProfileResponse getPhotographerProfile(String uniqueUrl) {
        Profile photographerProfile = profileRepository.findByUniqueUrl(uniqueUrl)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Member photographer = photographerProfile.getMember();

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

    public ApiResponse<ProfileResponse> getCurrentProfile(Member photographer) {
        Profile photographerProfile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<Link> links = linkRepository.findByProfile(photographerProfile);

        List<LinkInfo> linkInfos = links.stream()
            .map(link -> new LinkInfo(link.getTitle(), link.getUrl()))
            .collect(Collectors.toList());

        ProfileResponse profileResponse = new ProfileResponse(
            photographerProfile.getBannerImageUrl(),
            photographerProfile.getProfileImageUrl(),
            photographer.getInstagramId(),
            photographerProfile.getIntroductionContent(),
            linkInfos);

        return ApiResponse.<ProfileResponse>builder()
            .status(200)
            .message("Good Response")
            .data(profileResponse).build();
    }

    @Transactional
    public ApiResponse<Void> updateProfile(UpdateProfileRequest updateRequest, Member photographer) {
        Profile profile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        // 변경 감지: 필요한 필드만 업데이트
        if (!profile.getBannerImageUrl().equals(updateRequest.getBannerImageUrl())) {
            profile.assignBannerImageUrl(updateRequest.getBannerImageUrl());
        }
        if (!profile.getProfileImageUrl().equals(updateRequest.getProfileImageUrl())) {
            profile.assignProfileImageUrl(updateRequest.getProfileImageUrl());
        }
        if (!profile.getIntroductionContent().equals(updateRequest.getIntroductionContent())) {
            profile.assignIntroductionContent(updateRequest.getIntroductionContent());
        }

        // Link 업데이트 로직
        updateLinks(profile, updateRequest.getLinkInfos());

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Updated successfully")
            .data(null).build();
    }

    @Transactional
    protected void updateLinks(Profile profile, List<LinkInfo> linkInfos) {
        List<Link> existingLinks = linkRepository.findByProfile(profile);

        // 갱신된 링크 식별
        List<String> incomingLinkTitles = new ArrayList<>();
        for (LinkInfo linkInfo : linkInfos) {
            if (linkInfo.getLinkTitle() != null) {
                String linkTitle = linkInfo.getLinkTitle();
                incomingLinkTitles.add(linkTitle);
            }
        }

        // 삭제할 링크 식별
        existingLinks.stream()
            .filter(link -> !incomingLinkTitles.contains(link.getTitle()))
            .forEach(linkRepository::delete);

        // 갱신 및 추가 처리
        for (LinkInfo linkInfo : linkInfos) {
            Link existingLink = existingLinks.stream()
                .filter(link -> link.getTitle().equals(linkInfo.getLinkTitle()))
                .findFirst()
                .orElse(null);

            if (existingLink == null) {
                // 기존에 없는 새로운 링크 추가
                Link newLink = Link.builder()
                    .profile(profile)
                    .title(linkInfo.getLinkTitle())
                    .url(linkInfo.getLinkUrl())
                    .build();
                linkRepository.save(newLink);
            } else { // 기존 링크 갱신 (타이틀 또는 URL이 변경된 경우)
                if (isLinkInfoChanged(existingLink, linkInfo)) {
                    existingLink.assignLinkInfo(linkInfo.getLinkTitle(), linkInfo.getLinkUrl());
                }
            }
        }
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

    private Boolean isLinkInfoChanged(Link existingLink, LinkInfo linkInfo) {
        return !existingLink.getUrl().equals(linkInfo.getLinkUrl()) ||
            !existingLink.getTitle().equals(linkInfo.getLinkTitle());
    }
}
