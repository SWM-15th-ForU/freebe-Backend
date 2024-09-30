package com.foru.freebe.product.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProduct(Product product);

    void deleteByProduct(Product product);
}
