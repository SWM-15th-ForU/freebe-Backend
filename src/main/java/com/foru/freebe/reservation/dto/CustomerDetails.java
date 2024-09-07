package com.foru.freebe.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerDetails {
    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhoneNumber;

    @NotBlank
    private String customerInstagramId;

    @Builder
    public CustomerDetails(String customerName, String customerPhoneNumber, String customerInstagramId) {
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerInstagramId = customerInstagramId;
    }
}
