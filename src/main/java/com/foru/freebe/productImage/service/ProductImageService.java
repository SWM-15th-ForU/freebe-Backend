package com.foru.freebe.productImage.service;

import java.util.List;

import com.foru.freebe.product.entity.Product;

public interface ProductImageService {
    void registerProductImage(List<String> productImageUrls, Product product);
}
