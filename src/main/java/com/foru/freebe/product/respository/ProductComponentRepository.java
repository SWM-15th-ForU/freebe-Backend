package com.foru.freebe.product.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductComponent;

public interface ProductComponentRepository extends JpaRepository<ProductComponent, Long> {
    List<ProductComponent> findByProduct(Product product);

    void deleteByProduct(Product product);
}
