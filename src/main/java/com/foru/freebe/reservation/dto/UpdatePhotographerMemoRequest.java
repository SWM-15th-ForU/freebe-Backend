package com.foru.freebe.reservation.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePhotographerMemoRequest {
    @Size(max = 500, message = "메모는 최대 500자까지 입력 가능합니다.")
    private String photographerMemo;
}
