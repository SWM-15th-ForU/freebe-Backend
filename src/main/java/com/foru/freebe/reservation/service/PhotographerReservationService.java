package com.foru.freebe.reservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.reservation.dto.CustomerDetails;
import com.foru.freebe.reservation.dto.FormComponent;
import com.foru.freebe.reservation.dto.FormDetailsViewResponse;
import com.foru.freebe.reservation.dto.FormListViewResponse;
import com.foru.freebe.reservation.dto.PreferredDate;
import com.foru.freebe.reservation.dto.StatusHistory;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationHistory;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;
import com.foru.freebe.reservation.repository.ReservationHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;

    public ApiResponse<List<FormListViewResponse>> getReservationList(Long photographerId) {
        List<FormListViewResponse> data = getReservationListAsStatus(photographerId);

        return ApiResponse.<List<FormListViewResponse>>builder()
            .message("Successfully get reservation list")
            .status(200)
            .data(data)
            .build();
    }

    public ApiResponse<FormDetailsViewResponse> getReservationFormDetails(Long photographerId, Long formId) {
        ReservationForm reservationForm = findReservationForm(photographerId, formId);
        List<StatusHistory> statusHistories = getStatusHistories(reservationForm);

        CustomerDetails customerDetails = buildCustomerDetails(reservationForm);
        Map<String, String> shootDetails = reservationForm.getPhotoInfo();
        Map<Integer, PreferredDate> preferredDates = reservationForm.getPreferredDate();

        FormDetailsViewResponse formDetailsViewResponse = FormDetailsViewResponse.builder(reservationForm.getId(),
                reservationForm.getReservationStatus(), statusHistories, reservationForm.getProductTitle(), customerDetails,
                shootDetails, preferredDates)
            .preferredPhoto(null)
            .requestMemo(null)
            .build();

        return ApiResponse.<FormDetailsViewResponse>builder()
            .message("Successfully get reservation list")
            .status(200)
            .data(formDetailsViewResponse)
            .build();
    }

    private List<FormListViewResponse> getReservationListAsStatus(Long id) {
        List<ReservationForm> formList = reservationFormRepository.findAllByPhotographerId(id)
            .orElseGet(ArrayList::new);

        Map<ReservationStatus, List<FormComponent>> reservationStatusMap = groupingFormAsStatus(formList);

        return reservationStatusMap.entrySet().stream()
            .map(entry -> new FormListViewResponse(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    private Map<ReservationStatus, List<FormComponent>> groupingFormAsStatus(
        List<ReservationForm> reservationFormList) {
        return reservationFormList.stream()
            .map(this::toFormComponent)
            .collect(Collectors.groupingBy(FormComponent::getReservationStatus, Collectors.toList()));
    }

    private FormComponent toFormComponent(ReservationForm reservationForm) {
        return new FormComponent(
            reservationForm.getReservationStatus(),
            reservationForm.getCustomer().getName(),
            reservationForm.getProductTitle(),
            reservationForm.getPreferredDate().values().stream().findFirst().orElse(null)
        );
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
            .status(reservationHistory.getReservationStatus())
            .statusUpdateDate(reservationHistory.getStatusUpdateDate())
            .build();
    }

    private CustomerDetails buildCustomerDetails(ReservationForm reservationForm) {
        return CustomerDetails.builder()
            .name(reservationForm.getCustomer().getName())
            .phoneNumber(reservationForm.getCustomer().getPhoneNumber())
            .instagramId(reservationForm.getInstagramId())
            .build();
    }
}
