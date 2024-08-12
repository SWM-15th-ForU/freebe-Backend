package com.foru.freebe.reservation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.ReservationFormRequest;
import com.foru.freebe.reservation.service.CustomerReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerReservationController {
    private final CustomerReservationService customerReservationService;

    @PostMapping("/reservation")
    public ApiResponse<Void> registerReservationForm(@Valid @RequestBody ReservationFormRequest request,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member customer = memberAdapter.getMember();
        return customerReservationService.registerReservationForm(customer.getId(), request);
    }
}
