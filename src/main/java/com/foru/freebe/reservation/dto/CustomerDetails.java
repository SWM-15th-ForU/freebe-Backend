package com.foru.freebe.reservation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomerDetails {
    private String name;
    private String phoneNumber;
    private String instagramId;

    @Builder
    public CustomerDetails(String name, String phoneNumber, String instagramId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.instagramId = instagramId;
    }
}
