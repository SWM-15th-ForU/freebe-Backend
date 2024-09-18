package com.foru.freebe.profile.service;

class ProfileServiceTest {

    //     private static final String UNIQUE_URL = "unique-url";
    //
    //     @Mock
    //     private MemberRepository memberRepository;
    //
    //     @Mock
    //     private ProfileRepository profileRepository;
    //
    //     @Mock
    //     private ProfileImageRepository profileImageRepository;
    //
    //     @Mock
    //     private LinkRepository linkRepository;
    //
    //     @Mock
    //     private S3ImageService s3ImageService;
    //
    //     @InjectMocks
    //     private ProfileService profileService;
    //
    //     @BeforeEach
    //     void setUp() {
    //         MockitoAnnotations.openMocks(this);
    //         profileService = spy(
    //             new ProfileService(memberRepository, profileRepository, linkRepository, profileImageRepository,
    //                 s3ImageService));
    //     }
    //
    //     @DisplayName("사진작가 측 현재 프로필 조회")
    //     @Test
    //     void testGetCurrentProfile() {
    //         // Given
    //         Member photographer = createNewMember();
    //         Profile profile = createProfile(photographer);
    //         ProfileImage profileImage = createProfileImage(profile);
    //
    //         Link link1 = createLink(profile, "My Portfolio", "http://portfolio.com");
    //         Link link2 = createLink(profile, "My Blog", "http://blog.com");
    //
    //         when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(profile));
    //         when(linkRepository.findByProfile(profile)).thenReturn(Arrays.asList(link1, link2));
    //         when(profileImageRepository.findByProfile(profile)).thenReturn(profileImage);
    //
    //         // When
    //         ApiResponse<ProfileResponse> response = profileService.getCurrentProfile(photographer);
    //         ProfileResponse result = response.getData();
    //
    //         // Then
    //         assertNotNull(result);
    //         assertEquals("profile.jpg", result.getProfileImageUrl());
    //         assertEquals("banner.jpg", result.getBannerImageUrl());
    //         assertEquals("johndoe", result.getInstagramId());
    //         assertEquals("Welcome to my profile!", result.getIntroductionContent());
    //
    //         List<LinkInfo> linkInfos = result.getLinkInfos();
    //         assertNotNull(linkInfos);
    //         assertEquals(2, linkInfos.size());
    //
    //         LinkInfo linkInfo1 = linkInfos.get(0);
    //         assertEquals("My Portfolio", linkInfo1.getLinkTitle());
    //         assertEquals("http://portfolio.com", linkInfo1.getLinkUrl());
    //
    //         LinkInfo linkInfo2 = linkInfos.get(1);
    //         assertEquals("My Blog", linkInfo2.getLinkTitle());
    //         assertEquals("http://blog.com", linkInfo2.getLinkUrl());
    //     }
    //
    //     @DisplayName("사진작가 측 외부 링크를 제외한 프로필 업데이트")
    //     @Test
    //     void testUpdateProfile() throws IOException {
    //         // Given
    //         Member photographer = createNewMember();
    //         Profile existingProfile = createProfile(photographer);
    //         ProfileImage profileImage = createProfileImage(existingProfile);
    //
    //         List<LinkInfo> linkInfos = Arrays.asList(
    //             new LinkInfo("changed blog", "www.url.com"),
    //             new LinkInfo("My Portfolio", "www.change.com"),
    //             new LinkInfo("new info", "nice"));
    //
    //         UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
    //             .bannerImageUrl("banner.jpg")
    //             .introductionContent("changed content")
    //             .linkInfos(linkInfos)
    //             .build();
    //
    //         MockMultipartFile requestImage = new MockMultipartFile("file", "profile_change.jpg", "image/jpeg",
    //             "file contents".getBytes());
    //
    //         // doNothing().when(profileService).updateLinks(any(Profile.class), anyList());
    //         when(profileRepository.findByMember(photographer)).thenReturn(Optional.of(existingProfile));
    //
    //         // When
    //         profileService.updateProfile(updateRequest, photographer, requestImage);
    //         assertEquals("profile_change.jpg", profileImage.getOriginUrl());
    //         assertEquals("changed content", existingProfile.getIntroductionContent());
    //         assertEquals("banner.jpg", existingProfile.getBannerImageUrl());
    //     }
    //
    //     private ProfileImage createProfileImage(Profile existingProfile) {
    //         return ProfileImage.builder()
    //             .profile(existingProfile)
    //             .thumbnailUrl("http://thumbnails.com")
    //             .originUrl("http://originurl.com")
    //             .build();
    //     }
    //
    //     @DisplayName("사진작가 측 프로필의 외부 링크 업데이트")
    //     @Test
    //     void testUpdateLinks() throws IOException {
    //         Member photographer = createNewMember();
    //         Profile existingProfile = createProfile(photographer);
    //
    //         Link link1 = createLink(existingProfile, "Naver Blog", "www.naver.blog");
    //         Link link2 = createLink(existingProfile, "Pinterest", "www.pinterest.com");
    //
    //         List<LinkInfo> linkInfos = Arrays.asList(
    //             new LinkInfo("changed blog", "www.url.com"),
    //             new LinkInfo("Naver Blog", "www.change.com"),
    //             new LinkInfo("new info", "nice"));
    //
    //         UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
    //             .bannerImageUrl("banner.jpg")
    //             .introductionContent("Welcome to my profile!")
    //             .linkInfos(linkInfos)
    //             .build();
    //
    //         MockMultipartFile requestImage = new MockMultipartFile("file", "profile_change.jpg", "image/jpeg",
    //             "file contents".getBytes());
    //
    //         when(linkRepository.findByProfile(existingProfile)).thenReturn(Arrays.asList(link1, link2));
    //
    //         // When
    //         profileService.updateProfile(updateRequest, photographer, requestImage);
    //
    //         // Then
    //         verify(linkRepository).save(argThat(link ->
    //             "changed blog".equals(link.getTitle()) && "www.url.com".equals(link.getUrl())));
    //         verify(linkRepository).save(argThat(link ->
    //             "new info".equals(link.getTitle()) && "nice".equals(link.getUrl())));
    //
    //         verify(linkRepository, times(2)).save(any(Link.class));
    //     }
    //
    //     private Member createNewMember() {
    //         return new Member(1L, Role.PHOTOGRAPHER, "John Doe", "john@example.com",
    //             "1234567890", 1980, "Male", "johndoe");
    //     }
    //
    //     private Profile createProfile(Member photographer) {
    //         return Profile.builder()
    //             .uniqueUrl(UNIQUE_URL)
    //             .bannerImageUrl("banner.jpg")
    //             .introductionContent("Welcome to my profile!")
    //             .member(photographer)
    //             .build();
    //     }
    //
    //     private Link createLink(Profile profile, String title, String url) {
    //         return Link.builder()
    //             .profile(profile)
    //             .title(title)
    //             .url(url)
    //             .build();
    //     }
}