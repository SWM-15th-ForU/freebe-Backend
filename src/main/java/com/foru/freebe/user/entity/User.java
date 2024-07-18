package com.foru.freebe.user.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@NotNull
	private Long kakaoId;

	private String instagramId;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Role role;

	@NotNull
	private String name;

	@NotNull
	private String email;

	@NotNull
	private String phoneNumber;

	private Integer birthyear;

	private String gender;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@Builder
	public User(Long kakaoId, String name, String email, String phoneNumber, Role role) {
		this.kakaoId = kakaoId;
		this.instagramId = null;
		this.role = role;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.birthyear = null;
		this.gender = null;
	}
}
