package com.rabbit.global.code.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rabbit.global.code.domain.entity.CommonCode;
import com.rabbit.global.code.domain.entity.SysCommonCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON 응답에서 제외
@Schema(description = "공통 코드 응답 DTO")
public class CommonCodeResponseDTO {

    @Schema(description = "코드 ID", example = "1")
    private Long codeId;

    @Schema(description = "코드 타입", example = "AUCTION_STATUS", required = true)
    private String codeType;

    @Schema(description = "코드 값", example = "ING", required = true)
    private String code;

    @Schema(description = "코드명", example = "진행중", required = true)
    private String codeName;

    @JsonIgnore  // JSON 응답에서 제외
    @Schema(hidden = true)  // Swagger에서 숨김
    private String originalCodeName;

    @Schema(description = "코드 설명", example = "경매가 활성화되어 입찰을 받는 중")
    private String description;

    @Schema(description = "표시 순서", example = "1", required = true)
    private Integer displayOrder;

    @Schema(description = "활성화 여부", example = "true", required = true)
    private boolean activeFlag;

    @Schema(description = "생성일시", example = "2025-03-30 12:34:56")
    private ZonedDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-03-30 12:34:56")
    private ZonedDateTime updatedAt;

    /**
     * SysCommonCode 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * @param entity SysCommonCode 엔티티
     * @return CommonCodeResponseDTO 인스턴스
     */
    public static CommonCodeResponseDTO from(SysCommonCode entity) {
        return CommonCodeResponseDTO.builder()
                .codeId(entity.getCodeId())
                .codeType(entity.getCodeType())
                .code(entity.getCode())
                .codeName(entity.getCodeName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .activeFlag(entity.isActiveFlag())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * CommonCode 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * @param entity CommonCode 엔티티
     * @return CommonCodeResponseDTO 인스턴스
     */
    public static CommonCodeResponseDTO from(CommonCode entity) {
        return CommonCodeResponseDTO.builder()
                .codeId(entity.getCodeId())
                .codeType(entity.getCodeType())
                .code(entity.getCode())
                .codeName(entity.getCodeName())
                .description(entity.getDescription())
                .displayOrder(entity.getDisplayOrder())
                .activeFlag(entity.isActiveFlag())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}