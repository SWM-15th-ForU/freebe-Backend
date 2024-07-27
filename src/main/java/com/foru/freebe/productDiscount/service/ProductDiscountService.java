package com.foru.freebe.productDiscount.service;

import java.util.List;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.productDiscount.dto.ProductDiscountDto;

public interface ProductDiscountService {
    void registerDiscount(List<ProductDiscountDto> productDiscountDtoList, Product product);
}
