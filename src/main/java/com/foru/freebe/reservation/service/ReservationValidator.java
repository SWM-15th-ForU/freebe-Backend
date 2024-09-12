package com.foru.freebe.reservation.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationValidator {
    private final ProductRepository productRepository;

    public void validateReservationFormBeforeSave(FormRegisterRequest formRegisterRequest) {
        validateProductTitleExists(formRegisterRequest.getProductTitle());
        validateProductIsActive(formRegisterRequest.getProductTitle());
    }

    public void validateCustomerAccess(ReservationForm reservationForm, Long customerId) {
        Long reservationCustomerId = reservationForm.getCustomer().getId();
        if (!reservationCustomerId.equals(customerId)) {
            throw new RestApiException(CommonErrorCode.ACCESS_DENIED);
        }
    }

    public void validateStatusChange(ReservationStatus currentStatus, ReservationStatus updateStatus) {
        if (currentStatus == ReservationStatus.NEW) {
            if (updateStatus != ReservationStatus.IN_PROGRESS && updateStatus != ReservationStatus.CANCELLED) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
            }
        } else if (currentStatus == ReservationStatus.IN_PROGRESS) {
            if (updateStatus != ReservationStatus.WAITING_FOR_DEPOSIT && updateStatus != ReservationStatus.CANCELLED) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
            }
        } else if (currentStatus == ReservationStatus.WAITING_FOR_DEPOSIT) {
            if (updateStatus != ReservationStatus.WAITING_FOR_PHOTO && updateStatus != ReservationStatus.CANCELLED) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
            }
        } else if (currentStatus == ReservationStatus.WAITING_FOR_PHOTO) {
            if (updateStatus != ReservationStatus.PHOTO_COMPLETED && updateStatus != ReservationStatus.CANCELLED) {
                throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
            }
        }
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
