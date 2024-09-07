package com.foru.freebe.profile.service;

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

    public ApiResponse<Void> addExternalLink(LinkInfo request, Member photographer) {
        boolean isTitleDuplicate = linkRepository.findByTitle(request.getLinkTitle()).isPresent();
        boolean isUrlDuplicate = linkRepository.findByUrl(request.getLinkUrl()).isPresent();

        if (isTitleDuplicate || isUrlDuplicate) {
            throw new RestApiException(CommonErrorCode.DUPLICATED_RESOURCE);
        }

        Profile photographerProfile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Link link = Link.builder()
            .profile(photographerProfile)
            .title(request.getLinkTitle())
            .url(request.getLinkUrl())
            .build();

        linkRepository.save(link);

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Good Request")
            .data(null)
            .build();
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
