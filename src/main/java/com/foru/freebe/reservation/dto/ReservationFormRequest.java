package com.foru.freebe.reservation.dto;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationFormRequest {
    private Long photographerId;
    private Long customerId; // TODO 추후 토큰 로직으로 대체
    @NotNull
    private String instagramId;
    @NotNull
    private String productTitle;
    private Map<String, String> photoInfo;
    private Map<String, String> photoSchedule;
    private String requestMemo;
    @NotNull
    private Long totalPrice;
    @NotNull
    private Boolean serviceTermAgreement;
    @NotNull
    private Boolean photographerTermAgreement;
}
