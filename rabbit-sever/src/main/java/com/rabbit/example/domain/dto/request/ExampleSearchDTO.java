package com.rabbit.example.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbit.global.request.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Schema(description = "예시 목록 조회 조건")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleSearchDTO {

    @Schema(description = "검색 키워드", example = "example")
    private String keyword;

    @Schema(description = "카테고리 코드", example = "CATEGORY_A")
    private String categoryCode;

    @Schema(description = "활성 상태 필터")
    private Boolean isActive;

    @Schema(description = "시작 날짜", example = "2023-01-01")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다")
    private String startDate;

    @Schema(description = "종료 날짜", example = "2023-12-31")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다")
    private String endDate;
}