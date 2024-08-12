package com.foru.freebe.reservation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.reservation.dto.FormComponent;
import com.foru.freebe.reservation.dto.FormListViewResponse;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerReservationService {
    private final ReservationFormRepository reservationFormRepository;

    public ApiResponse<List<FormListViewResponse>> getReservationList(Long id) {
        List<FormListViewResponse> data = getReservationListAsStatus(id);

        return ApiResponse.<List<FormListViewResponse>>builder()
            .message("Successfully get reservation list")
            .status(200)
            .data(data)
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
            reservationForm.getPhotoSchedule().values().stream().findFirst().orElse(null)
        );
    }

}
