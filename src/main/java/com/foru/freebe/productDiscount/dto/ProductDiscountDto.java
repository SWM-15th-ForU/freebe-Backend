package com.foru.freebe.productDiscount.dto;

import com.foru.freebe.productDiscount.entity.DiscountType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDiscountDto {
    private String title;
    private DiscountType discountType;
    private Integer discountValue;
    private String description;
}
