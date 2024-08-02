package com.foru.freebe.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
	ROLE_ANONYMOUS,
	ROLE_PENDING,
	ROLE_PHOTOGRAPHER,
	ROLE_CUSTOMER,
	ROLE_ADMIN
}
