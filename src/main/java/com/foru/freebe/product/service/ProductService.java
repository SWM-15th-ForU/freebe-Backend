package com.foru.freebe.product.service;

import com.foru.freebe.common.dto.ApiResponseDto;
import com.foru.freebe.product.dto.ProductRegisterRequestDto;

public interface ProductService {
    ApiResponseDto<Void> registerProduct(ProductRegisterRequestDto productRegisterRequestDto);
}
