package com.foru.freebe.productDiscount.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.productDiscount.entity.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
}
