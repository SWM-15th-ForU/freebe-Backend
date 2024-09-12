package com.foru.freebe.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
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
    public ApiResponse<List<FormListViewResponse>> getReservationList(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member member = memberAdapter.getMember();
        return photographerReservationService.getReservationList(member.getId());
    }

    @GetMapping("/reservation/details/{formId}")
    public ApiResponse<FormDetailsViewResponse> getReservationFormDetails(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @PathVariable("formId") Long formId) {

        Member member = memberAdapter.getMember();
        return photographerReservationDetails.getReservationFormDetails(member.getId(), formId);
    }

    @PutMapping("/reservation/details/{formId}")
    public ResponseEntity<Void> updateReservationFormDetails(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @PathVariable("formId") Long formId,
        @Valid @RequestBody ReservationStatusUpdateRequest request) {

        Member member = memberAdapter.getMember();
        return reservationService.updateReservationStatus(member.getId(), formId, request, true);
    }
}
