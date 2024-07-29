package com.foru.freebe.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.entity.ProductImage;
import com.foru.freebe.product.respository.ProductImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;

    @Override
    public void registerProductImage(List<String> productImageUrls, Product product) {
        for (String productImageUrl : productImageUrls) {
            ProductImage productImage = ProductImage.createProductImage(null, productImageUrl, product);
            productImageRepository.save(productImage);
        }
    }
}
