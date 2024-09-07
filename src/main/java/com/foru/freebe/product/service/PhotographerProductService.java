package com.foru.freebe.product.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.common.service.S3ImageService;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductDiscountDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequest;
import com.foru.freebe.product.dto.photographer.RegisteredProductResponse;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final MemberRepository memberRepository;
    private final ReservationFormRepository reservationFormRepository;

    private final S3ImageService s3ImageService;

    public ApiResponse<Void> registerProduct(ProductRegisterRequest productRegisterRequestDto,
        List<MultipartFile> images, Long photographerId) throws IOException {
        Member member = getMember(photographerId);

        if (images.isEmpty()) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        Product productAsActive = registerActiveProduct(productRegisterRequestDto, member);
        registerProductImage(images, productAsActive);
        registerProductComponent(productRegisterRequestDto.getProductComponents(), productAsActive);

        if (productRegisterRequestDto.getProductOptions() != null) {
            registerProductOption(productRegisterRequestDto.getProductOptions(), productAsActive);
        }

        if (productRegisterRequestDto.getProductDiscounts() != null) {
            registerDiscount(productRegisterRequestDto.getProductDiscounts(), productAsActive);
        }

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Successfully added")
            .data(null)
            .build();
    }

    public ApiResponse<List<RegisteredProductResponse>> getRegisteredProductList(Member member) {
        List<Product> registeredProductList = productRepository.findByMember(member)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<RegisteredProductResponse> registeredProducts = registeredProductList.stream()
            .map(product -> RegisteredProductResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .reservationCount(0) // TODO 예약체결 관련 로직 구현 후 추가
                .reservationCount(getReservationCount(product.getTitle()))
                .activeStatus(product.getActiveStatus())
                .build())
            .collect(Collectors.toList());

        return ApiResponse.<List<RegisteredProductResponse>>builder()
            .status(200)
            .message("Successfully retrieved list of registered products")
            .data(registeredProducts)
            .build();
    }

    @Transactional
    public ApiResponse<Void> updateProductActiveStatus(UpdateProductRequest requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        product.updateProductActiveStatus(requestDto.getActiveStatus());

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Successfully updated product active status")
            .data(null)
            .build();
    }

    private Product registerActiveProduct(ProductRegisterRequest productRegisterRequestDto, Member member) {
        String productTitle = productRegisterRequestDto.getProductTitle();
        String productDescription = productRegisterRequestDto.getProductDescription();

        Product productAsActive;
        if (productDescription != null) {
            productAsActive = Product.createProductAsActive(productTitle, productDescription, member);
        } else {
            productAsActive = Product.createProductAsActiveWithoutDescription(productTitle, member);
        }

        validateProductTitle(productTitle);
        return productRepository.save(productAsActive);
    }

    private void validateProductTitle(String productTitle) {
        Product product = productRepository.findByTitle(productTitle);
        if (product != null) {
            throw new RestApiException(ProductErrorCode.PRODUCT_ALREADY_EXISTS);
        }
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private void registerProductImage(List<MultipartFile> images, Product product) throws IOException {
        List<String> originalImageUrls = s3ImageService.uploadOriginalImage(images);
        List<String> thumbnailImageUrls = s3ImageService.uploadThumbnailImage(images);

        IntStream.range(0, originalImageUrls.size()).forEach(i -> {
            ProductImage productImage = ProductImage.createProductImage(
                thumbnailImageUrls.get(i),
                originalImageUrls.get(i),
                product);
            productImageRepository.save(productImage);
        });
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
