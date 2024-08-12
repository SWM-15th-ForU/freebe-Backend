package com.foru.freebe.reservation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.ProductComponentDto;
import com.foru.freebe.product.dto.ProductOptionDto;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductComponentRepository;
import com.foru.freebe.product.respository.ProductOptionRepository;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.BasicReservationInfoResponse;
import com.foru.freebe.reservation.dto.ReservationFormRequest;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReservationFormService {
    private final ReservationFormRepository reservationFormRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductOptionRepository productOptionRepository;

    public ApiResponse<Void> registerReservationForm(ReservationFormRequest reservationFormRequest) {
        Member customer = memberRepository.findById(reservationFormRequest.getCustomerId())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Member photographer = memberRepository.findById(reservationFormRequest.getPhotographerId())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        validateProductTitleExists(reservationFormRequest.getProductTitle());

        ReservationForm reservationForm = ReservationForm.builder()
            .photographer(photographer)
            .customer(customer)
            .instagramId(reservationFormRequest.getInstagramId())
            .productTitle(reservationFormRequest.getProductTitle())
            .photoInfo(reservationFormRequest.getPhotoInfo())
            .photoSchedule(reservationFormRequest.getPhotoSchedule())
            .requestMemo(reservationFormRequest.getRequestMemo())
            .totalPrice(reservationFormRequest.getTotalPrice())
            .serviceTermAgreement(reservationFormRequest.getServiceTermAgreement())
            .photographerTermAgreement(reservationFormRequest.getPhotographerTermAgreement())
            .reservationStatus(ReservationStatus.NEW)
            .build();

        validateActiveStatusOfProduct(reservationFormRequest);
        reservationFormRepository.save(reservationForm);

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Good Request")
            .data(null)
            .build();
    }

    // TODO 추후 사진작가의 촬영 오픈일정 관련 로직이 추가되면 예약신청서 작성할 때 사진작가의 일정 조회 로직이 필요함
    public ApiResponse<BasicReservationInfoResponse> getBasicReservationInfo(Long customerId, Long productId) {
        Member customer = memberRepository.findById(customerId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<ProductComponent> productComponents = productComponentRepository.findByProduct(product);
        List<ProductComponentDto> productComponentDtoList = convertProductComponentDtoList(
            productComponents);

        List<ProductOption> productOptions = productOptionRepository.findByProduct(product);
        List<ProductOptionDto> productOptionDtoList = convertProductOptionDtoList(productOptions);

        BasicReservationInfoResponse basicReservationInfoResponse = BasicReservationInfoResponse.builder()
            .name(customer.getName())
            .phoneNumber(customer.getPhoneNumber())
            .productComponentDtoList(productComponentDtoList)
            .productOptionDtoList(productOptionDtoList)
            .build();

        return ApiResponse.<BasicReservationInfoResponse>builder()
            .status(200)
            .message("Good Response")
            .data(basicReservationInfoResponse)
            .build();
    }

    private static List<ProductOptionDto> convertProductOptionDtoList(List<ProductOption> productOptions) {
        List<ProductOptionDto> productOptionDtoList = new ArrayList<>();
        for (ProductOption productOption : productOptions) {
            ProductOptionDto productOptionDto = ProductOptionDto.builder()
                .title(productOption.getTitle())
                .price(productOption.getPrice())
                .description(productOption.getDescription())
                .build();
            productOptionDtoList.add(productOptionDto);
        }
        return productOptionDtoList;
    }

    private static List<ProductComponentDto> convertProductComponentDtoList(List<ProductComponent> productComponents) {
        List<ProductComponentDto> productComponentDtoList = new ArrayList<>();
        for (ProductComponent productComponent : productComponents) {
            ProductComponentDto productComponentDto = ProductComponentDto.builder()
                .title(productComponent.getTitle())
                .content(productComponent.getContent())
                .description(productComponent.getDescription())
                .build();
            productComponentDtoList.add(productComponentDto);
        }
        return productComponentDtoList;
    }

    private void validateActiveStatusOfProduct(ReservationFormRequest reservationFormRequest) {
        Product product = productRepository.findByTitle(reservationFormRequest.getProductTitle());
        if (product.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new RestApiException(ProductErrorCode.PRODUCT_INACTIVE_STATUS);
        }
    }

    private void validateProductTitleExists(String productTitle) {
        if (!productRepository.existsByTitle(productTitle)) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
