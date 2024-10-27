package com.foru.freebe.reservation.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormRegisterRequest {
    @NotBlank(message = "Profile name must not be blank")
    private String profileName;

    @NotNull(message = "Product id must not be null")
    private Long productId;

    @NotBlank(message = "Instagram ID must not be blank")
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "인스타그램 아이디 입력 형식이 틀렸습니다")
    @Size(min = 3, max = 30, message = "인스타그램 아이디는 최소 3자 이상 최대 30자 이하여야합니다")
    private String instagramId;

    @NotNull(message = "Preferred dates must not be null")
    private Map<Integer, TimeSlot> preferredDates;

    @Size(max = 100, message = "촬영장소는 최대 100자까지 입력 가능합니다.")
    private String preferredPlace;

    private Map<Integer, PhotoOption> photoOptions;

    @NotEmpty
    @Size(min = 1)
    private List<String> existingImages;

    @Size(max = 500, message = "메모는 최대 500자까지 입력 가능합니다.")
    private String customerMemo;

    @NotNull
    private Long totalPrice;

    @NotNull
    private Boolean noticeAgreement;
}

