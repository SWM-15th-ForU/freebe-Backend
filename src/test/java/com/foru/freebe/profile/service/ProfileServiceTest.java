package com.foru.freebe.profile.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.ImageLinkSet;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
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

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private S3ImageService s3ImageService;

    @InjectMocks
    private ProfileService profileService;

    private final Member photographer = createNewMember();

    private Member createNewMember() {
        return Member
            .builder(1L, Role.PHOTOGRAPHER, "이유리", "yuri@naver.com", "010-1234-5678")
            .build();
    }

    @Nested
    @DisplayName("프로필 조회 테스트")
    class ProfileQueryTest {
        private Profile profile;
        private ProfileImage profileImage;
        private List<Link> links;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            profile = Profile.builder()
                .profileName("uniqueName")
                .member(photographer)
                .introductionContent("Welcome to my profile")
                .build();

            profileImage = ProfileImage.builder()
                .bannerOriginUrl("https://freebe/banner/origin")
                .profileOriginUrl("https://freebe/profile/origin")
                .profileThumbnailUrl("https://freebe/profile/thumbnail")
                .build();

            links = List.of(
                Link.builder().profile(profile).title("title1").url("url1").build(),
                Link.builder().profile(profile).title("title2").url("url2").build()
            );
        }

        @Test
        @DisplayName("(성공) 사진작가가 자신의 프로필을 조회한다")
        void testGetMyCurrentProfile() {
            // given
            when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(profile));
            when(profileImageRepository.findByProfile(profile)).thenReturn(Optional.of(profileImage));
            when(linkRepository.findByProfile(profile)).thenReturn(links);

            // when
            ProfileResponse response = profileService.getMyCurrentProfile(photographer);

            // then
            assertEquals(response.getBannerImageUrl(), "https://freebe/banner/origin");
            assertEquals(response.getProfileImageUrl(), "https://freebe/profile/thumbnail");
            assertEquals(response.getProfileName(), "uniqueName");
            assertEquals(response.getIntroductionContent(), "Welcome to my profile");
            List<LinkInfo> linkInfos = response.getLinkInfos();
            assertEquals(linkInfos.size(), 2);
            assertEquals(linkInfos.get(0).getLinkTitle(), "title1");
            assertEquals(linkInfos.get(1).getLinkTitle(), "title2");
        }
    }

    @Nested
    @DisplayName("프로필 업데이트 테스트")
    class ProfileUpdateTest {
        private Profile profile;
        private ProfileImage profileImage;
        private List<Link> links;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            profile = mock(Profile.class);
            profileImage = mock(ProfileImage.class);
            links = List.of(
                Link.builder().profile(profile).title("existingTitle1").url("existingUrl1").build(),
                Link.builder().profile(profile).title("existingTitle2").url("existingUrl2").build()
            );
        }

        private MockMultipartFile createMockMultipartFile(String name) throws IOException {
            return new MockMultipartFile(name, name + ".jpg", "image/jpeg", new byte[] {1, 2, 3});
        }

        @Test
        @DisplayName("(성공) 사진작가가 최초 회원가입 직후 프로필 정보를 등록한다")
        void testInitialUpdateProfile() throws IOException {
            // given
            when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(profile));
            when(profileImageRepository.findByProfile(profile)).thenReturn(Optional.of(profileImage));
            when(linkRepository.findByProfile(profile)).thenReturn(links);

            UpdateProfileRequest request = UpdateProfileRequest.builder()
                .introductionContent("Welcome to my profile")
                .linkInfos(List.of(
                    new LinkInfo("existingTitle1", "existingUrl1"),
                    new LinkInfo("newTitle1", "newUrl1")
                ))
                .build();
            MockMultipartFile bannerImageFile = createMockMultipartFile("banner");
            MockMultipartFile profileImageFile = createMockMultipartFile("profile");

            ImageLinkSet bannerImageLinkSet = new ImageLinkSet(Collections.singletonList("originUrl"), null);
            List<MultipartFile> bannerImageFiles = Collections.singletonList(bannerImageFile);
            when(
                s3ImageService.imageUploadToS3(bannerImageFiles, S3ImageType.PROFILE, photographer.getId())).thenReturn(
                bannerImageLinkSet);

            ImageLinkSet profileImageLinkSet = new ImageLinkSet(Collections.singletonList("originUrl"),
                Collections.singletonList("thumbnailUrl"));
            List<MultipartFile> profileImageFiles = Collections.singletonList(profileImageFile);
            when(s3ImageService.imageUploadToS3(profileImageFiles, S3ImageType.PROFILE, photographer.getId(),
                100)).thenReturn(profileImageLinkSet);

            // when
            profileService.updateProfile(photographer, request, bannerImageFile, profileImageFile);

            // then
            verify(profile).updateIntroductionContent("Welcome to my profile");
            verify(linkRepository, times(1)).delete(any(Link.class));
            verify(s3ImageService, never()).deleteImageFromS3(anyString());
            verify(profileImage).assignBannerOriginUrl(anyString());
            verify(profileImage).assignProfileOriginUrl(anyString());
            verify(profileImage).assignProfileThumbnailUrl(anyString());
            verify(profileImageRepository, times(2)).save(any(ProfileImage.class));
        }

        @Test
        @DisplayName("(성공) 사진작가가 프로필 정보를 새로 업데이트한다")
        void testUpdateProfile() throws IOException {
            // given
            ProfileImage existingProfileImage = ProfileImage.builder()
                .bannerOriginUrl("existingBannerOriginUrl")
                .profileOriginUrl("existingProfileOriginUrl")
                .profileThumbnailUrl("existingProfileThumbnailUrl")
                .build();

            when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(profile));
            when(profileImageRepository.findByProfile(profile)).thenReturn(Optional.of(existingProfileImage));
            when(linkRepository.findByProfile(profile)).thenReturn(links);

            UpdateProfileRequest request = UpdateProfileRequest.builder()
                .introductionContent("Welcome to my profile")
                .linkInfos(List.of(
                    new LinkInfo("existingTitle1", "existingUrl1"),
                    new LinkInfo("newTitle1", "newUrl1")
                ))
                .build();
            MockMultipartFile bannerImageFile = createMockMultipartFile("banner");
            MockMultipartFile profileImageFile = createMockMultipartFile("profile");

            ImageLinkSet bannerImageLinkSet = new ImageLinkSet(Collections.singletonList("newBannerOriginUrl"), null);
            List<MultipartFile> bannerImageFiles = Collections.singletonList(bannerImageFile);
            when(
                s3ImageService.imageUploadToS3(bannerImageFiles, S3ImageType.PROFILE, photographer.getId())).thenReturn(
                bannerImageLinkSet);
            
            ImageLinkSet profileImageLinkSet = new ImageLinkSet(Collections.singletonList("newProfileOriginUrl"),
                Collections.singletonList("newProfileThumbnailUrl"));
            List<MultipartFile> profileImageFiles = Collections.singletonList(profileImageFile);
            when(s3ImageService.imageUploadToS3(profileImageFiles, S3ImageType.PROFILE, photographer.getId(),
                100)).thenReturn(profileImageLinkSet);

            // when
            profileService.updateProfile(photographer, request, bannerImageFile, profileImageFile);

            // then
            verify(profile).updateIntroductionContent("Welcome to my profile");
            verify(linkRepository, times(1)).delete(any(Link.class));
            verify(s3ImageService, times(3)).deleteImageFromS3(anyString());
            assertEquals(existingProfileImage.getBannerOriginUrl(), "newBannerOriginUrl");
            assertEquals(existingProfileImage.getProfileOriginUrl(), "newProfileOriginUrl");
            assertEquals(existingProfileImage.getProfileThumbnailUrl(), "newProfileThumbnailUrl");
            verify(profileImageRepository, times(2)).save(any(ProfileImage.class));
        }

    }
}