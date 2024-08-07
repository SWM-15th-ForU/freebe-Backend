package com.foru.freebe.profile.entity;

import com.foru.freebe.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @Column(name = "profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    private String uniqueUrl;

    private String introductionContent;

    private String bannerImageUrl;

    public void assignUniqueUrl(String uniqueUrl) {
        this.uniqueUrl = uniqueUrl;
    }

    @Builder
    public Profile(String uniqueUrl, String introductionContent, String bannerImageUrl, Member member) {
        this.uniqueUrl = uniqueUrl;
        this.introductionContent = introductionContent;
        this.bannerImageUrl = bannerImageUrl;
        this.member = member;
    }
}