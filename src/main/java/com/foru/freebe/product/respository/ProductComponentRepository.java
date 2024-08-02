package com.foru.freebe.product.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.ProductComponent;

public interface ProductComponentRepository extends JpaRepository<ProductComponent, Long> {
}
