package com.foru.freebe.profile.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProfileErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.profile.dto.CustomerViewProfileResponse;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.PhotographerViewProfileResponse;
import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.entity.ProfileImage;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileImageRepository;
import com.foru.freebe.profile.repository.ProfileRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final LinkRepository linkRepository;
    private final ProfileImageRepository profileImageRepository;

    @Transactional
    public Profile initialProfileSetting(Member photographer, String profileName, String contact) {
        boolean isProfileExists = profileRepository.existsByMemberId(photographer.getId());
        if (isProfileExists) {
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        validateProfileNameDuplicate(profileName);
        return createMemberProfile(photographer, profileName, contact);
    }

    public Profile getProfile(String profileName) {
        return profileRepository.findByProfileName(profileName)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.PROFILE_NAME_NOT_FOUND));
    }

    public Profile getProfile(Member photographer) {
        return profileRepository.findByMember(photographer)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.PROFILE_NOT_FOUND));
    }

    public String getProfileName(Long id) {
        Profile profile = getProfile(id);
        return profile.getProfileName();
    }

    public PhotographerViewProfileResponse findPhotographerViewProfile(Profile profile) {
        ProfileImage profileImage = profileImageRepository.findByProfile(profile).orElse(null);

        List<LinkInfo> linkInfos = getProfileLinkInfos(profile);

        return PhotographerViewProfileResponse.builder()
            .bannerImageUrl(profileImage != null ? profileImage.getBannerOriginUrl() : null)
            .profileImageUrl(profileImage != null ? profileImage.getProfileOriginUrl() : null)
            .profileName(profile.getProfileName())
            .contact(profile.getContact())
            .introductionContent(profile.getIntroductionContent())
            .linkInfos(linkInfos)
            .build();
    }

    public CustomerViewProfileResponse findCustomerViewProfile(Profile profile) {
        ProfileImage profileImage = profileImageRepository.findByProfile(profile).orElse(null);

        List<LinkInfo> linkInfos = getProfileLinkInfos(profile);

        return CustomerViewProfileResponse.builder()
            .bannerImageUrl(profileImage != null ? profileImage.getBannerOriginUrl() : null)
            .profileImageUrl(profileImage != null ? profileImage.getProfileOriginUrl() : null)
            .profileName(profile.getProfileName())
            .introductionContent(profile.getIntroductionContent())
            .linkInfos(linkInfos)
            .build();
    }

    private void validateProfileNameDuplicate(String profileName) {
        if (profileRepository.existsByProfileName(profileName)) {
            throw new RestApiException(ProfileErrorCode.PROFILE_NAME_ALREADY_EXISTS);
        }
    }

    private Profile createMemberProfile(Member member, String profileName, String contact) {
        Profile profile = Profile.builder()
            .profileName(profileName)
            .introductionContent(null)
            .member(member)
            .contact(contact)
            .build();

        return profileRepository.save(profile);
    }

    private Profile getProfile(Long memberId) {
        return profileRepository.findByMemberId(memberId)
            .orElseThrow(() -> new RestApiException(ProfileErrorCode.PROFILE_NOT_FOUND));
    }

    private List<LinkInfo> getProfileLinkInfos(Profile profile) {
        List<Link> links = linkRepository.findByProfile(profile);

        return links.stream()
            .map(link -> new LinkInfo(link.getTitle(), link.getUrl()))
            .collect(Collectors.toList());
    }
}
