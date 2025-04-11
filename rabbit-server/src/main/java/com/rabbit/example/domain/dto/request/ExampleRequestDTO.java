package com.rabbit.example.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "예시 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleRequestDTO { 

    @Schema(description = "예시 제목", example = "예시 제목입니다", required = true)
    @NotBlank(message = "제목은 필수 입력값입니다")
    @Size(min = 2, max = 100, message = "제목은 2~100자 사이여야 합니다")
    private String title;

    @Schema(description = "예시 내용", example = "예시 내용입니다")
    @Size(max = 1000, message = "내용은 최대 1000자까지 입력 가능합니다")
    private String content;

    @Schema(description = "카테고리 코드", example = "CATEGORY_A")
    private String categoryCode;

    @Schema(description = "사용 여부", example = "true", defaultValue = "true")
    private Boolean isActive = true;
}
