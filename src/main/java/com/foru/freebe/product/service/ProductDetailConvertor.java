package com.foru.freebe.product.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.notice.dto.NoticeDto;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductDiscountDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;
import com.foru.freebe.product.entity.ProductDiscount;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductComponentRepository;
import com.foru.freebe.product.respository.ProductDiscountRepository;
import com.foru.freebe.product.respository.ProductImageRepository;
import com.foru.freebe.product.respository.ProductOptionRepository;
import com.foru.freebe.reservation.dto.PhotoNotice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductDetailConvertor {

    private final ProductImageRepository productImageRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductDiscountRepository productDiscountRepository;

    public ProductDetailResponse convertProductToProductDetailResponse(Product product, Boolean isOrigin) {
        List<String> productImageUrls = getProductImageUrls(product, isOrigin);
        List<ProductComponentDto> productComponents = convertToProductComponentDtoList(product);
        List<ProductOptionDto> productOptions = convertToProductOptionDtoList(product);
        List<ProductDiscountDto> productDiscounts = convertToProductDiscountDtoList(product);
        List<NoticeDto> notices = convertToNoticeDtoList(product.getPhotoNotice());

        return ProductDetailResponse.builder()
            .productTitle(product.getTitle())
            .productDescription(product.getDescription())
            .basicPrice(product.getBasicPrice())
            .basicPlace(product.getBasicPlace())
            .notices(notices)
            .allowPreferredPlace(product.getAllowPreferredPlace())
            .productImageUrls(productImageUrls)
            .productComponents(productComponents)
            .productOptions(productOptions)
            .productDiscounts(productDiscounts)
            .build();
    }

    public List<NoticeDto> convertToNoticeDtoList(Map<String, PhotoNotice> photoNoticeMap) {
        return photoNoticeMap.values().stream()
            .map(photoNotice -> new NoticeDto(photoNotice.getTitle(), photoNotice.getContent()))
            .collect(Collectors.toList());
    }

    private List<ProductDiscountDto> convertToProductDiscountDtoList(Product product) {
        List<ProductDiscount> productDiscounts = productDiscountRepository.findByProduct(product);
        List<ProductDiscountDto> productDiscountResponse = new ArrayList<>();
        for (ProductDiscount productDiscount : productDiscounts) {
            ProductDiscountDto productDiscountDto = ProductDiscountDto.builder()
                .title(productDiscount.getTitle())
                .discountType(productDiscount.getDiscountType())
                .discountValue(productDiscount.getDiscountValue())
                .description(productDiscount.getDescription())
                .build();
            productDiscountResponse.add(productDiscountDto);
        }
        return productDiscountResponse;
    }

    public List<ProductOptionDto> convertToProductOptionDtoList(Product product) {
        List<ProductOption> productOptions = productOptionRepository.findByProduct(product);
        List<ProductOptionDto> productOptionResponse = new ArrayList<>();
        for (ProductOption productOption : productOptions) {
            ProductOptionDto productOptionDto = ProductOptionDto.builder()
                .title(productOption.getTitle())
                .price(productOption.getPrice())
                .description(productOption.getDescription())
                .build();
            productOptionResponse.add(productOptionDto);
        }
        return productOptionResponse;
    }

    public List<ProductComponentDto> convertToProductComponentDtoList(Product product) {
        List<ProductComponent> productComponents = productComponentRepository.findByProduct(product);
        List<ProductComponentDto> productComponentResponse = new ArrayList<>();
        for (ProductComponent productComponent : productComponents) {
            ProductComponentDto productComponentDto = ProductComponentDto.builder()
                .title(productComponent.getTitle())
                .content(productComponent.getContent())
                .description(productComponent.getDescription())
                .build();
            productComponentResponse.add(productComponentDto);
        }
        return productComponentResponse;
    }

    private List<String> getProductImageUrls(Product product, Boolean isOrigin) {
        List<ProductImage> productImages = productImageRepository.findByProduct(product)
            .stream()
            .sorted(Comparator.comparing(ProductImage::getImageOrder))
            .toList();

        List<String> productImageUrls = new ArrayList<>();
        if (isOrigin) {
            for (ProductImage productImage : productImages) {
                productImageUrls.add(productImage.getOriginUrl());
            }
        } else {
            for (ProductImage productImage : productImages) {
                productImageUrls.add(productImage.getThumbnailUrl());
            }
        }
        return productImageUrls;
    }
}
