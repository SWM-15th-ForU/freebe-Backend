package com.foru.freebe.profile.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProfileErrorCode;
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
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final LinkRepository linkRepository;
    private final ProfileImageRepository profileImageRepository;
    private final S3ImageService s3ImageService;

    public String getProfileName(Long id) {
        Profile profile = getProfile(id);
        return profile.getProfileName();
    }

    public ProfileResponse getPhotographerProfile(String profileName) {
        Profile profile = getProfile(profileName);
        return findPhotographerProfile(profileName, profile);
    }

    public ProfileResponse getMyCurrentProfile(Member photographer) {
        Profile profile = getProfile(photographer);
        return findPhotographerProfile(profile.getProfileName(), profile);
    }

    private ProfileResponse findPhotographerProfile(String profileName, Profile profile) {
        ProfileImage profileImage = profileImageRepository.findByProfile(profile).orElse(null);

        List<LinkInfo> linkInfos = getProfileLinkInfos(profile);

        return ProfileResponse.builder()
            .bannerImageUrl(profileImage != null ? profileImage.getBannerOriginUrl() : null)
            .profileImageUrl(profileImage != null ? profileImage.getProfileThumbnailUrl() : null)
            .profileName(profileName)
            .introductionContent(profile.getIntroductionContent())
            .linkInfos(linkInfos)
            .build();
    }

    @Transactional
    public void updateProfile(Member photographer, UpdateProfileRequest request, MultipartFile bannerImageFile,
        MultipartFile profileImageFile) throws IOException {

        Profile profile = getProfile(photographer);
        ProfileImage profileImage = createProfileImageIfNotExists(profile);

        if (request.getIntroductionContent() != null) {
            profile.updateIntroductionContent(request.getIntroductionContent());
        }

        updateLinks(profile, request.getLinkInfos());

        if (bannerImageFile != null) {
            updateBannerImage(profileImage, bannerImageFile, photographer.getId());
        } else {
            if (profileImage.getBannerOriginUrl() != null) {
                String bannerImageUrl = profileImage.getBannerOriginUrl();
                s3ImageService.deleteImageFromS3(bannerImageUrl);
                profileImage.assignBannerOriginUrl(null);
            }
        }

        if (profileImageFile != null) {
            updateProfileImage(profileImage, profileImageFile, photographer.getId());
        }
    }

    @Transactional
    public Profile initialProfileSetting(Member photographer, String profileName) {
        boolean isProfileExists = profileRepository.existsByMemberId(photographer.getId());
        if (isProfileExists) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        validateProfileNameDuplicate(profileName);
        return createMemberProfile(photographer, profileName);
    }

    @Transactional
    public void deleteProfile(Member photographer) {
        Profile profile = getProfile(photographer);
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

    private void validateProfileNameDuplicate(String profileName) {
        if (profileRepository.existsByProfileName(profileName)) {
            throw new RestApiException(ProfileErrorCode.PROFILE_NAME_ALREADY_EXISTS);
        }
    }

    private Profile getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private Profile getProfile(Member photographer) {
        return profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private Profile getProfile(String profileName) {
        return profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private List<LinkInfo> getProfileLinkInfos(Profile profile) {
        List<Link> links = linkRepository.findByProfile(profile);

        return links.stream()
            .map(link -> new LinkInfo(link.getTitle(), link.getUrl()))
            .collect(Collectors.toList());
    }

    private ProfileImage createProfileImageIfNotExists(Profile photographerProfile) {
        return profileImageRepository.findByProfile(photographerProfile)
            .orElse(ProfileImage.builder().profile(photographerProfile).build());
    }

    private void updateBannerImage(ProfileImage profileImage, MultipartFile imageFile, Long id) throws IOException {
        String bannerImageUrl = profileImage.getBannerOriginUrl();
        if (bannerImageUrl != null) {
            s3ImageService.deleteImageFromS3(bannerImageUrl);
        }

        SingleImageLink bannerImageLink = s3ImageService.imageUploadToS3(imageFile, S3ImageType.PROFILE, id, false);

        String newBannerImageUrl = bannerImageLink.getOriginalUrl();
        profileImage.assignBannerOriginUrl(newBannerImageUrl);

        profileImageRepository.save(profileImage);
    }

    private void updateProfileImage(ProfileImage profileImage, MultipartFile imageFile, Long id) throws IOException {
        String profileImageOriginUrl = profileImage.getProfileOriginUrl();
        String profileImageThumbnailUrl = profileImage.getProfileThumbnailUrl();
        if (profileImageOriginUrl != null) {
            s3ImageService.deleteImageFromS3(profileImageOriginUrl);
            s3ImageService.deleteImageFromS3(profileImageThumbnailUrl);
        }

        SingleImageLink profileImageLink = s3ImageService.imageUploadToS3(imageFile, S3ImageType.PROFILE, id, true);
        String originalImageUrl = profileImageLink.getOriginalUrl();
        String thumbnailImageUrl = profileImage.getProfileThumbnailUrl();

        profileImage.assignProfileOriginUrl(originalImageUrl);
        profileImage.assignProfileThumbnailUrl(thumbnailImageUrl);

        profileImageRepository.save(profileImage);
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

    private Profile createMemberProfile(Member member, String profileName) {
        Profile profile = Profile.builder()
            .profileName(profileName)
            .introductionContent(null)
            .member(member)
            .build();

        return profileRepository.save(profile);
    }

    private Boolean isLinkInfoChanged(Link existingLink, LinkInfo linkInfo) {
        return !existingLink.getUrl().equals(linkInfo.getLinkUrl()) || !existingLink.getTitle()
            .equals(linkInfo.getLinkTitle());
    }
}
