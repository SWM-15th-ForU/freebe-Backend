package com.foru.freebe.reservation.dto;

import java.util.List;

import com.foru.freebe.product.dto.photographer.ProductComponentDto;
import com.foru.freebe.product.dto.photographer.ProductOptionDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasicReservationInfoResponse {
    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    private String instagramId;

    @NotNull
    private Long basicPrice;

    @NotBlank
    private String basicPlace;

    @NotNull
    private Boolean allowPreferredPlace;

    @NotNull
    private List<ProductComponentDto> productComponentDtoList;

    private List<ProductOptionDto> productOptionDtoList;

    @Builder
    public BasicReservationInfoResponse(String name, String phoneNumber, String instagramId, Long basicPrice,
        String basicPlace, Boolean allowPreferredPlace, List<ProductComponentDto> productComponentDtoList,
        List<ProductOptionDto> productOptionDtoList) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.instagramId = instagramId;
        this.basicPrice = basicPrice;
        this.basicPlace = basicPlace;
        this.allowPreferredPlace = allowPreferredPlace;
        this.productComponentDtoList = productComponentDtoList;
        this.productOptionDtoList = productOptionDtoList;
    }
}