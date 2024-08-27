package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.customer.ProductListResponse;
import com.foru.freebe.product.service.CustomerProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerProductController {
    private final CustomerProductService customerProductService;

    @GetMapping("/product/list/{photographerId}")
    public ApiResponse<List<ProductListResponse>> getProductList(
        @PathVariable("photographerId") Long photographerId) {
        return customerProductService.getProductList(photographerId);
    }

    @GetMapping("/product/details/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetails(@PathVariable("productId") Long productId) {
        return customerProductService.getDetailedInfoOfProduct(productId);
    }

    @GetMapping("/product/images/{photographerId}")
    public ApiResponse<List<String>> getReferenceImages(@PathVariable("photographerId") Long photographerId) {
        return customerProductService.getReferenceImages(photographerId);
    }
}