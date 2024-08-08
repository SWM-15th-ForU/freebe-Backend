package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.product.dto.customer.ProductResponse;
import com.foru.freebe.product.service.CustomerProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer/product")
public class CustomerProductController {
    private final CustomerProductService customerProductService;

    @GetMapping("/{id}")
    public ApiResponse<List<ProductResponse>> getAllProducts(@PathVariable("id") Long photographerId) {
        return customerProductService.getAllProductsByPhotographerId(photographerId);
    }
}
