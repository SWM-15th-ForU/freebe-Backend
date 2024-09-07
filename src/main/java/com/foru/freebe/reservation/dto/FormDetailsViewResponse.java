package com.foru.freebe.reservation.dto;

import java.util.List;
import java.util.Map;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormDetailsViewResponse {
    private Long reservationNumber;
    private ReservationStatus currentReservationStatus;
    private List<StatusHistory> statusHistory;
    private String productTitle;
    private CustomerDetails customerDetails;
    private Map<String, String> photoInfo;
    private Map<Integer, PhotoOption> photoOptions;
    private Map<Integer, PreferredDate> preferredDates;
    private List<String> originalImage;
    private List<String> thumbnailImage;
    private String requestMemo;
    private String photographerMemo;

    @Builder
    public FormDetailsViewResponse(Long reservationNumber, ReservationStatus currentReservationStatus,
        List<StatusHistory> statusHistory, String productTitle, CustomerDetails customerDetails,
        Map<String, String> photoInfo, Map<Integer, PhotoOption> photoOptions,
        Map<Integer, PreferredDate> preferredDates, List<String> originalImage,
        List<String> thumbnailImage, String requestMemo, String photographerMemo) {
        this.reservationNumber = reservationNumber;
        this.currentReservationStatus = currentReservationStatus;
        this.statusHistory = statusHistory;
        this.productTitle = productTitle;
        this.customerDetails = customerDetails;
        this.photoInfo = photoInfo;
        this.photoOptions = photoOptions;
        this.preferredDates = preferredDates;
        this.originalImage = originalImage;
        this.thumbnailImage = thumbnailImage;
        this.requestMemo = requestMemo;
        this.photographerMemo = photographerMemo;
    }

    public static FormDetailsViewResponseBuilder builder(Long reservationNumber,
        ReservationStatus currentReservationStatus,
        List<StatusHistory> statusHistory, String productTitle, CustomerDetails customerDetails,
        Map<String, String> photoInfo, Map<Integer, PreferredDate> preferredDates) {
        return new FormDetailsViewResponseBuilder()
            .reservationNumber(reservationNumber)
            .currentReservationStatus(currentReservationStatus)
            .statusHistory(statusHistory)
            .productTitle(productTitle)
            .customerDetails(customerDetails)
            .photoInfo(photoInfo)
            .preferredDates(preferredDates);
    }
}
