package com.foru.freebe.auth.dto;

import com.foru.freebe.member.entity.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public class RoleTypeRequest {
	@Enumerated(EnumType.STRING)
	@NotNull
	private String userType;

	public Role getRole() {
		return Role.valueOf(userType.toUpperCase());
	}

	public String getUserType() {
		return userType;
	}
}
