package com.foru.freebe.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
	ANONYMOUS,
	PENDING,
	PHOTOGRAPHER,
	CUSTOMER,
	ADMIN
}
