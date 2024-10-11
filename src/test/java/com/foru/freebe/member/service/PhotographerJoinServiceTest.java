package com.foru.freebe.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("사진작가측 회원가입 테스트")
class PhotographerJoinServiceTest {
    // @Mock
    // private ProfileRepository profileRepository;
    //
    // @Mock
    // private LinkRepository linkRepository;
    //
    // @Mock
    // private ProfileImageRepository profileImageRepository;
    //
    // @Mock
    // private MemberRepository memberRepository;
    //
    // @Mock
    // private MemberTermAgreementRepository memberTermAgreementRepository;
    //
    // private PhotographerJoinService photographerJoinService;
    //
    // private ProfileService profileService;
    //
    // private Member photographer;
    //
    // @BeforeEach
    // void setUp() {
    //     MockitoAnnotations.openMocks(this);
    //     profileService = spy(
    //         new ProfileService(profileRepository, linkRepository, profileImageRepository));
    //     photographerJoinService = new PhotographerJoinService(profileService, memberRepository,
    //         memberTermAgreementRepository);
    //     photographer = Member.builder(1L, Role.PHOTOGRAPHER_PENDING, "이유리", "test@email", "010-0000-0000").build();
    // }
    //
    // @Test
    // @DisplayName("(성공) 사진작가가 회원가입을 진행한다")
    // void joinPhotographer() {
    //     // given
    //     PhotographerJoinRequest request = PhotographerJoinRequest.builder()
    //         .profileName("lee")
    //         .termsOfServiceAgreement(true)
    //         .privacyPolicyAgreement(true)
    //         .marketingAgreement(true)
    //         .build();
    //     when(memberRepository.save(any(Member.class))).thenReturn(photographer);
    //     when(profileService.initialProfileSetting(photographer, request.getProfileName()))
    //         .thenReturn(Profile.builder().profileName("profileName").build());
    //
    //     // when
    //     String profileName = photographerJoinService.joinPhotographer(photographer, request);
    //
    //     // then
    //     assertThat(profileName).isEqualTo("profileName");
    //     assertThat(photographer.getRole()).isEqualTo(Role.PHOTOGRAPHER);
    //     verify(memberTermAgreementRepository).save(any(MemberTermAgreement.class));
    //     verify(profileService).initialProfileSetting(any(Member.class), any(String.class));
    // }
}