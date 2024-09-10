package com.foru.freebe.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerDetails {
    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String instagramId;

    @Builder
    public CustomerDetails(String name, String phoneNumber, String instagramId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.instagramId = instagramId;
    }
}
