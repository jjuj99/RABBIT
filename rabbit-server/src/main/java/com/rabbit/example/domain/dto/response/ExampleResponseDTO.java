package com.rabbit.example.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "예시 응답 DTO",
        example = "{\n" +
                "  \"id\": 1,\n" +
                "  \"title\": \"예시 제목입니다\",\n" +
                "  \"content\": \"예시 내용입니다\",\n" +
                "  \"categoryCode\": \"CATEGORY_A\",\n" +
                "  \"isActive\": true,\n" +
                "  \"createdAt\": \"2023-01-01T12:00:00\",\n" +
                "  \"updatedAt\": \"2023-01-02T12:00:00\"\n" +
                "}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExampleResponseDTO {

    @Schema(description = "예시 ID", example = "1")
    private Long id;

    @Schema(description = "예시 제목", example = "예시 제목입니다")
    private String title;

    @Schema(description = "예시 내용", example = "예시 내용입니다")
    private String content;

    @Schema(description = "카테고리 코드", example = "CATEGORY_A")
    private String categoryCode;

    @Schema(description = "사용 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "생성 일시", example = "2023-01-01T12:00:00", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2023-01-02T12:00:00", type = "string", format = "date-time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // 간단한 생성자 추가
    public ExampleResponseDTO(Long id, String title) {
        this.id = id;
        this.title = title;
        this.content = "기본 내용";
        this.categoryCode = "CATEGORY_A";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}