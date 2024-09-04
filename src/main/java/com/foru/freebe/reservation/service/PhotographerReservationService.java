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
public class PhotographerReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private final ReferenceImageRepository referenceImageRepository;

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
        List<String> preferredImages = getPreferredImages(reservationForm);

        FormDetailsViewResponse formDetailsViewResponse = FormDetailsViewResponse.builder(reservationForm.getId(),
                reservationForm.getReservationStatus(), statusHistories, reservationForm.getProductTitle(), customerDetails,
                shootDetails, preferredDates)
            .preferredImages(preferredImages)
            .requestMemo(reservationForm.getCustomerMemo())
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
            reservationForm.getId(),
            reservationForm.getReservationStatus(),
            reservationForm.getCustomer().getName(),
            reservationForm.getProductTitle(),
            reservationForm.getReservationStatus() == ReservationStatus.NEW ? null :
                reservationForm.getPreferredDate().values().stream().findFirst().orElse(null)
            //ToDo: 임의로 희망 촬영일정의 첫번째 값을 반환하였음. 추후 신청서 상태 변경 로직이 추가되면 확정된 촬영일자로 변경해주어야 함
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
            .reservationStatus(reservationHistory.getReservationStatus())
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

    private List<String> getPreferredImages(ReservationForm reservationForm) {
        return referenceImageRepository.findAllByReservationForm(reservationForm)
            .orElse(List.of())
            .stream()
            .map(ReferenceImage::getReferencingImage)
            .collect(Collectors.toList());
    }
}
