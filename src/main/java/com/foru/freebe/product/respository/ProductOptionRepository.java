package com.foru.freebe.product.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}
