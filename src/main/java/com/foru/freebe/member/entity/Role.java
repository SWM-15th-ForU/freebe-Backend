package com.foru.freebe.member.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ANONYMOUS,
    PHOTOGRAPHER_PENDING,
    @JsonProperty("PHOTOGRAPHER")
    PHOTOGRAPHER,
    @JsonProperty("CUSTOMER")
    CUSTOMER,
    LEAVING,
    ADMIN
}
