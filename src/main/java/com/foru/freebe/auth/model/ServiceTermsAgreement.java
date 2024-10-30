package com.foru.freebe.auth.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServiceTermsAgreement {
    @JsonProperty("id")
    private Long kakaoId;

    @JsonProperty("service_terms")
    private List<ServiceTerms> scopes;

    public Boolean getMarketingServiceTermsAgreement() {
        if (scopes == null || scopes.isEmpty()) {
            return false;
        }
        return scopes.get(0).getAgreed();
    }
}
