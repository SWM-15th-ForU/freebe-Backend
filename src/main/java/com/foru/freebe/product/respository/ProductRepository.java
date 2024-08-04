package com.foru.freebe.product.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
