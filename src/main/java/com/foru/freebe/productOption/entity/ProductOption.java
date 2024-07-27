package com.foru.freebe.productOption.entity;

import com.foru.freebe.product.entity.Product;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_option_id")
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Integer price;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    private ProductOption(String title, Integer price, String description, Product product) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.product = product;

        validatePrice(price);
    }

    private void validatePrice(Integer price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
    }
}
