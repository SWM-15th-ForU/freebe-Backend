package com.foru.freebe.reservation.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.reservation.dto.CustomerDetails;
import com.foru.freebe.reservation.dto.FormDetailsViewResponse;
import com.foru.freebe.reservation.dto.ReferenceImageUrls;
import com.foru.freebe.reservation.dto.StatusHistory;
import com.foru.freebe.reservation.dto.TimeSlot;
import com.foru.freebe.reservation.entity.ReferenceImage;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.repository.ReferenceImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerReservationDetails {
    private final ReservationService reservationService;
    private final ReferenceImageRepository referenceImageRepository;

    public FormDetailsViewResponse getReservationFormDetails(Long photographerId, Long formId) {
        ReservationForm reservationForm = reservationService.findReservationForm(photographerId, formId, true);
        List<StatusHistory> statusHistories = reservationService.getStatusHistories(reservationForm);

        CustomerDetails customerDetails = buildCustomerDetails(reservationForm);
        Map<Integer, TimeSlot> preferredDates = reservationForm.getPreferredDate();
        Map<String, String> photoInfo = reservationForm.getPhotoInfo();
        ReferenceImageUrls preferredImages = getPreferredImages(reservationForm);

        return FormDetailsViewResponse.builder(reservationForm.getId(), reservationForm.getReservationStatus(),
                statusHistories, reservationForm.getProductTitle(), customerDetails, reservationForm.getBasicPrice(),
                reservationForm.getBasicPlace(), photoInfo, preferredDates)
            .photoOptions(reservationForm.getPhotoOption())
            .preferredPlace(reservationForm.getPreferredPlace())
            .shootingDate(reservationForm.getShootingDate())
            .shootingPlace(reservationForm.getShootingPlace())
            .originalImage(preferredImages.getOriginalImage())
            .thumbnailImage(preferredImages.getThumbnailImage())
            .requestMemo(reservationForm.getCustomerMemo())
            .photoNotice(reservationForm.getPhotoNotice())
            .photographerMemo(reservationForm.getPhotographerMemo())
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
