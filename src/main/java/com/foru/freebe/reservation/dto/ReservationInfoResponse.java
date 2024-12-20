package com.foru.freebe.reservation.dto;

import java.util.Map;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationInfoResponse {
    @NotNull
    private ReservationStatus reservationStatus;

    @NotNull
    private Long productId;

    @NotBlank
    private String productTitle;

    @NotNull
    private Long basicPrice;

    @NotBlank
    private String basicPlace;

    @NotNull
    private Map<String, String> photoInfo;

    @NotNull
    private Map<String, PhotoNotice> photoNotice;

    @NotNull
    private Map<Integer, TimeSlot> preferredDate;

    private String preferredPlace;

    private TimeSlot shootingDate;

    private String shootingPlace;

    private Map<Integer, PhotoOption> photoOptions;

    private String customerMemo;

    @Builder
    public ReservationInfoResponse(ReservationStatus reservationStatus, Long productId, String productTitle,
        Long basicPrice, String basicPlace, Map<String, String> photoInfo, Map<String, PhotoNotice> photoNotice,
        Map<Integer, TimeSlot> preferredDate, String preferredPlace, TimeSlot shootingDate, String shootingPlace,
        Map<Integer, PhotoOption> photoOptions, String customerMemo) {
        this.reservationStatus = reservationStatus;
        this.productId = productId;
        this.productTitle = productTitle;
        this.basicPrice = basicPrice;
        this.basicPlace = basicPlace;
        this.photoInfo = photoInfo;
        this.photoNotice = photoNotice;
        this.preferredDate = preferredDate;
        this.preferredPlace = preferredPlace;
        this.shootingDate = shootingDate;
        this.shootingPlace = shootingPlace;
        this.photoOptions = photoOptions;
        this.customerMemo = customerMemo;
    }
}
