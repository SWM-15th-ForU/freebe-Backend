package com.foru.freebe.product.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ApiResponse;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.dto.ProductRegisterRequestDto;
import com.foru.freebe.product.dto.RegisteredProductResponseDto;
import com.foru.freebe.product.dto.UpdateProductRequestDto;
import com.foru.freebe.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/")
    public ApiResponse<Void> registerProduct(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @RequestBody ProductRegisterRequestDto productRegisterRequestDto) {
        Member member = memberAdapter.getMember();
        return productService.registerProduct(member.getId(), productRegisterRequestDto);
    }

    @GetMapping("/registered-product/{id}")
    public ApiResponse<List<RegisteredProductResponseDto>> getRegisteredProductList(
        @PathVariable("id") Long memberId) {
        return productService.getRegisteredProductList(memberId);
    }

    @PutMapping("/update-status")
    public ApiResponse<Void> updateProductActiveStatus(@RequestBody UpdateProductRequestDto requestDto) {
        return productService.updateProductActiveStatus(requestDto);
    }

}
