package com.foru.freebe.profile.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.errors.errorcode.LinkErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
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
public class PhotographerProfileService {
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;
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

        profile.updateContact(request.getContact());
        updateIntroductionContent(profile, request.getIntroductionContent());

        validateLinkTitleDuplicate(request.getLinkInfos());
        updateLinks(profile, request.getLinkInfos());

        updateBannerImage(photographer.getId(), request.getExistingBannerImageUrl(), bannerImageFile, profileImage);
        updateProfileImage(photographer.getId(), request.getExistingProfileImageUrl(), profileImageFile, profileImage);
    }

    @Transactional
    public void deleteProfile(Member photographer) {
        Profile profile = profileService.getProfile(photographer);
        deleteLinks(profile);
        deleteProfileImage(profile);
        profileRepository.delete(profile);
    }

    private void deleteLinks(Profile profile) {
        List<Link> links = linkRepository.findByProfile(profile);
        linkRepository.deleteAll(links);
    }

    private void deleteProfileImage(Profile profile) {
        ProfileImage profileImage = profileImageRepository.findByProfile(profile)
            .orElse(null);

        if (profileImage != null) {
            deleteImageFromS3(profileImage);
            profileImageRepository.delete(profileImage);
        }
    }

    private void deleteImageFromS3(ProfileImage profileImage) {
        if (profileImage.getProfileOriginUrl() != null) {
            s3ImageService.deleteImageFromS3(profileImage.getProfileOriginUrl());
        } else if (profileImage.getBannerOriginUrl() != null) {
            s3ImageService.deleteImageFromS3(profileImage.getBannerOriginUrl());
        }
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

    private void validateLinkTitleDuplicate(List<LinkInfo> linkInfos) {
        boolean isDuplicatedTitle = linkInfos.size() != linkInfos.stream()
            .map(LinkInfo::getLinkTitle)
            .distinct()
            .count();

        if (isDuplicatedTitle) {
            throw new RestApiException(LinkErrorCode.DUPLICATE_TITLE);
        }
    }

    private void updateLinks(Profile profile, List<LinkInfo> linkInfos) {
        List<Link> existingLinks = linkRepository.findByProfile(profile);
        linkRepository.deleteAll(existingLinks);

        for (LinkInfo linkInfo : linkInfos) {
            createNewLink(profile, linkInfo);
        }
    }

    private void createNewLink(Profile profile, LinkInfo linkInfo) {
        Link newLink = Link.builder()
            .profile(profile)
            .title(linkInfo.getLinkTitle())
            .url(linkInfo.getLinkUrl())
            .build();
        linkRepository.save(newLink);
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
