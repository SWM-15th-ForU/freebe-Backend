package com.foru.freebe.product.service;

import java.util.List;

import com.foru.freebe.product.dto.ProductDiscountDto;
import com.foru.freebe.product.entity.Product;

public interface ProductDiscountService {
    void registerDiscount(List<ProductDiscountDto> productDiscountDtoList, Product product);
}
