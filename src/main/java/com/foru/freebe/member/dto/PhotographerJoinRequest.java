package com.foru.freebe.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotographerJoinRequest {
    @NotBlank(message = "Profile name must not be blank")
    private String profileName;

    @AssertTrue
    private Boolean termsOfServiceAgreement;

    @AssertTrue
    private Boolean privacyPolicyAgreement;

    private Boolean marketingAgreement;

    @Builder
    public PhotographerJoinRequest(String profileName, Boolean termsOfServiceAgreement, Boolean privacyPolicyAgreement,
        Boolean marketingAgreement) {
        this.profileName = profileName;
        this.termsOfServiceAgreement = termsOfServiceAgreement;
        this.privacyPolicyAgreement = privacyPolicyAgreement;
        this.marketingAgreement = marketingAgreement;
    }
}
