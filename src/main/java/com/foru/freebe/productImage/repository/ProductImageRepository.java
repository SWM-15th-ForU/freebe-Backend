package com.foru.freebe.productImage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.productImage.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
