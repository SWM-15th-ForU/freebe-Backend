package com.foru.freebe.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotographerJoinRequest {
    @NotBlank(message = "Profile name must not be blank")
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "프로필명 입력 형식이 틀렸습니다")
    @Size(min = 3, max = 30, message = "프로필명은 최소 3자 이상 최대 30자 이하여야합니다")
    private String profileName;

    @NotNull
    private String contact;

    @AssertTrue
    private Boolean termsOfServiceAgreement;

    @AssertTrue
    private Boolean privacyPolicyAgreement;

    private Boolean marketingAgreement;

    @Builder
    public PhotographerJoinRequest(String profileName, String contact, Boolean termsOfServiceAgreement,
        Boolean privacyPolicyAgreement, Boolean marketingAgreement) {
        this.profileName = profileName;
        this.contact = contact;
        this.termsOfServiceAgreement = termsOfServiceAgreement;
        this.privacyPolicyAgreement = privacyPolicyAgreement;
        this.marketingAgreement = marketingAgreement;
    }
}
