package com.foru.freebe.reservation.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.errorcode.ReservationErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.entity.ReservationStatusTransition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationVerifier {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public void validateReservationFormBeforeSave(FormRegisterRequest formRegisterRequest) {
        validateProductTitleExists(formRegisterRequest);
        validateProductIsActive(formRegisterRequest.getProductTitle());
    }

    public void validateCustomerAccess(ReservationForm reservationForm, Long customerId) {
        Long reservationCustomerId = reservationForm.getCustomer().getId();
        if (!reservationCustomerId.equals(customerId)) {
            throw new RestApiException(CommonErrorCode.ACCESS_DENIED);
        }
    }

    public void validateStatusChange(ReservationStatus currentStatus, ReservationStatusUpdateRequest request,
        Boolean isPhotographer) {

        if (!isPhotographer) {
            validateCustomerAuthorityToChangeStatus(currentStatus, request.getUpdateStatus());
        }

        if (request.getUpdateStatus() == ReservationStatus.CANCELLED_BY_PHOTOGRAPHER
            || request.getUpdateStatus() == ReservationStatus.CANCELLED_BY_CUSTOMER) {
            validateCancellationReason(request);
        }

        validateStatusTransition(currentStatus, request.getUpdateStatus());
    }

    private void validateProductTitleExists(FormRegisterRequest request) {
        Long photographerId = request.getPhotographerId();
        Member photographer = memberRepository.findById(photographerId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (!productRepository.existsByMemberAndTitle(photographer, request.getProductTitle())) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private void validateProductIsActive(String productTitle) {
        Product product = productRepository.findByTitle(productTitle)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if (product.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new RestApiException(ProductErrorCode.PRODUCT_INACTIVE_STATUS);
        }
    }

    private void validateCustomerAuthorityToChangeStatus(ReservationStatus currentStatus,
        ReservationStatus updateStatus) {
        if (!(currentStatus == ReservationStatus.NEW && updateStatus == ReservationStatus.CANCELLED_BY_CUSTOMER)) {
            throw new RestApiException(ReservationErrorCode.INVALID_RESERVATION_STATUS_FOR_CANCELLATION);
        }
    }

    private void validateCancellationReason(ReservationStatusUpdateRequest request) {
        if (request.getCancellationReason() == null) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateStatusTransition(ReservationStatus currentStatus, ReservationStatus updateStatus) {
        ReservationStatusTransition transition = ReservationStatusTransition.valueOf(currentStatus.name());
        if (transition.isInvalidTransition(updateStatus)) {
            throw new RestApiException(ReservationErrorCode.INVALID_RESERVATION_STATUS_FOR_CANCELLATION);
        }
    }
}
