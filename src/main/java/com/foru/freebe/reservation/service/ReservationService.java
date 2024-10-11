package com.foru.freebe.reservation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
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

    public String getCancelledProductName(Long customerId, Long formId) {
        ReservationForm reservationForm = findReservationForm(customerId, formId, false);
        return reservationForm.getProductTitle();
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
