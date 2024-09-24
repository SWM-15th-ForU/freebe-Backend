package com.foru.freebe.reservation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.FormDetailsViewResponse;
import com.foru.freebe.reservation.dto.FormListViewResponse;
import com.foru.freebe.reservation.dto.ReservationStatusUpdateRequest;
import com.foru.freebe.reservation.service.PhotographerReservationDetails;
import com.foru.freebe.reservation.service.PhotographerReservationService;
import com.foru.freebe.reservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerReservationController {
    private final PhotographerReservationService photographerReservationService;
    private final PhotographerReservationDetails photographerReservationDetails;
    private final ReservationService reservationService;

    @GetMapping("/reservation/list")
    public ResponseEntity<ResponseBody<List<FormListViewResponse>>> getReservationList(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member member = memberAdapter.getMember();
        List<FormListViewResponse> responseData = photographerReservationService.getReservationList(member.getId());

        ResponseBody<List<FormListViewResponse>> responseBody = ResponseBody.<List<FormListViewResponse>>builder()
            .message("Successfully get reservation list")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/reservation/details/{formId}")
    public ResponseEntity<ResponseBody<FormDetailsViewResponse>> getReservationFormDetails(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @PathVariable("formId") Long formId) {

        Member member = memberAdapter.getMember();
        FormDetailsViewResponse responseData = photographerReservationDetails.getReservationFormDetails(
            member.getId(), formId);

        ResponseBody<FormDetailsViewResponse> responseBody = ResponseBody.<FormDetailsViewResponse>builder()
            .message("Successfully get reservation list")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/reservation/details/{formId}")
    public ResponseEntity<ResponseBody<Void>> updateReservationFormDetails(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @PathVariable("formId") Long formId,
        @Valid @RequestBody ReservationStatusUpdateRequest request) {

        Member member = memberAdapter.getMember();
        reservationService.updateReservationStatus(member.getId(), formId, request, true);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully update reservation status")
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}
