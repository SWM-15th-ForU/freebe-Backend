package com.foru.freebe.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PhotographerJoinRequest {
    @NotBlank(message = "Profile name must not be blank")
    private String profileName;

    @AssertTrue
    private Boolean termsOfServiceAgreement;

    @AssertTrue
    private Boolean privacyPolicyAgreement;

    private Boolean marketingAgreement;
}
