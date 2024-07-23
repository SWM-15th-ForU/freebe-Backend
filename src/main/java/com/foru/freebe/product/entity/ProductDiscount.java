package com.foru.freebe.product.entity;

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
public class ProductDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_discount_id")
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private Integer discountValue;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ProductDiscount(String title, DiscountType discountType, Integer discountValue, String description,
        Product product) {
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.description = description;
        this.product = product;

        validateDiscountValue();
    }

    private void validateDiscountValue() {
        if (discountType == DiscountType.RATE && (discountValue < 0 || discountValue > 100)) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }
        if (discountType == DiscountType.AMOUNT && discountValue <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
    }
}
