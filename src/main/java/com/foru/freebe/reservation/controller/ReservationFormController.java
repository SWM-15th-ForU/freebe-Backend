package com.foru.freebe.reservation.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.reservation.dto.ReservationFormRequest;
import com.foru.freebe.reservation.service.ReservationFormService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
public class ReservationFormController {
    private final ReservationFormService reservationFormService;

    @PostMapping("/")
    public ApiResponse<Void> registerReservationForm(@RequestBody ReservationFormRequest reservationFormRequest) {
        return reservationFormService.registerReservationForm(reservationFormRequest);
    }
}
