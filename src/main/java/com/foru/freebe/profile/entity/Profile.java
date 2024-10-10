package com.foru.freebe.profile.entity;

import com.foru.freebe.common.entity.BaseEntity;
import com.foru.freebe.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotBlank(message = "Profile name must not be blank")
    private String profileName;

    @NotBlank(message = "Contact must not be blank")
    private String contact;

    private String introductionContent;

    public void updateIntroductionContent(String introductionContent) {
        this.introductionContent = introductionContent;
    }

    @Builder
    public Profile(Member member, String profileName, String contact, String introductionContent) {
        this.member = member;
        this.profileName = profileName;
        this.contact = contact;
        this.introductionContent = introductionContent;
    }
}