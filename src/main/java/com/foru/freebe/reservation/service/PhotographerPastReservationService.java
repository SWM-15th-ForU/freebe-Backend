package com.foru.freebe.reservation.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.respository.ProductImageRepository;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.PastReservationFormComponent;
import com.foru.freebe.reservation.dto.PastReservationResponse;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerPastReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private static final String CANCELLED = "cancelled";
    private static final String COMPLETED = "completed";

    public PastReservationResponse getPastReservationList(Long photographerId, LocalDate from, LocalDate to,
        String status, String keyword, Pageable defaultPageable) {

        Pageable pageable = setCustomPageable(defaultPageable);
        Page<ReservationForm> reservationFormPage = null;
        List<ReservationForm> reservationFormList = null;
        int totalPages = 1;
        List<PastReservationFormComponent> component = null;

        if (status != null) {
            if (status.equals(CANCELLED)) {
                if (from != null && to != null) {
                    reservationFormPage = reservationFormRepository.findByPhotographerIdAndReservationStatusInAndShootingDate_DateBetween(
                        photographerId, Arrays.asList(ReservationStatus.CANCELLED_BY_CUSTOMER,
                            ReservationStatus.CANCELLED_BY_PHOTOGRAPHER), from, to, pageable);
                } else {
                    reservationFormPage = reservationFormRepository.findByPhotographerIdAndReservationStatusIn(
                        photographerId, Arrays.asList(ReservationStatus.CANCELLED_BY_CUSTOMER,
                            ReservationStatus.CANCELLED_BY_PHOTOGRAPHER), pageable);
                }
            } else if (status.equals(COMPLETED)) {
                if (from != null && to != null) {
                    reservationFormPage = reservationFormRepository.findByPhotographerIdAndReservationStatusInAndShootingDate_DateBetween(
                        photographerId, Collections.singletonList(ReservationStatus.PHOTO_COMPLETED), from, to,
                        pageable);
                } else {
                    reservationFormPage = reservationFormRepository.findByPhotographerIdAndReservationStatusIn(
                        photographerId, Collections.singletonList(ReservationStatus.PHOTO_COMPLETED), pageable);
                }
            }
        } else {
            if (from != null && to != null) {
                reservationFormPage = reservationFormRepository.findByPhotographerIdAndReservationStatusInAndShootingDate_DateBetween(
                    photographerId, Arrays.asList(ReservationStatus.CANCELLED_BY_PHOTOGRAPHER,
                        ReservationStatus.CANCELLED_BY_PHOTOGRAPHER, ReservationStatus.PHOTO_COMPLETED), from, to,
                    pageable);
            } else {
                reservationFormPage = reservationFormRepository.findByPhotographerIdAndReservationStatusIn(
                    photographerId,
                    Arrays.asList(ReservationStatus.CANCELLED_BY_PHOTOGRAPHER, ReservationStatus.CANCELLED_BY_CUSTOMER,
                        ReservationStatus.PHOTO_COMPLETED), pageable);
            }
        }

        if (reservationFormPage != null) {
            reservationFormList = reservationFormPage.getContent();

            if (keyword != null && !keyword.isEmpty()) {
                reservationFormList = keywordFiltering(keyword, reservationFormPage);
            }
        }

        totalPages = reservationFormPage != null ? reservationFormPage.getTotalPages() : 1;
        component = reservationFormList != null ? reservationFormList.stream()
            .map(this::convertToPastReservationComponent)
            .collect(Collectors.toList()) : null;

        return new PastReservationResponse(totalPages, component);
    }

    private Pageable setCustomPageable(Pageable pageable) {
        int page = pageable.getPageNumber() > 0 ? pageable.getPageNumber() - 1 : 0;
        return PageRequest.of(page, pageable.getPageSize(), pageable.getSort());
    }

    private List<ReservationForm> keywordFiltering(String keyword, Page<ReservationForm> reservationFormPage) {
        return reservationFormPage.stream()
            .filter(reservationForm -> keywordMatches(reservationForm, keyword))
            .toList();
    }

    private boolean keywordMatches(ReservationForm form, String keyword) {
        return form.getCustomer().getName().contains(keyword) || form.getInstagramId().contains(keyword)
            || form.getProductTitle().contains(keyword);
    }

    private PastReservationFormComponent convertToPastReservationComponent(ReservationForm form) {
        Product product = productRepository.findByTitleAndMember(form.getProductTitle(), form.getPhotographer())
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        List<ProductImage> productImage = productImageRepository.findByProduct(product);
        String imageUrl = null;
        if (productImage != null) {
            imageUrl = productImage.get(0).getThumbnailUrl();
        }

        return PastReservationFormComponent.builder()
            .reservationStatus(form.getReservationStatus())
            .reservationId(form.getId())
            .reservationSubmissionDate(LocalDate.from(form.getCreatedAt()))
            .customerName(form.getCustomer().getName())
            .productTitle(form.getProductTitle())
            .shootingDate(form.getShootingDate())
            .price(form.getTotalPrice())
            .image(imageUrl)
            .build();
    }
}
