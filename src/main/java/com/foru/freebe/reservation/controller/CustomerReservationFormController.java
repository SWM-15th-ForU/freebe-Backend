package com.foru.freebe.reservation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.reservation.dto.BasicReservationInfoResponse;
import com.foru.freebe.reservation.dto.ReservationFormRequest;
import com.foru.freebe.reservation.service.CustomerReservationFormService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/reservation")
public class CustomerReservationFormController {
    private final CustomerReservationFormService reservationFormService;

    @PostMapping("/")
    public ApiResponse<Void> registerReservationForm(@RequestBody ReservationFormRequest reservationFormRequest) {
        return reservationFormService.registerReservationForm(reservationFormRequest);
    }

    @GetMapping("/basic-info/{id}/{productId}")
    public ApiResponse<BasicReservationInfoResponse> getBasicReservationInfo(@PathVariable("id") Long customerId,
        @PathVariable("productId") Long productId) {
        return reservationFormService.getBasicReservationInfo(customerId, productId);
    }
}
