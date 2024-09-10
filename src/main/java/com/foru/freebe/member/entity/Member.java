package com.foru.freebe.member.entity;

import com.foru.freebe.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    private Long kakaoId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 20, nullable = false)
    private Role role;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    private Integer birthYear;

    private String gender;

    private String instagramId;

    public void assignRole(Role role) {
        this.role = role;
    }

    public void assignInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public String getAuthority() {
        return "ROLE_" + role.name();
    }

    @Builder
    public Member(Long kakaoId, Role role, String name, String email, String phoneNumber, Integer birthyear,
        String gender, String instagramId) {
        this.kakaoId = kakaoId;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthYear = birthyear;
        this.gender = gender;
        this.instagramId = instagramId;
    }

    public static MemberBuilder builder(Long kakaoId, Role role, String name, String email, String phoneNumber) {
        return new MemberBuilder()
            .kakaoId(kakaoId)
            .role(role)
            .name(name)
            .email(email)
            .phoneNumber(phoneNumber);
    }
}
