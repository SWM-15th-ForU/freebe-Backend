package com.foru.freebe.reservation.service;

import org.springframework.stereotype.Service;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.errors.errorcode.CommonErrorCode;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.member.repository.MemberRepository;
import com.foru.freebe.product.entity.ActiveStatus;
import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.respository.ProductRepository;
import com.foru.freebe.reservation.dto.ReservationFormRequest;
import com.foru.freebe.reservation.entity.ReservationForm;
import com.foru.freebe.reservation.entity.ReservationStatus;
import com.foru.freebe.reservation.repository.ReservationFormRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerReservationService {
    private final ReservationFormRepository reservationFormRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public ApiResponse<Void> registerReservationForm(Long customerId, ReservationFormRequest reservationFormRequest) {
        Member customer = findMember(customerId);
        Member photographer = findMember(reservationFormRequest.getPhotographerId());

        ReservationForm reservationForm = createReservationForm(reservationFormRequest, photographer, customer);

        validateReservationForm(reservationFormRequest);
        reservationFormRepository.save(reservationForm);

        return ApiResponse.<Void>builder()
            .status(200)
            .message("Good Request")
            .data(null)
            .build();
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }

    private static ReservationForm createReservationForm(ReservationFormRequest request,
        Member photographer, Member customer) {
        ReservationForm.ReservationFormBuilder builder = ReservationForm.builder(photographer, customer,
                request.getInstagramId(), request.getProductTitle(), request.getTotalPrice(),
                request.getServiceTermAgreement(), request.getPhotographerTermAgreement(), ReservationStatus.NEW)
            .photographerMemo(request.getRequestMemo())
            .photoInfo(request.getPhotoInfo())
            .photoSchedule(request.getPhotoSchedule())
            .requestMemo(request.getRequestMemo());
        return builder.build();
    }

    private void validateReservationForm(ReservationFormRequest reservationFormRequest) {
        validateProductTitleExists(reservationFormRequest.getProductTitle());
        validateProductIsActive(reservationFormRequest.getProductTitle());
    }

    private void validateProductIsActive(String productTitle) {
        Product product = productRepository.findByTitle(productTitle);
        if (product.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new RestApiException(ProductErrorCode.PRODUCT_INACTIVE_STATUS);
        }
    }

    private void validateProductTitleExists(String productTitle) {
        if (!productRepository.existsByTitle(productTitle)) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
