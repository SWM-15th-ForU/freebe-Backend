package com.foru.freebe.product.service;

class PhotographerProductServiceTest {
    //
    // @Mock
    // private ProductRepository productRepository;
    //
    // @Mock
    // private ProductImageRepository productImageRepository;
    //
    // @Mock
    // private MemberRepository memberRepository;
    //
    // @Mock
    // private S3ImageService s3ImageService;
    //
    // @Mock
    // private ProductComponentRepository productComponentRepository;
    //
    // @Mock
    // private ProductOptionRepository productOptionRepository;
    //
    // @Mock
    // private ProductDiscountRepository productDiscountRepository;
    //
    // @InjectMocks
    // private PhotographerProductService photographerProductService;
    //
    // @BeforeEach
    // void setUp() {
    //     MockitoAnnotations.initMocks(this);
    // }
    //
    // @Test
    // @DisplayName("상품 상세정보 업데이트")
    // void testUpdateProductDetail() throws IOException {
    //     // Given
    //     String productTitle = "Updated Product Title";
    //     String productDescription = "Updated Product Description";
    //
    //     Member photographer = createNewMember();
    //     Long photographerId = photographer.getId();
    //     Product product = new Product("바다스냅", "안녕하세요", ActiveStatus.ACTIVE, photographer);
    //     Long productId = product.getId();
    //
    //     ProductImage productImage1 = ProductImage.createProductImage("existing_thumbnail_url_1",
    //         "existing_origin_url_1", product);
    //     ProductImage productImage2 = ProductImage.createProductImage("existing_thumbnail_url_2",
    //         "existing_origin_url_2", product);
    //
    //     List<ProductImage> productImages = Arrays.asList(productImage1, productImage2);
    //     List<String> existingUrls = Arrays.asList("existing_thumbnail_url_1", null);
    //
    //     ProductComponentDto productComponentDto = mock(ProductComponentDto.class);
    //     List<ProductComponentDto> productComponentDtoList = Arrays.asList(productComponentDto);
    //
    //     UpdateProductDetailRequest request = UpdateProductDetailRequest.builder()
    //         .productId(productId)
    //         .existingUrls(existingUrls)
    //         .productTitle(productTitle)
    //         .productDescription(productDescription)
    //         .productComponents(productComponentDtoList)
    //         .productOptions(Collections.emptyList())
    //         .productDiscounts(Collections.emptyList())
    //         .build();
    //
    //     // Mock MultipartFile
    //     MultipartFile image = mock(MultipartFile.class);
    //     List<MultipartFile> images = Arrays.asList(image);
    //     SingleImageLink singleImageLink = new SingleImageLink("new_origin_url", "new_thumbnail_url");
    //
    //     // Mock 행동 정의
    //     when(memberRepository.findById(photographerId)).thenReturn(Optional.of(photographer));
    //     when(productRepository.findByIdAndMember(productId, photographer)).thenReturn(Optional.of(product));
    //     when(productImageRepository.findByProduct(product)).thenReturn(productImages);
    //     when(productImageRepository.findByThumbnailUrl("existing_thumbnail_url_1"))
    //         .thenReturn(Optional.of(productImage1));
    //     when(s3ImageService.imageUploadToS3(any(MultipartFile.class), any(S3ImageType.class), anyLong(), true))
    //         .thenReturn(singleImageLink);
    //
    //     // 서비스 메서드 실행
    //     photographerProductService.updateProductDetail(images, request, photographerId);
    //
    //     // 검증
    //     verify(productRepository).findByIdAndMember(productId, photographer);
    //     verify(productImageRepository, times(1)).delete(productImage2);
    //     verify(s3ImageService, times(1)).deleteImageFromS3(productImage2.getOriginUrl());
    //     verify(s3ImageService, times(1)).deleteImageFromS3(productImage2.getThumbnailUrl());
    //     verify(productImageRepository, times(2)).save(any(ProductImage.class));
    // }
    //
    // private Member createNewMember() {
    //     return new Member(1L, Role.PHOTOGRAPHER, "John Doe", "john@example.com",
    //         "1234567890", 1980, "Male", "johndoe");
    // }
}