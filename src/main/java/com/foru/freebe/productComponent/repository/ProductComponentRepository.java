package com.foru.freebe.productComponent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.productComponent.entity.ProductComponent;

public interface ProductComponentRepository extends JpaRepository<ProductComponent, Long> {
}
