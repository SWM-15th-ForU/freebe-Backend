package com.foru.freebe.product.entity;

import com.foru.freebe.common.entity.BaseEntity;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
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
public class Product extends BaseEntity {
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

    @NotNull
    private Long basicPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Product(String title, String description, ActiveStatus activeStatus, Long basicPrice, Member member) {
        this.title = title;
        this.description = description;
        this.activeStatus = activeStatus;
        this.basicPrice = basicPrice;
        this.member = member;
    }

    public static Product createProductAsActive(String title, String description, Long basicPrice, Member member) {
        return new Product(title, description, ActiveStatus.ACTIVE, basicPrice, member);
    }

    public static Product createProductAsActiveWithoutDescription(String title, Long basicPrice, Member member) {
        return new Product(title, null, ActiveStatus.ACTIVE, basicPrice, member);
    }

    public void updateProductActiveStatus(ActiveStatus newStatus) {
        if (this.activeStatus == newStatus) {
            throw new RestApiException(ProductErrorCode.INVALID_ACTIVE_STATUS);
        }
        this.activeStatus = newStatus;
    }

    public void assignTitle(String newTitle) {
        this.title = newTitle;
    }

    public void assignDescription(String newDescription) {
        this.description = newDescription;
    }

    public void assignBasicPrice(Long basicPrice) {
        this.basicPrice = basicPrice;
    }
}
