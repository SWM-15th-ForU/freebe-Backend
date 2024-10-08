package com.foru.freebe.reservation.dto;

import java.util.List;
import java.util.Map;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormDetailsViewResponse {
    @NotNull
    private Long reservationNumber;

    @NotNull
    private ReservationStatus currentReservationStatus;

    @NotNull
    private List<StatusHistory> statusHistory;

    @NotBlank
    private String productTitle;

    @NotNull
    private CustomerDetails customerDetails;

    @NotNull
    private Long basicPrice;

    @NotNull
    private Map<String, String> photoInfo;

    private Map<Integer, PhotoOption> photoOptions;

    @NotNull
    private Map<Integer, PreferredDate> preferredDates;

    private List<String> originalImage;

    private List<String> thumbnailImage;

    private String requestMemo;

    private String photographerMemo;

    @Builder
    public FormDetailsViewResponse(Long reservationNumber, ReservationStatus currentReservationStatus,
        List<StatusHistory> statusHistory, String productTitle, CustomerDetails customerDetails, Long basicPrice,
        Map<String, String> photoInfo, Map<Integer, PhotoOption> photoOptions,
        Map<Integer, PreferredDate> preferredDates, List<String> originalImage,
        List<String> thumbnailImage, String requestMemo, String photographerMemo) {
        this.reservationNumber = reservationNumber;
        this.currentReservationStatus = currentReservationStatus;
        this.statusHistory = statusHistory;
        this.productTitle = productTitle;
        this.customerDetails = customerDetails;
        this.basicPrice = basicPrice;
        this.photoInfo = photoInfo;
        this.photoOptions = photoOptions;
        this.preferredDates = preferredDates;
        this.originalImage = originalImage;
        this.thumbnailImage = thumbnailImage;
        this.requestMemo = requestMemo;
        this.photographerMemo = photographerMemo;
    }

    public static FormDetailsViewResponseBuilder builder(Long reservationNumber,
        ReservationStatus currentReservationStatus, List<StatusHistory> statusHistory, String productTitle,
        CustomerDetails customerDetails, Long basicPrice, Map<String, String> photoInfo,
        Map<Integer, PreferredDate> preferredDates) {
        return new FormDetailsViewResponseBuilder()
            .reservationNumber(reservationNumber)
            .currentReservationStatus(currentReservationStatus)
            .statusHistory(statusHistory)
            .productTitle(productTitle)
            .customerDetails(customerDetails)
            .basicPrice(basicPrice)
            .photoInfo(photoInfo)
            .preferredDates(preferredDates);
    }
}
