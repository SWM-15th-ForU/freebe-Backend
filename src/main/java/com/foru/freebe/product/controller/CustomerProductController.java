package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.customer.ProductListResponse;
import com.foru.freebe.product.service.CustomerProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerProductController {
    private final CustomerProductService customerProductService;

    @GetMapping("/product/list/{profileName}")
    public ResponseEntity<ResponseBody<List<ProductListResponse>>> getProductList(
        @PathVariable("profileName") String profileName) {

        List<ProductListResponse> responseData = customerProductService.getProductList(profileName);

        ResponseBody<List<ProductListResponse>> responseBody = ResponseBody.<List<ProductListResponse>>builder()
            .message("Good Request")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/product/details/{productId}")
    public ResponseEntity<ResponseBody<ProductDetailResponse>> getProductDetails(
        @PathVariable("productId") Long productId) {

        ProductDetailResponse responseData = customerProductService.getDetailedInfoOfProduct(productId);

        ResponseBody<ProductDetailResponse> responseBody = ResponseBody.<ProductDetailResponse>builder()
            .message("Good Response")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/product/images/{productId}")
    public ResponseEntity<ResponseBody<List<String>>> getReferenceImages(
        @PathVariable("productId") Long productId) {

        List<String> responseData = customerProductService.getReferenceImages(productId);

        ResponseBody<List<String>> responseBody = ResponseBody.<List<String>>builder()
            .message("Good Response")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}