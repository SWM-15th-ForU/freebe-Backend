package com.foru.freebe.reservation.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.errorcode.ReservationErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.profile.entity.Profile;
import com.foru.freebe.profile.repository.ProfileRepository;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.dto.TimeSlot;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.entity.ReservationStatusTransition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationVerifier {
    private final ProductRepository productRepository;
    private final ProfileRepository profileRepository;

    public void validateReservationFormBeforeSave(FormRegisterRequest request) {
        validateProductTitleExists(request);
        validateProductIsActive(request.getProductTitle());
    }

    public void validateCustomerAccess(ReservationForm reservationForm, Long customerId) {
        Long reservationCustomerId = reservationForm.getCustomer().getId();
        if (!reservationCustomerId.equals(customerId)) {
            throw new RestApiException(CommonErrorCode.ACCESS_DENIED);
        }
    }

    public void validateStatusChange(ReservationStatus currentStatus, ReservationStatusUpdateRequest request,
        Boolean isPhotographer, TimeSlot shootingDate) {

        if (!isPhotographer) {
            validateCustomerAuthorityToChangeStatus(currentStatus, request.getUpdateStatus());
        }

        if (request.getUpdateStatus() == ReservationStatus.WAITING_FOR_DEPOSIT
            || request.getUpdateStatus() == ReservationStatus.WAITING_FOR_PHOTO
            || request.getUpdateStatus() == ReservationStatus.PHOTO_COMPLETED) {
            if (shootingDate == null) {
                throw new RestApiException(ReservationErrorCode.INVALID_STATUS_TRANSITION);
            }
        }

        if (request.getUpdateStatus() == ReservationStatus.CANCELLED_BY_PHOTOGRAPHER
            || request.getUpdateStatus() == ReservationStatus.CANCELLED_BY_CUSTOMER) {
            validateCancellationReason(request);
        }

        validateStatusTransition(currentStatus, request.getUpdateStatus());
    }

    private void validateProductTitleExists(FormRegisterRequest request) {
        Profile photographerProfile = profileRepository.findByProfileName(request.getProfileName())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        Member photographer = photographerProfile.getMember();

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
            throw new RestApiException(ReservationErrorCode.INVALID_STATUS_TRANSITION);
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
            throw new RestApiException(ReservationErrorCode.INVALID_STATUS_TRANSITION);
        }
    }
}
