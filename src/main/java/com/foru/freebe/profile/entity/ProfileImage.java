package com.foru.freebe.profile.entity;

import com.foru.freebe.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    private String bannerOriginUrl;

    private String profileOriginUrl;

    public void assignBannerOriginUrl(String bannerOriginUrl) {
        this.bannerOriginUrl = bannerOriginUrl;
    }

    public void assignProfileOriginUrl(String profileOriginUrl) {
        this.profileOriginUrl = profileOriginUrl;
    }

    @Builder
    public ProfileImage(Profile profile, String profileOriginUrl, String bannerOriginUrl) {
        this.profile = profile;
        this.profileOriginUrl = profileOriginUrl;
        this.bannerOriginUrl = bannerOriginUrl;
    }
}
