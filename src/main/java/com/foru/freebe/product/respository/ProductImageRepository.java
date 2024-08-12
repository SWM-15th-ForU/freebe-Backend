package com.foru.freebe.product.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
}
