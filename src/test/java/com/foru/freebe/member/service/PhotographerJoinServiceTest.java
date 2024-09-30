package com.foru.freebe.member.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foru.freebe.member.dto.PhotographerJoinRequest;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.entity.MemberTermAgreement;
import com.foru.freebe.member.entity.Role;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.member.repository.MemberTermAgreementRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.LinkRepository;
import com.foru.freebe.profile.repository.ProfileImageRepository;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.profile.service.ProfileService;
import com.foru.freebe.s3.S3ImageService;

@ExtendWith(MockitoExtension.class)
@DisplayName("사진작가측 회원가입 테스트")
class PhotographerJoinServiceTest {
    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ProfileImageRepository profileImageRepository;

    @Mock
    private S3ImageService s3ImageService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberTermAgreementRepository memberTermAgreementRepository;

    private PhotographerJoinService photographerJoinService;

    private ProfileService profileService;

    private Member photographer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = spy(
            new ProfileService(profileRepository, linkRepository, profileImageRepository, s3ImageService));
        photographerJoinService = new PhotographerJoinService(profileService, memberRepository,
            memberTermAgreementRepository);
        photographer = Member.builder(1L, Role.PHOTOGRAPHER_PENDING, "이유리", "test@email", "010-0000-0000").build();
    }

    @Test
    @DisplayName("(성공) 사진작가가 회원가입을 진행한다")
    void joinPhotographer() {
        // given
        PhotographerJoinRequest request = PhotographerJoinRequest.builder()
            .profileName("lee")
            .termsOfServiceAgreement(true)
            .privacyPolicyAgreement(true)
            .marketingAgreement(true)
            .build();
        when(memberRepository.save(any(Member.class))).thenReturn(photographer);
        when(profileService.initialProfileSetting(photographer, request.getProfileName()))
            .thenReturn(Profile.builder().profileName("profileName").build());

        // when
        String profileName = photographerJoinService.joinPhotographer(photographer, request);

        // then
        assertThat(profileName).isEqualTo("profileName");
        assertThat(photographer.getRole()).isEqualTo(Role.PHOTOGRAPHER);
        verify(memberTermAgreementRepository).save(any(MemberTermAgreement.class));
        verify(profileService).initialProfileSetting(any(Member.class), any(String.class));
    }
}