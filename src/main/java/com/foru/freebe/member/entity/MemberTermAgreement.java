package com.foru.freebe.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_term_agreement")
public class MemberTermAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @AssertTrue
    private Boolean termsOfServiceAgreement;

    @AssertTrue
    private Boolean privacyPolicyAgreement;

    private Boolean marketingAgreement;

    @Builder
    public MemberTermAgreement(Member member, Boolean termsOfServiceAgreement, Boolean privacyPolicyAgreement,
        Boolean marketingAgreement) {
        this.member = member;
        this.termsOfServiceAgreement = termsOfServiceAgreement;
        this.privacyPolicyAgreement = privacyPolicyAgreement;
        this.marketingAgreement = marketingAgreement;
    }
}
