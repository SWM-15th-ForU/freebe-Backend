package com.foru.freebe.product.dto;

import com.foru.freebe.product.entity.ActiveStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProductRequestDto {
    @NotNull
    private Long productId;
    @NotNull
    private ActiveStatus activeStatus;
}
