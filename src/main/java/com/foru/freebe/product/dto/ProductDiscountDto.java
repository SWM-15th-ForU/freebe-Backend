package com.foru.freebe.product.dto;

import com.foru.freebe.product.entity.DiscountType;

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
