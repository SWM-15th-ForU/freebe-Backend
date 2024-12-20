package com.foru.freebe.product.dto.photographer;

import java.util.List;

import com.foru.freebe.notice.dto.NoticeDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProductDetailRequest {
    @NotNull
    private Long productId;

    @NotNull
    private List<String> existingUrls;

    @NotBlank
    @Size(max = 30, message = "Title cannot be longer than 30 characters")
    private String productTitle;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    private String productDescription;

    @NotNull(message = "Basic price must not be null")
    private Long basicPrice;

    @NotBlank(message = "PhotoPlace name must not be blank")
    @Size(max = 100, message = "촬영장소는 최대 100자까지 입력 가능합니다.")
    private String basicPlace;

    @NotEmpty(message = "Notice must not be empty")
    private List<NoticeDto> notices;

    @NotNull
    private Boolean allowPreferredPlace;

    @NotNull
    private List<ProductComponentDto> productComponents;

    private List<ProductOptionDto> productOptions;

    private List<ProductDiscountDto> productDiscounts;

    @Builder
    public UpdateProductDetailRequest(Long productId, List<String> existingUrls, String productTitle,
        String productDescription, Long basicPrice, String basicPlace, List<NoticeDto> notices,
        Boolean allowPreferredPlace,
        List<ProductComponentDto> productComponents, List<ProductOptionDto> productOptions,
        List<ProductDiscountDto> productDiscounts) {
        this.productId = productId;
        this.existingUrls = existingUrls;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.basicPrice = basicPrice;
        this.basicPlace = basicPlace;
        this.notices = notices;
        this.allowPreferredPlace = allowPreferredPlace;
        this.productComponents = productComponents;
        this.productOptions = productOptions;
        this.productDiscounts = productDiscounts;
    }
}
