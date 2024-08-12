package com.foru.freebe.reservation.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormDetailsViewResponse {
    private ProgressTracker progressTracker;
    private Long reservationNumber;
    private String productTitle;
    private CustomerDetails customerDetails;
    private Map<String, String> photoInfo;
    private Map<Integer, PreferredDate> preferredDate;
    private List<String> preferredPhoto;
    private String requestMemo;

    @Builder
    public FormDetailsViewResponse(ProgressTracker progressTracker, Long reservationNumber, String productTitle,
        CustomerDetails customerDetails, Map<String, String> photoInfo, Map<Integer, PreferredDate> preferredDate,
        List<String> preferredPhoto, String requestMemo) {
        this.progressTracker = progressTracker;
        this.reservationNumber = reservationNumber;
        this.productTitle = productTitle;
        this.customerDetails = customerDetails;
        this.photoInfo = photoInfo;
        this.preferredDate = preferredDate;
        this.preferredPhoto = preferredPhoto;
        this.requestMemo = requestMemo;
    }

    public static FormDetailsViewResponseBuilder builder(ProgressTracker progressTracker, Long reservationNumber,
        String productTitle, CustomerDetails customerDetails, Map<String, String> photoInfo,
        Map<Integer, PreferredDate> preferredDate) {
        return new FormDetailsViewResponseBuilder()
            .progressTracker(progressTracker)
            .reservationNumber(reservationNumber)
            .productTitle(productTitle)
            .customerDetails(customerDetails)
            .photoInfo(photoInfo)
            .preferredDate(preferredDate);
    }
}
