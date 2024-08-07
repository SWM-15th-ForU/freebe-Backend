package com.foru.freebe.auth.dto;

import com.foru.freebe.member.entity.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoleTypeRequest {
    @Enumerated(EnumType.STRING)
    @NotNull
    private String roleType;

    public Role getRole() {
        return Role.valueOf(roleType.toUpperCase());
    }
}
