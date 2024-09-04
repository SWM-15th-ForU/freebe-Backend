package com.foru.freebe.product.entity;

import com.foru.freebe.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @NotNull
    private String thumbnailUrl;

    @NotNull
    private String originUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private ProductImage(String thumbnailUrl, String originUrl, Product product) {
        this.thumbnailUrl = thumbnailUrl;
        this.originUrl = originUrl;
        this.product = product;
    }

    public static ProductImage createProductImage(String thumbnailUrl, String originUrl, Product product) {
        return new ProductImage(thumbnailUrl, originUrl, product);
    }
}
