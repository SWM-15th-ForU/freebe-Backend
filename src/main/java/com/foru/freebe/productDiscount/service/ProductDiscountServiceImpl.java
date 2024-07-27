package com.foru.freebe.productDiscount.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.productDiscount.dto.ProductDiscountDto;
import com.foru.freebe.productDiscount.entity.ProductDiscount;
import com.foru.freebe.productDiscount.repository.ProductDiscountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductDiscountServiceImpl implements ProductDiscountService {
    private final ProductDiscountRepository productDiscountRepository;

    @Override
    public void registerDiscount(List<ProductDiscountDto> productDiscountDtoList, Product product) {
        for (ProductDiscountDto productDiscountDto : productDiscountDtoList) {
            ProductDiscount productDiscount = ProductDiscount.builder()
                .title(productDiscountDto.getTitle())
                .discountType(productDiscountDto.getDiscountType())
                .discountValue(productDiscountDto.getDiscountValue())
                .description(productDiscountDto.getDescription())
                .product(product)
                .build();
            productDiscountRepository.save(productDiscount);
        }
    }
}
