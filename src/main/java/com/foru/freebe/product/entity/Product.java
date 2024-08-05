package com.foru.freebe.product.entity;

import com.foru.freebe.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @NotNull
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ActiveStatus activeStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Product(String title, String description, ActiveStatus activeStatus, Member member) {
        this.title = title;
        this.description = description;
        this.activeStatus = activeStatus;
        this.member = member;
    }

    public static Product createProductAsActive(String title, String description, Member member) {
        return new Product(title, description, ActiveStatus.ACTIVE, member);
    }

    public static Product createProductAsActiveWithoutDescription(String title, Member member) {
        return new Product(title, null, ActiveStatus.ACTIVE, member);
    }

    public void updateProductActiveStatus(ActiveStatus newStatus) {
        this.activeStatus = newStatus;
    }
}
