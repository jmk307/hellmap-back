package com.jimin.hellmap.domain.report.dto;

import jakarta.validation.constraints.*;

public record ReportUpdateRequestDto(
        @NotNull(message = "감정을 선택해주세요")
        @Pattern(regexp = "^(SCARY|ANNOYING|FUNNY)$", message = "올바른 감정을 선택해주세요")
        String emotion,

        @NotNull(message = "제목을 입력해주세요")
        @Size(min = 5, max = 100, message = "제목은 5자 이상 100자 이하로 입력해주세요")
        String title,

        @NotNull(message = "내용을 입력해주세요")
        @Size(min = 10, max = 1000, message = "내용은 10자 이상 1000자 이하로 입력해주세요")
        String content,

        @NotNull(message = "주소를 입력해주세요")
        @NotBlank(message = "주소를 입력해주세요")
        String address,  // 전체 주소

        @NotNull(message = "행정구역 코드를 입력해주세요")
        Long regionCode,   // 행정구역 코드

        @NotNull(message = "위도를 입력해주세요")
        @DecimalMin(value = "33", message = "올바른 위도를 입력해주세요")
        @DecimalMax(value = "38.5", message = "올바른 위도를 입력해주세요")
        Double latitude,

        @NotNull(message = "경도를 입력해주세요")
        @DecimalMin(value = "125", message = "올바른 경도를 입력해주세요")
        @DecimalMax(value = "132", message = "올바른 경도를 입력해주세요")
        Double longitude,

        boolean keepExistingImage  // 기존 이미지 유지 여부
) {
}
