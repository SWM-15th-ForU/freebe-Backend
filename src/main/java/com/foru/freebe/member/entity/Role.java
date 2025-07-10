package com.foru.freebe.member.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * Role 클래스
 */
@AllArgsConstructor
@Getter
public enum Role {
    ANONYMOUS,
    PHOTOGRAPHER_PENDING,
    @JsonProperty("PHOTOGRAPHER")
    PHOTOGRAPHER,
    @JsonProperty("CUSTOMER")
    CUSTOMER,
    @JsonProperty("LEAVING")
    LEAVING,
    ADMIN
}
