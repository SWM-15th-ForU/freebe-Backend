package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.product.service.CustomerProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/product")
public class CustomProductController {
    private final CustomerProductService customerProductService;

    @GetMapping("/images/{id}")
    public ApiResponseDto<List<String>> getReferenceImages(@PathVariable("id") Long productId) {
        return customerProductService.getReferenceImages(productId);
    }
}
