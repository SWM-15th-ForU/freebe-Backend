package com.foru.freebe.product.dto.photographer;

import com.foru.freebe.product.entity.ActiveStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisteredProductResponse {
    @NotNull
    private Long productId;

    @NotBlank
    private String productTitle;

    @NotBlank(message = "Representative image must not be blank")
    private String representativeImage;

    @PositiveOrZero
    private Integer reservationCount;

    @NotNull
    private ActiveStatus activeStatus;
    //TODO 추후 상품 대표 사진에 대한 로직 처리와 함께 대표사진 경로에 대한 필드 추가

    @Builder
    public RegisteredProductResponse(Long productId, String productTitle, String representativeImage,
        Integer reservationCount, ActiveStatus activeStatus) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.representativeImage = representativeImage;
        this.reservationCount = reservationCount;
        this.activeStatus = activeStatus;
    }
}
