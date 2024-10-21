package com.foru.freebe.product.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    List<ProductImage> findByProduct(Product product);

    void deleteByProduct(Product product);

    Optional<ProductImage> findByThumbnailUrl(String thumbnailUrl);

    Optional<ProductImage> findByOriginUrl(String originUrl);
}
