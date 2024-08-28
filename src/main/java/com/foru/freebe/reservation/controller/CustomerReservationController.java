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
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.service.CustomerReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerReservationController {
    private final CustomerReservationService customerReservationService;

    @PostMapping("/reservation")
    public ApiResponse<Long> registerReservationForm(@Valid @RequestBody FormRegisterRequest request,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member customer = memberAdapter.getMember();
        return customerReservationService.registerReservationForm(customer.getId(), request);
    }

    @GetMapping("/reservation/form/{productId}")
    public ApiResponse<BasicReservationInfoResponse> getBasicReservationForm(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @Valid @PathVariable("productId") Long productId) {
        Member customer = memberAdapter.getMember();
        return customerReservationService.getBasicReservationForm(customer.getId(), productId);
    }
}
