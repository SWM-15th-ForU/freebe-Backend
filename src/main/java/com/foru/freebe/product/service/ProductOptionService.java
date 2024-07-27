package com.foru.freebe.product.service;

import java.util.List;

import com.foru.freebe.product.dto.ProductOptionDto;
import com.foru.freebe.product.entity.Product;

public interface ProductOptionService {
    void registerProductOption(List<ProductOptionDto> productOptionDtoList, Product product);
}
