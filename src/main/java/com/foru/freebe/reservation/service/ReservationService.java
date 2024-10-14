package com.foru.freebe.reservation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.reservation.dto.CustomerCancelInfo;
import com.foru.freebe.reservation.dto.CustomerCancelledInfo;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.dto.StatusHistory;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationVerifier reservationVerifier;
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    @Transactional
    public void updateReservationStatus(Long memberId, Long formId,
        ReservationStatusUpdateRequest request, Boolean isPhotographer) {

        ReservationForm reservationForm = findReservationForm(memberId, formId, isPhotographer);
        ReservationStatus currentStatus = reservationForm.getReservationStatus();
        ReservationStatus updateStatus = request.getUpdateStatus();

        reservationVerifier.validateStatusChange(currentStatus, request, isPhotographer,
            reservationForm.getShootingDate());
        reservationForm.changeReservationStatus(updateStatus);

        ReservationHistory reservationHistory = updateReservationHistory(request, reservationForm);

        reservationFormRepository.save(reservationForm);
        reservationHistoryRepository.save(reservationHistory);
    }

    public ReservationForm findReservationForm(Long id, Long formId, Boolean isPhotographer) {
        if (isPhotographer) {
            return reservationFormRepository.findByPhotographerIdAndId(id, formId)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        }
        return reservationFormRepository.findByCustomerIdAndId(id, formId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    public List<StatusHistory> getStatusHistories(ReservationForm reservationForm) {
        return reservationHistoryRepository.findAllByReservationForm(reservationForm)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND))
            .stream()
            .map(this::toStatusHistory)
            .collect(Collectors.toList());
    }

    public CustomerCancelInfo getCustomerCancelledInfo(Long id, Long formId, String cancellationReason) {
        ReservationForm reservationForm = findReservationForm(id, formId, false);
        String photographerPhoneNumber = reservationForm.getPhotographer().getPhoneNumber();
        String productTitle = reservationForm.getProductTitle();
        String customerName = reservationForm.getCustomer().getName();

        return CustomerCancelInfo.builder()
            .photographerPhoneNumber(photographerPhoneNumber)
            .productTitle(productTitle)
            .customerName(customerName)
            .cancellationReason(cancellationReason)
            .reservationId(formId.toString())
            .build();
    }

    public CustomerCancelledInfo getPhotographerCancelledInfo(Long id, Long formId, String cancellationReason) {
        ReservationForm reservationForm = findReservationForm(id, formId, true);
        String customerPhoneNumber = reservationForm.getCustomer().getPhoneNumber();
        String productTitle = reservationForm.getProductTitle();

        return CustomerCancelledInfo.builder()
            .customerPhoneNumber(customerPhoneNumber)
            .productTitle(productTitle)
            .cancellationReason(cancellationReason)
            .reservationId(formId.toString())
            .build();
    }

    private StatusHistory toStatusHistory(ReservationHistory reservationHistory) {
        return StatusHistory.builder()
            .reservationStatus(reservationHistory.getReservationStatus())
            .statusUpdateDate(reservationHistory.getStatusUpdateDate().toLocalDate())
            .build();
    }

    private ReservationHistory updateReservationHistory(ReservationStatusUpdateRequest request,
        ReservationForm reservationForm) {

        if (request.getCancellationReason() != null) {
            return ReservationHistory.createCancelledReservationHistory(reservationForm, request.getUpdateStatus(),
                request.getCancellationReason());
        } else {
            return ReservationHistory.createReservationHistory(reservationForm, request.getUpdateStatus());
        }
    }
}
