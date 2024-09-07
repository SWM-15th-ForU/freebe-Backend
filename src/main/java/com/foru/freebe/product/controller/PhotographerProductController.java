package com.foru.freebe.product.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequest;
import com.foru.freebe.product.dto.photographer.RegisteredProductResponse;
import com.foru.freebe.product.dto.photographer.UpdateProductRequest;
import com.foru.freebe.product.service.PhotographerProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerProductController {
    private final PhotographerProductService productService;

    @PostMapping("/product")
    public ApiResponse<Void> registerProduct(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestPart(value = "request") ProductRegisterRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Member photographer = memberAdapter.getMember();
        return productService.registerProduct(request, images, photographer.getId());
    }

    @GetMapping("/product/list")
    public ApiResponse<List<RegisteredProductResponse>> getRegisteredProductList(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member photographer = memberAdapter.getMember();
        return productService.getRegisteredProductList(photographer);
    }

    @PutMapping("/product/status")
    public ApiResponse<Void> updateProductActiveStatus(@Valid @RequestBody UpdateProductRequest updateProductRequest) {
        return productService.updateProductActiveStatus(updateProductRequest);
    }
}