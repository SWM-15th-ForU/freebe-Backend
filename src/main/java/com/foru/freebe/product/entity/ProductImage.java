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
    private int imageOrder;

    @NotNull
    @Column(length = 600)
    private String thumbnailUrl;

    @NotNull
    @Column(length = 600)
    private String originUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void updateImageOrder(int imageOrder) {
        this.imageOrder = imageOrder;
    }

    public static ProductImage createProductImage(int imageOrder, String originUrl, String thumbnailUrl,
        Product product) {
        return new ProductImage(imageOrder, originUrl, thumbnailUrl, product);
    }

    private ProductImage(int imageOrder, String originUrl, String thumbnailUrl, Product product) {
        this.imageOrder = imageOrder;
        this.originUrl = originUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.product = product;
    }
}
