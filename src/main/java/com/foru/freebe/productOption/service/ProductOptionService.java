package com.foru.freebe.productOption.service;

import java.util.List;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.productOption.dto.ProductOptionDto;

public interface ProductOptionService {
    void registerProductOption(List<ProductOptionDto> productOptionDtoList, Product product);
}
