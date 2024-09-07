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
    private String customerName;

    @NotBlank
    private String customerPhoneNumber;

    @NotNull
    private List<ProductComponentDto> productComponentDtoList;

    private List<ProductOptionDto> productOptionDtoList;

    @Builder
    public BasicReservationInfoResponse(String customerName, String customerPhoneNumber,
        List<ProductComponentDto> productComponentDtoList,
        List<ProductOptionDto> productOptionDtoList) {
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.productComponentDtoList = productComponentDtoList;
        this.productOptionDtoList = productOptionDtoList;
    }
}