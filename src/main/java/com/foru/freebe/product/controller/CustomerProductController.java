package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.dto.customer.ProductBasicInfoResponse;
import com.foru.freebe.product.dto.customer.ProductResponse;
import com.foru.freebe.product.service.CustomerProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/product")
public class CustomerProductController {
    private final CustomerProductService customerProductService;

    @GetMapping("/basic-info")
    public ApiResponse<List<ProductBasicInfoResponse>> getBasicInfoOfAllProducts(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member customer = memberAdapter.getMember();
        return customerProductService.getBasicInfoOfAllProducts(customer.getId());
    }

    @GetMapping("/detail-info")
    public ApiResponse<ProductResponse> getDetailedInfoOfProduct(@AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member customer = memberAdapter.getMember();
        return customerProductService.getDetailedInfoOfProduct(customer.getId());
    }

    @GetMapping("/images/{id}")
    public ApiResponse<List<String>> getReferenceImages(@PathVariable("id") Long productId) {
        return customerProductService.getReferenceImages(productId);
    }
}