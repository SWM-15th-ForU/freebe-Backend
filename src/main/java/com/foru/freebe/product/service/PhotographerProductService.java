package com.foru.freebe.product.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.ImageLinkSet;
import com.foru.freebe.common.dto.SingleImageLink;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductDiscountDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequest;
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
    private static final Logger log = LoggerFactory.getLogger(PhotographerProductService.class);

    private final ProductDetailConvertor productDetailConvertor;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final MemberRepository memberRepository;
    private final ReservationFormRepository reservationFormRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void registerProduct(ProductRegisterRequest productRegisterRequestDto,
        List<MultipartFile> images, Long photographerId) throws IOException {
        Member photographer = getMember(photographerId);

        Product productAsActive = registerActiveProduct(productRegisterRequestDto, photographer);
        registerProductImage(images, productAsActive, photographerId);
        registerProductComponent(productRegisterRequestDto.getProductComponents(), productAsActive);

        if (productRegisterRequestDto.getProductOptions() != null) {
            registerProductOption(productRegisterRequestDto.getProductOptions(), productAsActive);
        }

        if (productRegisterRequestDto.getProductDiscounts() != null) {
            registerDiscount(productRegisterRequestDto.getProductDiscounts(), productAsActive);
        }
    }

    public List<RegisteredProductResponse> getRegisteredProductList(Member member) {
        List<Product> registeredProductList = productRepository.findByMember(member);

        return registeredProductList.stream()
            .map(product -> RegisteredProductResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .reservationCount(getReservationCount(member.getId(), product.getTitle()))
                .activeStatus(product.getActiveStatus())
                .build())
            .collect(Collectors.toList());
    }

    public ProductDetailResponse getRegisteredProductInfo(Long productId, Long photographerId) {
        Member photographer = getMember(photographerId);

        Product product = productRepository.findByIdAndMember(productId, photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        return productDetailConvertor.convertProductToProductDetailResponse(product);
    }

    @Transactional
    public void updateProductActiveStatus(UpdateProductRequest requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        product.updateProductActiveStatus(requestDto.getActiveStatus());
    }

    @Transactional
    public void updateProductDetail(List<MultipartFile> images, UpdateProductDetailRequest updateProductDetailRequest,
        Long photographerId) throws IOException {
        Member photographer = getMember(photographerId);

        Product product = productRepository.findByIdAndMember(updateProductDetailRequest.getProductId(), photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (!Objects.equals(updateProductDetailRequest.getProductTitle(), product.getTitle())) {
            validateProductTitleBeforeRegister(updateProductDetailRequest.getProductTitle(), photographer);
        }

        product.assignTitle(updateProductDetailRequest.getProductTitle());
        product.assignDescription(updateProductDetailRequest.getProductDescription());

        List<ProductImage> productImages = productImageRepository.findByProduct(product);
        deleteSelectedImageByUser(updateProductDetailRequest, productImages);

        int newImageCount = 0;
        for (String existingUrl : updateProductDetailRequest.getExistingUrls()) {
            if (isExistingImage(existingUrl)) {
                rearrangeOrderOfExistingUrl(existingUrl, product);
            } else {
                saveNewProductImage(images.get(newImageCount), photographerId, product);
                newImageCount += 1;
            }
        }

        updateProductCompositionExcludingImage(updateProductDetailRequest, product);
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

    private void saveNewProductImage(MultipartFile image, Long photographerId, Product product) throws IOException {
        SingleImageLink productImageLink = s3ImageService.imageUploadToS3(image, S3ImageType.PRODUCT, photographerId,
            true);

        ProductImage updateProductImage = ProductImage.createProductImage(productImageLink.getThumbnailUrl(),
            productImageLink.getOriginalUrl(), product);

        productImageRepository.save(updateProductImage);
    }

    private static boolean isExistingImage(String existingUrl) {
        return existingUrl != null;
    }

    private void rearrangeOrderOfExistingUrl(String existingUrl, Product product) {
        ProductImage productImage = productImageRepository.findByThumbnailUrl(existingUrl)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        updateAndReplaceProductImage(productImage.getThumbnailUrl(), productImage.getOriginUrl(), product,
            productImage.getId());
    }

    private void deleteSelectedImageByUser(UpdateProductDetailRequest updateProductDetailRequest,
        List<ProductImage> productImages) {
        for (ProductImage productImage : productImages) {
            boolean found = updateProductDetailRequest.getExistingUrls().stream()
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

    private void updateAndReplaceProductImage(String thumbnailUrl, String originUrl, Product product,
        Long oldImageId) {
        ProductImage updateProductImage = ProductImage.createProductImage(thumbnailUrl, originUrl, product);
        productImageRepository.save(updateProductImage);
        productImageRepository.deleteById(oldImageId);
    }

    @Transactional
    public void deleteProduct(Long productId, Long photographerId) {
        Member photographer = getMember(photographerId);

        Product product = productRepository.findByIdAndMember(productId, photographer)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

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

    private Product registerActiveProduct(ProductRegisterRequest productRegisterRequestDto, Member photographer) {
        String productTitle = productRegisterRequestDto.getProductTitle();
        String productDescription = productRegisterRequestDto.getProductDescription();

        Product productAsActive;
        if (isExistingImage(productDescription)) {
            productAsActive = Product.createProductAsActive(productTitle, productDescription, photographer);
        } else {
            productAsActive = Product.createProductAsActiveWithoutDescription(productTitle, photographer);
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
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
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
            ProductImage productImage = ProductImage.createProductImage(originalImages.get(i), thumbnailImages.get(i),
                product);
            productImageRepository.save(productImage);
        }
    }

    private void validateProductImage(List<MultipartFile> images) {
        if (images.isEmpty()) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private void registerProductComponent(List<ProductComponentDto> productComponentDtoList, Product product) {
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
}
