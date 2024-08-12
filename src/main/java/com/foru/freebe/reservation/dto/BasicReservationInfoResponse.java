package com.foru.freebe.reservation.dto;

import java.util.List;

import com.foru.freebe.product.dto.ProductComponentDto;
import com.foru.freebe.product.dto.ProductOptionDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasicReservationInfoResponse {
    private String name;
    private String phoneNumber;
    private List<ProductComponentDto> productComponentDtoList;
    private List<ProductOptionDto> productOptionDtoList;

    @Builder
    public BasicReservationInfoResponse(String name, String phoneNumber,
        List<ProductComponentDto> productComponentDtoList,
        List<ProductOptionDto> productOptionDtoList) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.productComponentDtoList = productComponentDtoList;
        this.productOptionDtoList = productOptionDtoList;
    }
}