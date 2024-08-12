package com.foru.freebe.product.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductDiscount;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {
    List<ProductDiscount> findByProduct(Product product);
}
