package com.foru.freebe.productOption.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.productOption.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
}
