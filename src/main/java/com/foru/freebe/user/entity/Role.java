package com.foru.freebe.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
	ROLE_ANONYMOUS,
	ROLE_USER,
	ROLE_ADMIN
}
