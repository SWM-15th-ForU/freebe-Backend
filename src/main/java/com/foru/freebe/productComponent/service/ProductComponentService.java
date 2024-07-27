package com.foru.freebe.productComponent.service;

import java.util.List;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.productComponent.dto.ProductComponentDto;

public interface ProductComponentService {
    void registerProductComponent(List<ProductComponentDto> productComponentDtoList, Product product);
}
