package com.foru.freebe.profile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.dto.UpdateProfileRequest;
import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.entity.ProfileImage;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileImageRepository;
import com.foru.freebe.s3.S3ImageService;
import com.foru.freebe.s3.S3ImageType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerProfileService {
    private final ProfileService profileService;
    private final S3ImageService s3ImageService;
    private final ProfileImageRepository profileImageRepository;
    private final LinkRepository linkRepository;

    public ProfileResponse getMyCurrentProfile(Member photographer) {
        Profile profile = profileService.getProfile(photographer);
        return profileService.findPhotographerProfile(profile.getProfileName(), profile);
    }

    @Transactional
    public void updateProfile(Member photographer, UpdateProfileRequest request, MultipartFile bannerImageFile,
        MultipartFile profileImageFile) throws IOException {

        Profile profile = profileService.getProfile(photographer);
        ProfileImage profileImage = createProfileImageIfNotExists(profile);

        updateIntroductionContent(profile, request.getIntroductionContent());
        updateLinks(profile, request.getLinkInfos());
        updateBannerImage(photographer.getId(), request.getExistingBannerImageUrl(), bannerImageFile, profileImage);
        updateProfileImage(photographer.getId(), request.getExistingProfileImageUrl(), profileImageFile, profileImage);
    }

    private ProfileImage createProfileImageIfNotExists(Profile photographerProfile) {
        return profileImageRepository.findByProfile(photographerProfile)
            .orElse(ProfileImage.builder().profile(photographerProfile).build());
    }

    private void updateIntroductionContent(Profile profile, String newIntroductionContent) {
        if (newIntroductionContent != null) {
            profile.updateIntroductionContent(newIntroductionContent);
        }
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

    private Boolean isLinkInfoChanged(Link existingLink, LinkInfo linkInfo) {
        return !existingLink.getUrl().equals(linkInfo.getLinkUrl()) || !existingLink.getTitle()
            .equals(linkInfo.getLinkTitle());
    }

    private void updateBannerImage(Long photographerId, String existingBannerImageUrl, MultipartFile newImageFile,
        ProfileImage profileImage) throws IOException {

        if (newImageFile != null) {
            registerOrUpdateBannerImage(profileImage, newImageFile, photographerId);
        } else if (existingBannerImageUrl == null) {
            deleteCurrentBannerImage(profileImage);
        }
    }

    private void updateProfileImage(Long photographerId, String existingProfileImageUrl, MultipartFile newImageFile,
        ProfileImage profileImage) throws IOException {

        if (newImageFile != null) {
            registerOrUpdateProfileImage(profileImage, newImageFile, photographerId);
        } else if (existingProfileImageUrl == null) {
            deleteCurrentProfileImage(profileImage);
        }
    }

    private void registerOrUpdateBannerImage(ProfileImage profileImage, MultipartFile newImage,
        Long photographerId) throws IOException {

        deleteCurrentBannerImage(profileImage);

        SingleImageLink bannerImageLink = s3ImageService.imageUploadToS3(newImage, S3ImageType.BANNER, photographerId,
            false);
        String newBannerImageUrl = bannerImageLink.getOriginalUrl();

        profileImage.assignBannerOriginUrl(newBannerImageUrl);
        profileImageRepository.save(profileImage);
    }

    private void registerOrUpdateProfileImage(ProfileImage profileImage, MultipartFile newImage,
        Long photographerId) throws IOException {

        deleteCurrentProfileImage(profileImage);

        SingleImageLink profileImageLink = s3ImageService.imageUploadToS3(newImage, S3ImageType.PROFILE, photographerId,
            false);
        String newProfileImageUrl = profileImageLink.getOriginalUrl();

        profileImage.assignProfileOriginUrl(newProfileImageUrl);
        profileImageRepository.save(profileImage);
    }

    private void deleteCurrentBannerImage(ProfileImage profileImage) {
        String bannerOriginUrl = profileImage.getBannerOriginUrl();
        if (bannerOriginUrl != null) {
            s3ImageService.deleteImageFromS3(bannerOriginUrl);
            profileImage.assignBannerOriginUrl(null);
        }
    }

    private void deleteCurrentProfileImage(ProfileImage profileImage) {
        String profileImageOriginUrl = profileImage.getProfileOriginUrl();
        if (profileImageOriginUrl != null) {
            s3ImageService.deleteImageFromS3(profileImageOriginUrl);
            profileImage.assignProfileOriginUrl(null);
        }
    }
}
