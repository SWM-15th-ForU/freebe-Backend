package com.foru.freebe.reservation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.common.service.S3ImageService;
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
    private final S3ImageService s3ImageService;

    @PostMapping(value = "/reservation", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
        MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<String> registerReservationForm(@RequestPart("request") FormRegisterRequest request,
        @AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Member customer = memberAdapter.getMember();
        return customerReservationService.registerReservationForm(customer.getId(), request, images);
    }

    @GetMapping("/reservation/form/{productId}")
    public ApiResponse<BasicReservationInfoResponse> getBasicReservationForm(
        @AuthenticationPrincipal MemberAdapter memberAdapter, @Valid @PathVariable("productId") Long productId) {
        Member customer = memberAdapter.getMember();
        return customerReservationService.getBasicReservationForm(customer.getId(), productId);
    }
}
