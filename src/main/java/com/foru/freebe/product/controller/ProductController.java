package com.foru.freebe.product.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.product.dto.ProductRegisterRequestDto;
import com.foru.freebe.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/")
    public ApiResponseDto<Void> registerProduct(@RequestBody ProductRegisterRequestDto productRegisterRequestDto) {
        return productService.registerProduct(productRegisterRequestDto);
    }
}
