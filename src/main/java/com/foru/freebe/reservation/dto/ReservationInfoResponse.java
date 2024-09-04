package com.foru.freebe.reservation.dto;

import java.util.Map;

import com.foru.freebe.reservation.entity.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationInfoResponse {
    private ReservationStatus reservationStatus;
    private String productTitle;
    private Map<String, String> photoInfo;
    private Map<Integer, PreferredDate> preferredDate;
    private Map<Integer, PhotoOption> photoOptions;
    private String customerMemo;
    private Boolean serviceTermAgreement;
    private Boolean photographerTermAgreement;
}
