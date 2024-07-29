package com.foru.freebe.product.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
}
