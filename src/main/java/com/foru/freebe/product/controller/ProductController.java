package com.foru.freebe.product.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequestDto;
import com.foru.freebe.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/")
    public ApiResponse<Void> registerProduct(@RequestBody ProductRegisterRequestDto productRegisterRequestDto) {
        return productService.registerProduct(productRegisterRequestDto);
    }

    @GetMapping("/registered-product/{id}")
    public ApiResponseDto<List<RegisteredProductResponseDto>> getRegisteredProductList(
        @PathVariable("id") Long memberId) {
        return productService.getRegisteredProductList(memberId);
    }

    @PutMapping("/update-status")
    public ApiResponseDto<Void> updateProductActiveStatus(@RequestBody UpdateProductRequestDto requestDto) {
        return productService.updateProductActiveStatus(requestDto);
    }

}
