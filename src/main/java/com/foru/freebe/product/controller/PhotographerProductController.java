package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequest;
import com.foru.freebe.product.dto.photographer.RegisteredProductResponse;
import com.foru.freebe.product.dto.photographer.UpdateProductRequest;
import com.foru.freebe.product.service.PhotographerProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer/product")
public class PhotographerProductController {
    private final PhotographerProductService productService;

    @PostMapping("/")
    public ApiResponse<Void> registerProduct(@RequestBody ProductRegisterRequest productRegisterRequestDto) {
        return productService.registerProduct(productRegisterRequestDto);
    }

    @GetMapping("/registered-product/{id}")
    public ApiResponse<List<RegisteredProductResponse>> getRegisteredProductList(
        @PathVariable("id") Long memberId) {
        return productService.getRegisteredProductList(memberId);
    }

    @PutMapping("/update-status")
    public ApiResponse<Void> updateProductActiveStatus(@RequestBody UpdateProductRequest requestDto) {
        return productService.updateProductActiveStatus(requestDto);
    }

}
