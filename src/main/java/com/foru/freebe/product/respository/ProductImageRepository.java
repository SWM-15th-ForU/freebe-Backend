package com.foru.freebe.product.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
