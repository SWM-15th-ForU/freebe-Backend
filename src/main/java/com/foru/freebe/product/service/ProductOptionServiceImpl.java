package com.foru.freebe.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.product.dto.ProductOptionDto;
import com.foru.freebe.product.entity.ProductOption;
import com.foru.freebe.product.respository.ProductOptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductOptionServiceImpl implements ProductOptionService {
    private ProductOptionRepository productOptionRepository;

    @Override
    public void registerProductOption(List<ProductOptionDto> productOptionDtoList, Product product) {
        for (ProductOptionDto productOptionDto : productOptionDtoList) {
            ProductOption productOption = ProductOption.builder()
                .title(productOptionDto.getTitle())
                .price(productOptionDto.getPrice())
                .description(productOptionDto.getDescription())
                .product(product)
                .build();

            productOptionRepository.save(productOption);
        }
    }
}
