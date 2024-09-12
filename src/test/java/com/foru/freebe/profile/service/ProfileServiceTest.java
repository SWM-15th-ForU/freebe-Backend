package com.foru.freebe.profile.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.profile.dto.LinkInfo;
import com.foru.freebe.profile.dto.ProfileResponse;
import com.foru.freebe.profile.dto.UpdateProfileRequest;
import com.foru.freebe.profile.entity.Link;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileRepository;

class ProfileServiceTest {

    private static final String UNIQUE_URL = "unique-url";

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = spy(new ProfileService(memberRepository, profileRepository, linkRepository));
    }

    @DisplayName("사진작가 측 현재 프로필 조회")
    @Test
    void testGetCurrentProfile() {
        // Given
        Member photographer = createNewMember();
        Profile profile = createProfile(photographer);

        Link link1 = createLink(profile, "My Portfolio", "http://portfolio.com");
        Link link2 = createLink(profile, "My Blog", "http://blog.com");

        when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(profile));
        when(linkRepository.findByProfile(profile)).thenReturn(Arrays.asList(link1, link2));

        // When
        ApiResponse<ProfileResponse> response = profileService.getCurrentProfile(photographer);
        ProfileResponse result = response.getData();

        // Then
        assertNotNull(result);
        assertEquals("profile.jpg", result.getProfileImageUrl());
        assertEquals("banner.jpg", result.getBannerImageUrl());
        assertEquals("johndoe", result.getInstagramId());
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
    void testUpdateProfile() {
        // Given
        Member photographer = createNewMember();
        Profile existingProfile = createProfile(photographer);

        List<LinkInfo> linkInfos = Arrays.asList(
            new LinkInfo("changed blog", "www.url.com"),
            new LinkInfo("My Portfolio", "www.change.com"),
            new LinkInfo("new info", "nice"));

        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
            .profileImageUrl("profile_change.jpg")
            .bannerImageUrl("banner.jpg")
            .introductionContent("changed content")
            .linkInfos(linkInfos)
            .build();

        doNothing().when(profileService).updateLinks(any(Profile.class), anyList());
        when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(existingProfile));

        // When
        ApiResponse<Void> response = profileService.updateProfile(updateRequest, photographer);
        assertEquals(200, response.getStatus());
        assertEquals("profile_change.jpg", existingProfile.getProfileImageUrl());
        assertEquals("changed content", existingProfile.getIntroductionContent());
        assertEquals("banner.jpg", existingProfile.getBannerImageUrl());

        verify(profileService).updateLinks(existingProfile, updateRequest.getLinkInfos());
    }

    @DisplayName("사진작가 측 프로필의 외부 링크 업데이트")
    @Test
    void testUpdateLinks() {
        Member photographer = createNewMember();
        Profile existingProfile = createProfile(photographer);

        Link link1 = createLink(existingProfile, "Naver Blog", "www.naver.blog");
        Link link2 = createLink(existingProfile, "Pinterest", "www.pinterest.com");

        List<LinkInfo> linkInfos = Arrays.asList(
            new LinkInfo("changed blog", "www.url.com"),
            new LinkInfo("Naver Blog", "www.change.com"),
            new LinkInfo("new info", "nice"));

        when(linkRepository.findByProfile(existingProfile)).thenReturn(Arrays.asList(link1, link2));

        // When
        profileService.updateLinks(existingProfile, linkInfos);

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
            .uniqueUrl(UNIQUE_URL)
            .profileImageUrl("profile.jpg")
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