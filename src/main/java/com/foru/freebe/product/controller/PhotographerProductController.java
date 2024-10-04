package com.foru.freebe.product.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foru.freebe.auth.model.MemberAdapter;
import com.foru.freebe.common.dto.ResponseBody;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.product.dto.customer.ProductDetailResponse;
import com.foru.freebe.product.dto.photographer.ProductRegisterRequest;
import com.foru.freebe.product.dto.photographer.ProductTitleDto;
import com.foru.freebe.product.dto.photographer.RegisteredProductResponse;
import com.foru.freebe.product.dto.photographer.UpdateProductDetailRequest;
import com.foru.freebe.product.dto.photographer.UpdateProductRequest;
import com.foru.freebe.product.service.PhotographerProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/photographer")
public class PhotographerProductController {
    private final PhotographerProductService photographerProductService;

    @PostMapping("/product")
    public ResponseEntity<ResponseBody<Void>> registerProduct(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @Valid @RequestPart(value = "request") ProductRegisterRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        Member photographer = memberAdapter.getMember();
        photographerProductService.registerProduct(request, images, photographer.getId());

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully added")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(responseBody);
    }

    @GetMapping("/product/list")
    public ResponseEntity<ResponseBody<List<RegisteredProductResponse>>> getRegisteredProductList(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member photographer = memberAdapter.getMember();
        List<RegisteredProductResponse> responseData = photographerProductService.getRegisteredProductList(
            photographer);

        ResponseBody<List<RegisteredProductResponse>> responseBody = ResponseBody
            .<List<RegisteredProductResponse>>builder()
            .message("Successfully retrieved list of registered products")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/product/status")
    public ResponseEntity<ResponseBody<Void>> updateProductActiveStatus(
        @Valid @RequestBody UpdateProductRequest updateProductRequest) {

        photographerProductService.updateProductActiveStatus(updateProductRequest);

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully updated product active status")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseBody<ProductDetailResponse>> getProductById(
        @PathVariable("productId") Long productId,
        @AuthenticationPrincipal MemberAdapter memberAdapter) {

        Member photographer = memberAdapter.getMember();
        ProductDetailResponse responseData = photographerProductService.getRegisteredProductInfo(productId,
            photographer.getId());

        ResponseBody<ProductDetailResponse> responseBody = ResponseBody.<ProductDetailResponse>builder()
            .message("Good Response")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @PutMapping("/product")
    public ResponseEntity<ResponseBody<Void>> updateProduct(
        @Valid @RequestPart(value = "request") UpdateProductDetailRequest request,
        @RequestPart(value = "images") List<MultipartFile> images,
        @AuthenticationPrincipal MemberAdapter memberAdapter) throws IOException {

        Member photographer = memberAdapter.getMember();
        photographerProductService.updateProductDetail(images, request, photographer.getId());

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully updated product info")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ResponseBody<Void>> deleteProduct(@AuthenticationPrincipal MemberAdapter memberAdapter,
        @PathVariable("productId") Long productId) {

        Member photographer = memberAdapter.getMember();
        photographerProductService.deleteProduct(productId, photographer.getId());

        ResponseBody<Void> responseBody = ResponseBody.<Void>builder()
            .message("Successfully delete product")
            .data(null)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }

    @GetMapping("/product/title")
    public ResponseEntity<ResponseBody<List<ProductTitleDto>>> getAllProductTitle(
        @AuthenticationPrincipal MemberAdapter memberAdapter) {
        Member photographer = memberAdapter.getMember();

        List<ProductTitleDto> responseData = photographerProductService.getAllProductTitle(photographer.getId());

        ResponseBody<List<ProductTitleDto>> responseBody = ResponseBody.<List<ProductTitleDto>>builder()
            .message("Successfully retrieved list of product titles")
            .data(responseData)
            .build();

        return ResponseEntity.status(HttpStatus.OK.value())
            .body(responseBody);
    }
}