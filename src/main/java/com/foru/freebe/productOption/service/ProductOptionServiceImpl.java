package com.foru.freebe.productOption.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.productOption.dto.ProductOptionDto;
import com.foru.freebe.productOption.entity.ProductOption;
import com.foru.freebe.productOption.repository.ProductOptionRepository;

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
