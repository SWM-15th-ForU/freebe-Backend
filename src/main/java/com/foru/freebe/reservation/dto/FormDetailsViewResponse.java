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
    private ReservationStatus status;
    private List<StatusHistory> statusHistory;
    private String productTitle;
    private CustomerDetails customerDetails;
    private Map<String, String> photoInfo;
    private Map<Integer, PreferredDate> preferredDate;
    private List<String> preferredPhoto;
    private String requestMemo;

    @Builder
    public FormDetailsViewResponse(Long reservationNumber, ReservationStatus status,
        List<StatusHistory> statusHistory, String productTitle, CustomerDetails customerDetails,
        Map<String, String> photoInfo, Map<Integer, PreferredDate> preferredDate, List<String> preferredPhoto,
        String requestMemo) {
        this.reservationNumber = reservationNumber;
        this.status = status;
        this.statusHistory = statusHistory;
        this.productTitle = productTitle;
        this.customerDetails = customerDetails;
        this.photoInfo = photoInfo;
        this.preferredDate = preferredDate;
        this.preferredPhoto = preferredPhoto;
        this.requestMemo = requestMemo;
    }

    public static FormDetailsViewResponseBuilder builder(Long reservationNumber, ReservationStatus status,
        List<StatusHistory> statusHistory, String productTitle, CustomerDetails customerDetails,
        Map<String, String> photoInfo, Map<Integer, PreferredDate> preferredDate) {
        return new FormDetailsViewResponseBuilder()
            .reservationNumber(reservationNumber)
            .status(status)
            .statusHistory(statusHistory)
            .productTitle(productTitle)
            .customerDetails(customerDetails)
            .photoInfo(photoInfo)
            .preferredDate(preferredDate);
    }
}
