package com.foru.freebe.reservation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.foru.freebe.constants.SortConstants;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.dto.photographer.ProductTitleDto;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.FormComponent;
import com.foru.freebe.reservation.dto.FormListViewResponse;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotographerReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public List<FormListViewResponse> getReservationList(Long photographerId) {
        return getReservationListAsStatus(photographerId);
    }

    public List<FormListViewResponse> getFilteredReservationList(Long photographerId,
        List<ProductTitleDto> productTitleList) {
        validateIsExistedMember(photographerId);

        List<String> productTitles = productTitleList.stream()
            .map(ProductTitleDto::getTitle)
            .collect(Collectors.toList());

        return getReservationListAsTitles(productTitles);
    }

    private List<FormListViewResponse> getReservationListAsTitles(List<String> titles) {
        List<FormListViewResponse> formListViewResponses = new ArrayList<>();

        for (String title : titles) {
            Boolean isExistingTitle = productRepository.existsByTitle(title);
            if (!isExistingTitle) {
                throw new RestApiException(ProductErrorCode.INVALID_PRODUCT_TITLE);
            }

            List<ReservationForm> formList = reservationFormRepository.findByProductTitle(title);

            Map<ReservationStatus, List<FormComponent>> reservationStatusMap = groupingFormAsStatus(formList);

            formListViewResponses.addAll(
                reservationStatusMap.entrySet().stream()
                    .map(entry -> new FormListViewResponse(entry.getKey(),
                        sortFormComponents(entry.getKey(), entry.getValue())))
                    .collect(Collectors.toList())
            );
        }

        return formListViewResponses;
    }

    private void validateIsExistedMember(Long photographerId) {
        if (!memberRepository.existsById(photographerId)) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private List<FormListViewResponse> getReservationListAsStatus(Long id) {
        List<ReservationForm> formList = reservationFormRepository.findAllByPhotographerId(id)
            .orElseGet(ArrayList::new);

        Map<ReservationStatus, List<FormComponent>> reservationStatusMap = groupingFormAsStatus(formList);

        return reservationStatusMap.entrySet().stream()
            .map(
                entry -> new FormListViewResponse(entry.getKey(), sortFormComponents(entry.getKey(), entry.getValue())))
            .collect(Collectors.toList());
    }

    private Map<ReservationStatus, List<FormComponent>> groupingFormAsStatus(
        List<ReservationForm> reservationFormList) {
        return reservationFormList.stream()
            .map(this::toFormComponent)
            .filter(form -> isPublicStatus(form.getReservationStatus()))
            .collect(Collectors.groupingBy(FormComponent::getReservationStatus, Collectors.toList()));
    }

    private boolean isPublicStatus(ReservationStatus status) {
        return status != ReservationStatus.PHOTO_COMPLETED && status != ReservationStatus.CANCELLED_BY_PHOTOGRAPHER
            && status != ReservationStatus.CANCELLED_BY_CUSTOMER;
    }

    private FormComponent toFormComponent(ReservationForm reservationForm) {
        return FormComponent.builder()
            .reservationId(reservationForm.getId())
            .reservationSubmissionDate(reservationForm.getCreatedAt().toLocalDate())
            .reservationStatus(reservationForm.getReservationStatus())
            .customerName(reservationForm.getCustomer().getName())
            .productTitle(reservationForm.getProductTitle())
            .shootingDate(reservationForm.getShootingDate() == null ? null : reservationForm.getShootingDate())
            .build();
    }

    private List<FormComponent> sortFormComponents(ReservationStatus status, List<FormComponent> formComponents) {
        if (status == ReservationStatus.NEW || status == ReservationStatus.IN_PROGRESS) {
            return formComponents.stream()
                .sorted(Comparator.comparingLong(FormComponent::getReservationId).reversed())
                .collect(Collectors.toList());
        } else {
            formComponents.sort((fc1, fc2) -> {
                if (fc1.getShootingDate() != null && fc2.getShootingDate() != null) {
                    int dateComparison = fc1.getShootingDate().getDate().compareTo(fc2.getShootingDate().getDate());
                    if (dateComparison == SortConstants.EQUAL) {
                        return fc1.getShootingDate().getStartTime().compareTo(fc2.getShootingDate().getStartTime());
                    }
                    return dateComparison;
                } else if (fc1.getShootingDate() != null) {
                    return SortConstants.LESS_THAN;
                } else if (fc2.getShootingDate() != null) {
                    return SortConstants.GREATER_THAN;
                }
                return SortConstants.EQUAL;
            });
            return formComponents;
        }
    }
}
