package com.foru.freebe.productComponent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.foru.freebe.product.entity.Product;
import com.foru.freebe.productComponent.dto.ProductComponentDto;
import com.foru.freebe.productComponent.entity.ProductComponent;
import com.foru.freebe.productComponent.repository.ProductComponentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductComponentServiceImpl implements ProductComponentService {
    private final ProductComponentRepository productComponentRepository;

    @Override
    public void registerProductComponent(List<ProductComponentDto> productComponentDtoList, Product product) {
        for (ProductComponentDto productComponentDto : productComponentDtoList) {
            ProductComponent productComponent = ProductComponent.builder()
                .title(productComponentDto.getTitle())
                .content(productComponentDto.getContent())
                .description(productComponentDto.getDescription())
                .product(product)
                .build();

            productComponentRepository.save(productComponent);
        }
    }
}
