package com.foru.freebe.reservation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.BasicReservationInfoResponse;
import com.foru.freebe.reservation.dto.ReservationFormRequest;
import com.foru.freebe.reservation.service.CustomerReservationFormService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/reservation")
public class CustomerReservationFormController {
    private final CustomerReservationFormService reservationFormService;

    @PostMapping("/")
    public ApiResponse<Void> registerReservationForm(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestBody ReservationFormRequest reservationFormRequest) {
        Member customer = memberAdapter.getMember();
        return reservationFormService.registerReservationForm(reservationFormRequest, customer.getId());
    }

    @GetMapping("/basic-info/{productId}")
    public ApiResponse<BasicReservationInfoResponse> getBasicReservationInfo(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @PathVariable("productId") Long productId) {
        Member customer = memberAdapter.getMember();
        return reservationFormService.getBasicReservationInfo(customer.getId(), productId);
    }
}
