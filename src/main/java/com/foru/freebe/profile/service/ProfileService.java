package com.foru.freebe.profile.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
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

        ProfileImage profileImage = profileImageRepository.findByProfile(photographerProfile);

        return new ProfileResponse(
            photographerProfile.getBannerImageUrl(),
            profileImage.getThumbnailUrl(),
            photographer.getInstagramId(),
            photographerProfile.getIntroductionContent(),
            linkInfos);
    }

    @Transactional
    public void initialProfileSetting(Member photographer, MultipartFile profileImage) throws IOException {
        Boolean isProfileExists = profileRepository.existsByMemberId(photographer.getId());
        if (isProfileExists) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        Profile profile = createMemberProfile(photographer);
        if (profileImage != null) {
            saveProfileImage(profile, profileImage, photographer.getId());
        }
    }

    private Profile createMemberProfile(Member member) {
        String uniqueUrl = createUniqueUrl();

        Profile profile = Profile.builder()
            .uniqueUrl(uniqueUrl)
            .introductionContent(null)
            .bannerImageUrl(null)
            .member(member)
            .build();

        return profileRepository.save(profile);
    }

    private String createUniqueUrl() {
        String uniqueId = UUID.randomUUID().toString();
        return freebeBaseUrl + "/photographer/" + uniqueId;
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
}
