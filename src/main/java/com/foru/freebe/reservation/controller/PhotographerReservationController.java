package com.foru.freebe.reservation.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.FormDetailsViewResponse;
import com.foru.freebe.reservation.dto.FormListViewResponse;
import com.foru.freebe.reservation.service.PhotographerReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerReservationController {
    private final PhotographerReservationService photographerReservationService;

    @GetMapping("/reservation")
    public ApiResponse<List<FormListViewResponse>> getReservationList(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member member = memberAdapter.getMember();
        return photographerReservationService.getReservationList(member.getId());
    }

    @GetMapping("/reservation/details/{formId}")
    public ApiResponse<FormDetailsViewResponse> getReservationFormDetails(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @PathVariable("formId") Long formId) {
        Member member = memberAdapter.getMember();
        return photographerReservationService.getReservationFormDetails(member.getId(), formId);
    }
}
