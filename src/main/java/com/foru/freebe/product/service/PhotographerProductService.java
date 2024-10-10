package com.foru.freebe.product.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.ImageLinkSet;
import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.MemberErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.errorcode.ProductImageErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductDiscountDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequest;
import com.foru.freebe.product.dto.photographer.ProductTitleResponse;
import com.foru.freebe.product.dto.photographer.RegisteredProductResponse;
import com.foru.freebe.product.dto.photographer.UpdateProductDetailRequest;
import com.foru.freebe.product.dto.photographer.UpdateProductRequest;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;
import com.foru.freebe.product.entity.ProductDiscount;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductComponentRepository;
import com.foru.freebe.product.respository.ProductDiscountRepository;
import com.foru.freebe.product.respository.ProductImageRepository;
import com.foru.freebe.product.respository.ProductOptionRepository;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.s3.S3ImageService;
import com.foru.freebe.s3.S3ImageType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerProductService {
    private final S3ImageService s3ImageService;
    private final ProductDetailConvertor productDetailConvertor;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final MemberRepository memberRepository;
    private final ReservationFormRepository reservationFormRepository;

    @Transactional
    public void registerProduct(ProductRegisterRequest request, List<MultipartFile> images, Long photographerId) throws
        IOException {
        Member photographer = getMember(photographerId);

        Product productAsActive = registerActiveProduct(request, photographer);
        registerProductImage(images, productAsActive, photographerId);
        registerProductComponent(request.getProductComponents(), productAsActive);

        if (request.getProductOptions() != null) {
            registerProductOption(request.getProductOptions(), productAsActive);
        }

        if (request.getProductDiscounts() != null) {
            registerDiscount(request.getProductDiscounts(), productAsActive);
        }
    }

    public List<RegisteredProductResponse> getRegisteredProductList(Member member) {
        List<Product> registeredProductList = productRepository.findByMember(member);

        return registeredProductList.stream()
            .map(product -> RegisteredProductResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .representativeImage(getRepresentativeProductImage(product))
                .reservationCount(getReservationCount(member.getId(), product.getTitle()))
                .activeStatus(product.getActiveStatus())
                .build())
            .collect(Collectors.toList());
    }

    public ProductDetailResponse getRegisteredProductDetails(Long productId, Long photographerId) {
        Member photographer = getMember(photographerId);

        Product product = productRepository.findByIdAndMember(productId, photographer)
            .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return productDetailConvertor.convertProductToProductDetailResponse(product, false);
    }

    @Transactional
    public void updateProductActiveStatus(UpdateProductRequest requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));
        product.updateProductActiveStatus(requestDto.getActiveStatus());
    }

    @Transactional
    public void updateProductDetail(List<MultipartFile> images, UpdateProductDetailRequest updateProductDetailRequest,
        Long photographerId) throws IOException {
        Member photographer = getMember(photographerId);

        Product product = productRepository.findByIdAndMember(updateProductDetailRequest.getProductId(), photographer)
            .orElseThrow(() -> new RestApiException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (!Objects.equals(updateProductDetailRequest.getProductTitle(), product.getTitle())) {
            validateProductTitleBeforeRegister(updateProductDetailRequest.getProductTitle(), photographer);
        }

        product.assignTitle(updateProductDetailRequest.getProductTitle());
        product.assignDescription(updateProductDetailRequest.getProductDescription());
        product.assignBasicPrice(updateProductDetailRequest.getBasicPrice());

        updateProductImage(photographer.getId(), updateProductDetailRequest, images, product);
        updateProductCompositionExcludingImage(updateProductDetailRequest, product);
    }

    public List<ProductTitleResponse> getAllProductTitle(Long photographerId) {
        Member photographer = getMember(photographerId);

        List<Product> productList = productRepository.findByMember(photographer);

        return productList.stream()
            .map(product -> new ProductTitleResponse(product.getTitle()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long productId, Long photographerId) {
        Member photographer = getMember(photographerId);

        Product product = productRepository.findByIdAndMember(productId, photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        deleteEntityAboutProduct(product);
    }

    @Transactional
    public void deleteProductForUnlike(Product product) {
        deleteEntityAboutProduct(product);
    }

    private void deleteEntityAboutProduct(Product product) {
        List<ProductImage> productImages = productImageRepository.findByProduct(product);
        for (ProductImage productImage : productImages) {
            String originUrl = productImage.getOriginUrl();
            String thumbnailUrl = productImage.getThumbnailUrl();

            s3ImageService.deleteImageFromS3(originUrl);
            s3ImageService.deleteImageFromS3(thumbnailUrl);
        }

        productImageRepository.deleteByProduct(product);
        productComponentRepository.deleteByProduct(product);
        productOptionRepository.deleteByProduct(product);
        productDiscountRepository.deleteByProduct(product);

        productRepository.delete(product);
    }

    private void updateProductCompositionExcludingImage(UpdateProductDetailRequest updateProductDetailRequest,
        Product product) {
        List<ProductComponent> productComponents = productComponentRepository.findByProduct(product);
        List<ProductOption> productOptions = productOptionRepository.findByProduct(product);
        List<ProductDiscount> productDiscounts = productDiscountRepository.findByProduct(product);

        updateProductComponent(updateProductDetailRequest, productComponents, product);
        updateProductOption(updateProductDetailRequest, productOptions, product);
        updateProductDiscount(updateProductDetailRequest, productDiscounts, product);
    }

    private void updateProductImage(Long photographerId, UpdateProductDetailRequest request,
        List<MultipartFile> images, Product product) throws IOException {

        deleteRemovedImages(product, request.getExistingUrls());

        int imageIndex = 0;
        int order = 0;
        for (String existingUrl : request.getExistingUrls()) {
            if (existingUrl == null) {
                saveNewProductImage(order, images.get(imageIndex), photographerId, product);
                imageIndex++;
            } else {
                reorderAlreadyExistingImage(existingUrl, order);
            }
            order++;
        }
    }

    private void deleteRemovedImages(Product product, List<String> existingUrls) {
        List<ProductImage> productImages = productImageRepository.findByProduct(product);
        for (ProductImage productImage : productImages) {
            boolean found = existingUrls.stream()
                .anyMatch(existingUrl -> Objects.equals(productImage.getThumbnailUrl(), existingUrl));

            if (!found) {
                deleteImageOfAllTypeFromS3(productImage);
                productImageRepository.delete(productImage);
            }
        }
    }

    private void deleteImageOfAllTypeFromS3(ProductImage productImage) {
        s3ImageService.deleteImageFromS3(productImage.getOriginUrl());
        s3ImageService.deleteImageFromS3(productImage.getThumbnailUrl());
    }

    private void saveNewProductImage(int order, MultipartFile image, Long photographerId, Product product) throws
        IOException {
        SingleImageLink productImageLink = s3ImageService.imageUploadToS3(image, S3ImageType.PRODUCT, photographerId,
            true);

        ProductImage updateProductImage = ProductImage.createProductImage(order, productImageLink.getOriginalUrl(),
            productImageLink.getThumbnailUrl(), product);

        productImageRepository.save(updateProductImage);
    }

    private void reorderAlreadyExistingImage(String existingUrl, int order) {
        ProductImage productImage = productImageRepository.findByThumbnailUrl(existingUrl)
            .orElseThrow(() -> new RestApiException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));
        productImage.updateImageOrder(order);
    }

    private void updateProductDiscount(UpdateProductDetailRequest updateProductDetailRequest,
        List<ProductDiscount> productDiscounts, Product product) {
        productDiscountRepository.deleteAll(productDiscounts);
        List<ProductDiscountDto> updateProductDiscounts = updateProductDetailRequest.getProductDiscounts();
        for (ProductDiscountDto productDiscountDto : updateProductDiscounts) {
            ProductDiscount productDiscount = ProductDiscount.builder()
                .title(productDiscountDto.getTitle())
                .discountType(productDiscountDto.getDiscountType())
                .discountValue(productDiscountDto.getDiscountValue())
                .description(productDiscountDto.getDescription())
                .product(product)
                .build();

            productDiscountRepository.save(productDiscount);
        }
    }

    private void updateProductOption(UpdateProductDetailRequest updateProductDetailRequest,
        List<ProductOption> productOptions,
        Product product) {
        productOptionRepository.deleteAll(productOptions);
        List<ProductOptionDto> updateProductOptions = updateProductDetailRequest.getProductOptions();
        for (ProductOptionDto productOptionDto : updateProductOptions) {
            ProductOption productOption = ProductOption.builder()
                .title(productOptionDto.getTitle())
                .price(productOptionDto.getPrice())
                .description(productOptionDto.getDescription())
                .product(product)
                .build();

            productOptionRepository.save(productOption);
        }
    }

    private void updateProductComponent(UpdateProductDetailRequest updateProductDetailRequest,
        List<ProductComponent> productComponents, Product product) {
        productComponentRepository.deleteAll(productComponents);
        List<ProductComponentDto> updateProductComponents = updateProductDetailRequest.getProductComponents();
        for (ProductComponentDto productComponentDto : updateProductComponents) {
            ProductComponent productComponent = ProductComponent.builder()
                .title(productComponentDto.getTitle())
                .description(productComponentDto.getDescription())
                .content(productComponentDto.getContent())
                .product(product)
                .build();

            productComponentRepository.save(productComponent);
        }
    }

    private Integer getReservationCount(Long id, String productTitle) {
        return (int)reservationFormRepository.findAllByPhotographerIdAndProductTitle(id, productTitle).stream()
            .filter(form -> form.getReservationStatus() == ReservationStatus.PHOTO_COMPLETED)
            .count();
    }

    private Product registerActiveProduct(ProductRegisterRequest request, Member photographer) {
        String productTitle = request.getProductTitle();
        String productDescription = request.getProductDescription();
        Long basicPrice = request.getBasicPrice();

        Product productAsActive;
        if (productDescription != null) {
            productAsActive = Product.createProductAsActive(productTitle, productDescription, basicPrice, photographer);
        } else {
            productAsActive = Product.createProductAsActiveWithoutDescription(productTitle, basicPrice, photographer);
        }

        validateProductTitleBeforeRegister(productTitle, photographer);
        return productRepository.save(productAsActive);
    }

    private void validateProductTitleBeforeRegister(String productTitle, Member photographer) {
        if (productRepository.existsByMemberAndTitle(photographer, productTitle)) {
            throw new RestApiException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void registerProductImage(List<MultipartFile> images, Product product, Long photographerId) throws
        IOException {

        validateProductImage(images);

        ImageLinkSet productImageLinkSet = s3ImageService.imageUploadToS3(images, S3ImageType.PRODUCT, photographerId,
            true);
        saveProductImages(product, productImageLinkSet);
    }

    private void saveProductImages(Product product, ImageLinkSet productImageLinkSet) {
        List<String> originalImages = productImageLinkSet.getOriginUrls();
        List<String> thumbnailImages = productImageLinkSet.getThumbnailUrls();
        for (int i = 0; i < originalImages.size(); i++) {
            ProductImage productImage = ProductImage.createProductImage(i, originalImages.get(i),
                thumbnailImages.get(i), product);
            productImageRepository.save(productImage);
        }
    }

    private void validateProductImage(List<MultipartFile> images) {
        if (images.isEmpty()) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void registerProductComponent(List<ProductComponentDto> productComponentDtoList, Product product) {
        validateUniqueProductComponentTitle(productComponentDtoList);

        for (ProductComponentDto productComponentDto : productComponentDtoList) {
            ProductComponent productComponent = ProductComponent.builder()
                .title(productComponentDto.getTitle())
                .content(productComponentDto.getContent())
                .description(productComponentDto.getDescription())
                .product(product)
                .build();
            productComponentRepository.save(productComponent);
        }
    }

    private void validateUniqueProductComponentTitle(List<ProductComponentDto> productComponentDtoList) {
        List<String> componentTitle = productComponentDtoList
            .stream()
            .map(ProductComponentDto::getTitle)
            .toList();

        HashMap<String, Boolean> titleMap = new HashMap<>();
        for (String title : componentTitle) {
            if (!titleMap.containsKey(title)) {
                titleMap.put(title, true);
            } else {
                throw new RestApiException(ProductErrorCode.COMPONENT_TITLE_ALREADY_EXISTS);
            }
        }
    }

    private void registerProductOption(List<ProductOptionDto> productOptionDtoList, Product product) {
        for (ProductOptionDto productOptionDto : productOptionDtoList) {
            ProductOption productOption = ProductOption.builder()
                .title(productOptionDto.getTitle())
                .price(productOptionDto.getPrice())
                .description(productOptionDto.getDescription())
                .product(product)
                .build();

            productOptionRepository.save(productOption);
        }
    }

    private void registerDiscount(List<ProductDiscountDto> productDiscountDtoList, Product product) {
        for (ProductDiscountDto productDiscountDto : productDiscountDtoList) {
            ProductDiscount productDiscount = ProductDiscount.builder()
                .title(productDiscountDto.getTitle())
                .discountType(productDiscountDto.getDiscountType())
                .discountValue(productDiscountDto.getDiscountValue())
                .description(productDiscountDto.getDescription())
                .product(product)
                .build();
            productDiscountRepository.save(productDiscount);
        }
    }

    private String getRepresentativeProductImage(Product product) {
        List<ProductImage> productImage = productImageRepository.findByProduct(product);

        return productImage.stream()
            .filter(image -> image.getImageOrder() == 0)
            .map(ProductImage::getThumbnailUrl)
            .findFirst()
            .orElseThrow(() -> new RestApiException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));
    }
}
