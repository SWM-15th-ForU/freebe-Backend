package com.foru.freebe.profile.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String uniqueUrl;

	private String introductionContent;

	private String bannerImageUrl;

	@Builder
	public Profile(String uniqueUrl, String introductionContent, String bannerImageUrl) {
		this.uniqueUrl = uniqueUrl;
		this.introductionContent = introductionContent;
		this.bannerImageUrl = bannerImageUrl;
	}
}