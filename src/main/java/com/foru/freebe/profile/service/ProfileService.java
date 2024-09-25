package com.foru.freebe.profile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProfileErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.dto.UpdateProfileRequest;
import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.entity.ProfileImage;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileImageRepository;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.s3.S3ImageService;
import com.foru.freebe.s3.S3ImageType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final int PROFILE_THUMBNAIL_SIZE = 100;

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final LinkRepository linkRepository;
    private final ProfileImageRepository profileImageRepository;
    private final S3ImageService s3ImageService;

    @Value("${FREEBE_BASE_URL}")
    private String freebeBaseUrl;

    public String getUniqueUrl(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Profile profile = profileRepository.findByMember(member)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        return profile.getProfileName();
    }

    public ProfileResponse getPhotographerProfile(String profileName) {
        Profile photographerProfile = profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Member photographer = photographerProfile.getMember();

        List<Link> links = linkRepository.findByProfile(photographerProfile);

        List<LinkInfo> linkInfos = links.stream()
            .map(link -> new LinkInfo(link.getTitle(), link.getUrl()))
            .collect(Collectors.toList());

        ProfileImage profileImage = profileImageRepository.findByProfile(photographerProfile).orElse(null);

        if (profileImage != null) {
            return new ProfileResponse(
                photographerProfile.getBannerImageUrl(),
                profileImage.getThumbnailUrl(),
                photographerProfile.getProfileName(),
                photographerProfile.getIntroductionContent(),
                linkInfos);
        } else {
            return new ProfileResponse(
                photographerProfile.getBannerImageUrl(),
                null,
                photographerProfile.getProfileName(),
                photographerProfile.getIntroductionContent(),
                linkInfos);
        }

    }

    public ProfileResponse getCurrentProfile(Member photographer) {
        Profile photographerProfile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        ProfileImage profileImage = profileImageRepository.findByProfile(photographerProfile).orElse(null);

        List<Link> links = linkRepository.findByProfile(photographerProfile);

        List<LinkInfo> linkInfos = links.stream()
            .map(link -> new LinkInfo(link.getTitle(), link.getUrl()))
            .collect(Collectors.toList());

        if (profileImage != null) {
            return new ProfileResponse(
                photographerProfile.getBannerImageUrl(),
                profileImage.getThumbnailUrl(),
                photographerProfile.getProfileName(),
                photographerProfile.getIntroductionContent(),
                linkInfos);
        } else {
            return new ProfileResponse(
                photographerProfile.getBannerImageUrl(),
                null,
                photographerProfile.getProfileName(),
                photographerProfile.getIntroductionContent(),
                linkInfos);
        }
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest updateRequest, Member photographer,
        MultipartFile profileImage) throws IOException {
        Profile photographerProfile = profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Member persistedPhotographer = memberRepository.findById(photographer.getId())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        ProfileImage existingProfileImage = profileImageRepository.findByProfile(photographerProfile).orElse(null);
        if (existingProfileImage != null) {
            profileImageRepository.delete(existingProfileImage);
            saveProfileImage(photographerProfile, profileImage, photographer.getId());
        }

        // 변경 감지: 필요한 필드만 업데이트
        if (!Objects.equals(photographerProfile.getBannerImageUrl(), updateRequest.getBannerImageUrl())) {
            photographerProfile.assignBannerImageUrl(updateRequest.getBannerImageUrl());
        }
        if (!Objects.equals(photographerProfile.getIntroductionContent(), updateRequest.getIntroductionContent())) {
            photographerProfile.assignIntroductionContent(updateRequest.getIntroductionContent());
        }

        updateLinks(photographerProfile, updateRequest.getLinkInfos());
    }

    private void updateLinks(Profile profile, List<LinkInfo> linkInfos) {
        List<Link> existingLinks = linkRepository.findByProfile(profile);

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
            } else { // 기존 링크 갱신 (URL이 변경된 경우)
                if (isLinkInfoChanged(existingLink, linkInfo)) {
                    existingLink.assignLinkUrl(linkInfo.getLinkUrl());
                }
            }
        }
    }

    @Transactional
    public void initialProfileSetting(Member photographer, MultipartFile profileImage, String profileName) throws
        IOException {
        boolean isProfileExists = profileRepository.existsByMemberId(photographer.getId());
        if (isProfileExists) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        validateProfileNameDuplicate(profileName);
        Profile profile = createMemberProfile(photographer, profileName);
        if (profileImage != null) {
            saveProfileImage(profile, profileImage, photographer.getId());
        }
    }

    private void validateProfileNameDuplicate(String profileName) {
        if (profileRepository.existsByProfileName(profileName)) {
            throw new RestApiException(ProfileErrorCode.PROFILE_NAME_ALREADY_EXISTS);
        }
    }

    private Profile createMemberProfile(Member member, String profileName) {
        Profile profile = Profile.builder()
            .profileName(profileName)
            .introductionContent(null)
            .bannerImageUrl(null)
            .member(member)
            .build();

        return profileRepository.save(profile);
    }

    private String createUniqueUrl() {
        String uniqueId = UUID.randomUUID().toString();
        return freebeBaseUrl + "/" + uniqueId;
    }

    private void saveProfileImage(Profile profile, MultipartFile profileImage, Long id) throws IOException {
        deleteProfileImageIfExists(profile);

        List<MultipartFile> profileImages = Collections.singletonList(profileImage);
        List<String> originalImageUrls = s3ImageService.uploadOriginalImages(profileImages, S3ImageType.PROFILE, id);
        List<String> thumbnailImageUrls = s3ImageService.uploadThumbnailImages(profileImages, S3ImageType.PROFILE, id,
            PROFILE_THUMBNAIL_SIZE);

        String originalImageUrl = originalImageUrls.get(0);
        String thumbnailImageUrl = thumbnailImageUrls.get(0);

        ProfileImage image = ProfileImage.builder()
            .profile(profile)
            .originUrl(originalImageUrl)
            .thumbnailUrl(thumbnailImageUrl)
            .build();
        profileImageRepository.save(image);
    }

    private void deleteProfileImageIfExists(Profile profile) {
        Boolean isProfileExists = profileImageRepository.existsByProfile(profile);
        if (isProfileExists) {
            profileImageRepository.deleteByProfile(profile);
        }
    }

    private Boolean isLinkInfoChanged(Link existingLink, LinkInfo linkInfo) {
        return !existingLink.getUrl().equals(linkInfo.getLinkUrl()) ||
            !existingLink.getTitle().equals(linkInfo.getLinkTitle());
    }
}
