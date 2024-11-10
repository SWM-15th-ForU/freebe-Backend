package com.foru.freebe.product.entity;

import java.util.Map;

import org.hibernate.annotations.Type;

import com.foru.freebe.common.entity.BaseEntity;
import com.foru.freebe.errors.errorcode.ProductErrorCode;
import com.foru.freebe.errors.exception.RestApiException;
import com.foru.freebe.member.entity.Member;
import com.foru.freebe.reservation.dto.PhotoNotice;

import io.hypersistence.utils.hibernate.type.json.JsonType;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = @UniqueConstraint(
        name = "unique_title",
        columnNames = {"title", "member_id"})
)
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

    @NotBlank(message = "PhotoPlace name must not be blank")
    private String basicPlace;

    @NotNull
    private Boolean allowPreferredPlace;

    @Type(JsonType.class)
    @Column(name = "photo_notice", columnDefinition = "longtext")
    private Map<String, PhotoNotice> photoNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Product(String title, String description, ActiveStatus activeStatus, Long basicPrice, String basicPlace,
        Boolean allowPreferredPlace, Map<String, PhotoNotice> photoNotice, Member member) {
        this.title = title;
        this.description = description;
        this.activeStatus = activeStatus;
        this.basicPrice = basicPrice;
        this.basicPlace = basicPlace;
        this.allowPreferredPlace = allowPreferredPlace;
        this.photoNotice = photoNotice;
        this.member = member;
    }

    public void updateProductActiveStatus(ActiveStatus newStatus) {
        if (this.activeStatus == newStatus) {
            throw new RestApiException(ProductErrorCode.INVALID_ACTIVE_STATUS);
        }
        this.activeStatus = newStatus;
    }

    public void assignBasicProductInfo(String updateTitle, String updateDescription, Long updateBasicPrice,
        String updateBasicPlace, Boolean updateAllowPreferredPlace, Map<String, PhotoNotice> photoNotice) {
        this.title = updateTitle;
        this.description = updateDescription;
        this.basicPrice = updateBasicPrice;
        this.basicPlace = updateBasicPlace;
        this.allowPreferredPlace = updateAllowPreferredPlace;
        this.photoNotice = photoNotice;
    }
}
