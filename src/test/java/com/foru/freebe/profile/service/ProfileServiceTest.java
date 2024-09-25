package com.foru.freebe.profile.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
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

class ProfileServiceTest {

    private static final String PROFILE_NAME = "unique-profile-name";

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private S3ImageService s3ImageService;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = spy(
            new ProfileService(profileRepository, linkRepository, profileImageRepository, s3ImageService));
    }

    @DisplayName("사진작가 측 현재 프로필 조회")
    @Test
    void testGetCurrentProfile() {
        // Given
        Member photographer = createNewMember();
        Profile profile = createProfile(photographer);
        ProfileImage profileImage = createProfileImage(profile);

        Link link1 = createLink(profile, "My Portfolio", "http://portfolio.com");
        Link link2 = createLink(profile, "My Blog", "http://blog.com");

        when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(profile));
        when(linkRepository.findByProfile(profile)).thenReturn(Arrays.asList(link1, link2));
        when(profileImageRepository.findByProfile(profile)).thenReturn(Optional.ofNullable(profileImage));

        // When
        ProfileResponse result = profileService.getCurrentProfile(photographer);

        // Then
        assertNotNull(result);
        assertEquals("http://thumbnails.com", result.getProfileImageUrl());
        assertEquals("banner.jpg", result.getBannerImageUrl());
        assertEquals("unique-profile-name", result.getProfileName());
        assertEquals("Welcome to my profile!", result.getIntroductionContent());

        List<LinkInfo> linkInfos = result.getLinkInfos();
        assertNotNull(linkInfos);
        assertEquals(2, linkInfos.size());

        LinkInfo linkInfo1 = linkInfos.get(0);
        assertEquals("My Portfolio", linkInfo1.getLinkTitle());
        assertEquals("http://portfolio.com", linkInfo1.getLinkUrl());

        LinkInfo linkInfo2 = linkInfos.get(1);
        assertEquals("My Blog", linkInfo2.getLinkTitle());
        assertEquals("http://blog.com", linkInfo2.getLinkUrl());
    }

    @DisplayName("사진작가 측 외부 링크를 제외한 프로필 업데이트")
    @Test
    void testUpdateProfile() throws IOException {
        // Given
        Member photographer = createNewMember();
        Profile existingProfile = createProfile(photographer);
        ProfileImage profileImage = createProfileImage(existingProfile);

        List<LinkInfo> linkInfos = Arrays.asList(
            new LinkInfo("changed blog", "www.url.com"),
            new LinkInfo("My Portfolio", "www.change.com"),
            new LinkInfo("new info", "nice"));

        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
            .bannerImageUrl("banner.jpg")
            .introductionContent("changed content")
            .linkInfos(linkInfos)
            .build();

        MockMultipartFile requestImage = new MockMultipartFile("file", "profile_change.jpg", "image/jpeg",
            "file contents".getBytes());

        when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(existingProfile));
        when(memberRepository.findById(photographer.getId())).thenReturn(Optional.of(photographer));

        // When
        profileService.updateProfile(updateRequest, photographer, requestImage);

        // then
        assertEquals("http://originurl.com", profileImage.getOriginUrl());
        assertEquals("changed content", existingProfile.getIntroductionContent());
        assertEquals("banner.jpg", existingProfile.getBannerImageUrl());
    }

    private ProfileImage createProfileImage(Profile existingProfile) {
        return ProfileImage.builder()
            .profile(existingProfile)
            .thumbnailUrl("http://thumbnails.com")
            .originUrl("http://originurl.com")
            .build();
    }

    @DisplayName("사진작가 측 프로필의 외부 링크 업데이트")
    @Test
    void testUpdateLinks() throws IOException {
        // Given
        Member photographer = createNewMember();
        Profile existingProfile = createProfile(photographer);

        Link link1 = createLink(existingProfile, "Naver Blog", "www.naver.blog");
        Link link2 = createLink(existingProfile, "Pinterest", "www.pinterest.com");

        List<LinkInfo> linkInfos = Arrays.asList(
            new LinkInfo("changed blog", "www.url.com"),
            new LinkInfo("Naver Blog", "www.change.com"),
            new LinkInfo("new info", "nice"));

        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
            .bannerImageUrl("banner.jpg")
            .introductionContent("Welcome to my profile!")
            .linkInfos(linkInfos)
            .build();

        MockMultipartFile requestImage = new MockMultipartFile("file", "profile_change.jpg", "image/jpeg",
            "file contents".getBytes());

        when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(existingProfile));
        when(linkRepository.findByProfile(existingProfile)).thenReturn(Arrays.asList(link1, link2));
        when(memberRepository.findById(photographer.getId())).thenReturn(Optional.of(photographer));

        // When
        profileService.updateProfile(updateRequest, photographer, requestImage);

        // Then
        verify(linkRepository).save(argThat(link ->
            "changed blog".equals(link.getTitle()) && "www.url.com".equals(link.getUrl())));
        verify(linkRepository).save(argThat(link ->
            "new info".equals(link.getTitle()) && "nice".equals(link.getUrl())));

        verify(linkRepository, times(2)).save(any(Link.class));
    }

    private Member createNewMember() {
        return new Member(1L, Role.PHOTOGRAPHER, "John Doe", "john@example.com",
            "1234567890", 1980, "Male", "johndoe");
    }

    private Profile createProfile(Member photographer) {
        return Profile.builder()
            .profileName(PROFILE_NAME)
            .bannerImageUrl("banner.jpg")
            .introductionContent("Welcome to my profile!")
            .member(photographer)
            .build();
    }

    private Link createLink(Profile profile, String title, String url) {
        return Link.builder()
            .profile(profile)
            .title(title)
            .url(url)
            .build();
    }
}