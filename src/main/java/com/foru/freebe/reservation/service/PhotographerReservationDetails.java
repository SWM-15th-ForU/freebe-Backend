package com.foru.freebe.reservation.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.reservation.dto.CustomerDetails;
import com.foru.freebe.reservation.dto.FormDetailsViewResponse;
import com.foru.freebe.reservation.dto.PreferredDate;
import com.foru.freebe.reservation.dto.ReferenceImageUrls;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.dto.StatusHistory;
import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReferenceImageRepository;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerReservationDetails {
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReferenceImageRepository referenceImageRepository;

    public ApiResponse<FormDetailsViewResponse> getReservationFormDetails(Long photographerId, Long formId) {
        ReservationForm reservationForm = findReservationForm(photographerId, formId);
        List<StatusHistory> statusHistories = getStatusHistories(reservationForm);

        CustomerDetails customerDetails = buildCustomerDetails(reservationForm);
        Map<String, String> shootDetails = reservationForm.getPhotoInfo();
        Map<Integer, PreferredDate> preferredDates = reservationForm.getPreferredDate();
        ReferenceImageUrls preferredImages = getPreferredImages(reservationForm);

        FormDetailsViewResponse formDetailsViewResponse = FormDetailsViewResponse.builder(reservationForm.getId(),
                reservationForm.getReservationStatus(), statusHistories, reservationForm.getProductTitle(), customerDetails,
                shootDetails, preferredDates)
            .photoOptions(reservationForm.getPhotoOption())
            .originalImage(preferredImages.getOriginalImage())
            .thumbnailImage(preferredImages.getThumbnailImage())
            .requestMemo(reservationForm.getCustomerMemo())
            .photographerMemo(reservationForm.getPhotographerMemo())
            .build();

        return ApiResponse.<FormDetailsViewResponse>builder()
            .message("Successfully get reservation list")
            .status(200)
            .data(formDetailsViewResponse)
            .build();
    }

    @Transactional
    public ApiResponse<Void> updateReservationStatus(Long photographerId, Long formId,
        ReservationStatusUpdateRequest request) {

        ReservationForm reservationForm = findReservationForm(photographerId, formId);
        validateStatusChange(reservationForm.getReservationStatus(), request.getUpdateStatus());

        reservationForm.updateReservationStatus(request.getUpdateStatus());
        ReservationHistory history = getReservationHistory(request, reservationForm);

        reservationFormRepository.save(reservationForm);
        reservationHistoryRepository.save(history);

        return ApiResponse.<Void>builder()
            .message("Successfully update reservation status")
            .status(200)
            .build();
    }

    private ReservationHistory getReservationHistory(ReservationStatusUpdateRequest request,
        ReservationForm reservationForm) {
        if (request.getCancellationReason() != null) {
            return ReservationHistory.createCancelledReservationHistory(reservationForm, request.getUpdateStatus(),
                request.getCancellationReason());
        } else {
            return ReservationHistory.createReservationHistory(reservationForm, request.getUpdateStatus());
        }
    }

    private void validateStatusChange(ReservationStatus currentStatus, ReservationStatus updateStatus) {
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

    private ReservationForm findReservationForm(Long photographerId, Long formId) {
        return reservationFormRepository.findByPhotographerIdAndId(photographerId, formId)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private List<StatusHistory> getStatusHistories(ReservationForm reservationForm) {
        return reservationHistoryRepository.findAllByReservationForm(reservationForm)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND))
            .stream()
            .map(this::toStatusHistory)
            .collect(Collectors.toList());
    }

    private StatusHistory toStatusHistory(ReservationHistory reservationHistory) {
        return StatusHistory.builder()
            .reservationStatus(reservationHistory.getReservationStatus())
            .statusUpdateDate(reservationHistory.getStatusUpdateDate().toLocalDate())
            .build();
    }

    private CustomerDetails buildCustomerDetails(ReservationForm reservationForm) {
        return CustomerDetails.builder()
            .customerName(reservationForm.getCustomer().getName())
            .customerPhoneNumber(reservationForm.getCustomer().getPhoneNumber())
            .customerInstagramId(reservationForm.getInstagramId())
            .build();
    }

    private ReferenceImageUrls getPreferredImages(ReservationForm reservationForm) {
        List<ReferenceImage> referenceImages = referenceImageRepository.findAllByReservationForm(reservationForm);

        List<String> originalImageUrls = referenceImages.stream()
            .map(ReferenceImage::getOriginUrl)
            .collect(Collectors.toList());

        List<String> thumbnailImageUrls = referenceImages.stream()
            .map(ReferenceImage::getThumbnailUrl)
            .collect(Collectors.toList());

        return ReferenceImageUrls.builder()
            .originalImage(originalImageUrls)
            .thumbnailImage(thumbnailImageUrls)
            .build();
    }
}
