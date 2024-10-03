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

    @NotBlank
    private String productTitle;

    @NotNull
    private Long basicPrice;

    @NotNull
    private Map<String, String> photoInfo;

    @NotNull
    private Map<Integer, PreferredDate> preferredDate;

    private Map<Integer, PhotoOption> photoOptions;

    private String customerMemo;

    @Builder
    public ReservationInfoResponse(ReservationStatus reservationStatus, String productTitle, Long basicPrice,
        Map<String, String> photoInfo, Map<Integer, PreferredDate> preferredDate,
        Map<Integer, PhotoOption> photoOptions, String customerMemo) {
        this.reservationStatus = reservationStatus;
        this.productTitle = productTitle;
        this.basicPrice = basicPrice;
        this.photoInfo = photoInfo;
        this.preferredDate = preferredDate;
        this.photoOptions = photoOptions;
        this.customerMemo = customerMemo;
    }
}
