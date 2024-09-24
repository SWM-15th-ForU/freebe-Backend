package com.foru.freebe.reservation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.BasicReservationInfoResponse;
import com.foru.freebe.reservation.dto.FormRegisterRequest;
import com.foru.freebe.reservation.dto.ReservationInfoResponse;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.service.CustomerReservationService;
import com.foru.freebe.reservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerReservationController {
    private final CustomerReservationService customerReservationService;
    private final ReservationService reservationService;

    @PostMapping(value = "/reservation", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
        MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseBody<Long>> registerReservationForm(
        @RequestPart("request") FormRegisterRequest request,
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        Member customer = memberAdapter.getMember();
        Long responseData = customerReservationService.registerReservationForm(customer.getId(), request, images);

        ResponseBody<Long> responseBody = ResponseBody.<Long>builder()
            .message("Good Request")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/reservation/form/{productId}")
    public ResponseEntity<ResponseBody<BasicReservationInfoResponse>> getBasicReservationForm(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @Valid @PathVariable("productId") Long productId) {

        Member customer = memberAdapter.getMember();
        BasicReservationInfoResponse responseData = customerReservationService.getBasicReservationForm(
            customer.getId(), productId);

        ResponseBody<BasicReservationInfoResponse> responseBody = ResponseBody.<BasicReservationInfoResponse>builder()
            .message("Good Response")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/reservation/{reservationFormId}")
    public ResponseEntity<ResponseBody<ReservationInfoResponse>> getReservationInfo(
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @PathVariable("reservationFormId") Long reservationFormId) {

        Member customer = memberAdapter.getMember();
        ReservationInfoResponse responseData = customerReservationService.getReservationInfo(reservationFormId,
            customer.getId());

        ResponseBody<ReservationInfoResponse> responseBody = ResponseBody.<ReservationInfoResponse>builder()
            .message("Good Response")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/reservation/{formId}")
    public ResponseEntity<Void> updateBasicReservationForm(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @PathVariable("formId") Long formId, @Valid @RequestBody ReservationStatusUpdateRequest request) {

        Member customer = memberAdapter.getMember();
        reservationService.updateReservationStatus(customer.getId(), formId, request, false);

        return ResponseEntity.status(HttpStatus.OK.value()).build();
    }
}