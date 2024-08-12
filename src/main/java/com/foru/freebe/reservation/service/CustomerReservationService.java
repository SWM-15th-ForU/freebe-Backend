package com.foru.freebe.reservation.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReferenceImageRepository;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ReferenceImageRepository referenceImageRepository;

    public ApiResponse<Void> registerReservationForm(Long customerId, FormRegisterRequest formRegisterRequest) {
        Member customer = findMember(customerId);
        Member photographer = findMember(formRegisterRequest.getPhotographerId());

        ReservationForm reservationForm = createReservationForm(formRegisterRequest, photographer, customer);

        validateReservationForm(formRegisterRequest);
        saveReservationForm(formRegisterRequest, reservationForm);

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Good Request")
            .data(null)
            .build();
    }

    private void saveReservationForm(FormRegisterRequest formRegisterRequest, ReservationForm reservationForm) {
        ReservationForm newReservationForm = reservationFormRepository.save(reservationForm);

        reservationHistoryRepository.save(
            ReservationHistory.updateReservationStatus(newReservationForm, ReservationStatus.NEW));

        formRegisterRequest.getPreferredImages()
            .stream()
            .map(referenceImage -> ReferenceImage.updateReferenceImage(referenceImage, reservationForm))
            .forEach(referenceImageRepository::save);
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private static ReservationForm createReservationForm(FormRegisterRequest request,
        Member photographer, Member customer) {
        ReservationForm.ReservationFormBuilder builder = ReservationForm.builder(photographer, customer,
                request.getInstagramId(), request.getProductTitle(), request.getTotalPrice(),
                request.getServiceTermAgreement(), request.getPhotographerTermAgreement(), ReservationStatus.NEW)
            .photoInfo(request.getPhotoInfo())
            .preferredDate(request.getPreferredDates())
            .customerMemo(request.getCustomerMemo());
        return builder.build();
    }

    private void validateReservationForm(FormRegisterRequest formRegisterRequest) {
        validateProductTitleExists(formRegisterRequest.getProductTitle());
        validateProductIsActive(formRegisterRequest.getProductTitle());
    }

    private void validateProductIsActive(String productTitle) {
        Product product = productRepository.findByTitle(productTitle);
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