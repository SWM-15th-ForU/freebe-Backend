package com.foru.freebe.reservation.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.reservation.dto.CustomerDetails;
import com.foru.freebe.reservation.dto.FormDetailsViewResponse;
import com.foru.freebe.reservation.dto.PreferredDate;
import com.foru.freebe.reservation.dto.ReferenceImageUrls;
import com.foru.freebe.reservation.dto.StatusHistory;
import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.repository.ReferenceImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerReservationDetails {
    private final ReservationService reservationService;
    private final ReferenceImageRepository referenceImageRepository;

    public ApiResponse<FormDetailsViewResponse> getReservationFormDetails(Long photographerId, Long formId) {
        ReservationForm reservationForm = reservationService.findReservationForm(photographerId, formId, true);
        List<StatusHistory> statusHistories = reservationService.getStatusHistories(reservationForm);

        CustomerDetails customerDetails = buildCustomerDetails(reservationForm);
        Map<String, String> shootDetails = reservationForm.getPhotoInfo();
        Map<Integer, PreferredDate> preferredDates = reservationForm.getPreferredDate();
        ReferenceImageUrls preferredImages = getPreferredImages(reservationForm);

        FormDetailsViewResponse formDetailsViewResponse = FormDetailsViewResponse.builder(reservationForm.getId(),
                reservationForm.getReservationStatus(), statusHistories, reservationForm.getProductTitle(), customerDetails,
                shootDetails, preferredDates)
            .photoOptions(reservationForm.getPhotoOption())
            .originalImage(preferredImages.getOriginalImage())
            .thumbnailImage(preferredImages.getThumbnailImage())
            .requestMemo(reservationForm.getCustomerMemo())
            .photographerMemo(reservationForm.getPhotographerMemo())
            .build();

        return ApiResponse.<FormDetailsViewResponse>builder()
            .message("Successfully get reservation list")
            .status(200)
            .data(formDetailsViewResponse)
            .build();
    }

    private CustomerDetails buildCustomerDetails(ReservationForm reservationForm) {
        return CustomerDetails.builder()
            .name(reservationForm.getCustomer().getName())
            .phoneNumber(reservationForm.getCustomer().getPhoneNumber())
            .instagramId(reservationForm.getInstagramId())
            .build();
    }

    private ReferenceImageUrls getPreferredImages(ReservationForm reservationForm) {
        List<ReferenceImage> referenceImages = referenceImageRepository.findAllByReservationForm(reservationForm);

        List<String> originalImageUrls = referenceImages.stream()
            .map(ReferenceImage::getOriginUrl)
            .collect(Collectors.toList());

        List<String> thumbnailImageUrls = referenceImages.stream()
            .map(ReferenceImage::getThumbnailUrl)
            .collect(Collectors.toList());

        return ReferenceImageUrls.builder()
            .originalImage(originalImageUrls)
            .thumbnailImage(thumbnailImageUrls)
            .build();
    }
}
