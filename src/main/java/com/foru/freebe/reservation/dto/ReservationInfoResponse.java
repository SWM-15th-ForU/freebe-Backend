package com.foru.freebe.reservation.dto;

import java.util.Map;

import com.foru.freebe.reservation.entity.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ReservationInfoResponse {
    @NotNull
    private ReservationStatus reservationStatus;

    @NotBlank
    private String productTitle;

    @NotNull
    private Map<String, String> photoInfo;

    @NotNull
    private Map<Integer, PreferredDate> preferredDate;

    private Map<Integer, PhotoOption> photoOptions;

    private String customerMemo;
    private Boolean serviceTermAgreement;
    private Boolean photographerTermAgreement;
}
