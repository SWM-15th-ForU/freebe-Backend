package com.foru.freebe.member.entity;

import com.foru.freebe.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DeletedMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deleted_member_id")
    private Long id;

    @NotNull
    private Long kakaoId;

    @NotNull
    private Long memberId;

    @NotNull
    private String unlinkReason;

    @Builder
    public DeletedMember(Long kakaoId, Long memberId, String unlinkReason) {
        this.kakaoId = kakaoId;
        this.memberId = memberId;
        this.unlinkReason = unlinkReason;
    }
}
