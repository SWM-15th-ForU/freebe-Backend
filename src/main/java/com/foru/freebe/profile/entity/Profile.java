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
	private String uniqueLink;

	private String introductionContent;

	private String bannerImageUrl;

	@Builder
	public Profile(String uniqueLink, String introductionContent, String bannerImageUrl) {
		this.uniqueLink = uniqueLink;
		this.introductionContent = introductionContent;
		this.bannerImageUrl = bannerImageUrl;
	}
}