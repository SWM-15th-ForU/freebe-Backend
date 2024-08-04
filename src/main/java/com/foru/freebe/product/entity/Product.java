package com.foru.freebe.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public Product(String title, String description, ActiveStatus activeStatus) {
        this.title = title;
        this.description = description;
        this.activeStatus = activeStatus;
    }

    public static Product createProductAsActive(String title, String description) {
        return new Product(title, description, ActiveStatus.ACTIVE);
    }

    public static Product createProductAsActiveWithoutDescription(String title) {
        return new Product(title, null, ActiveStatus.ACTIVE);
    }
}
