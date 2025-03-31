package com.rabbit.global.code.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 코드 생성 요청 DTO")
public class CommonCodeRequestDTO {

    @NotBlank(message = "코드 타입은 필수입니다")
    @Size(max = 50, message = "코드 타입은 최대 50자까지 입력 가능합니다")
    @Schema(description = "코드 타입", example = "AUCTION_STATUS", required = true)
    private String codeType;

    @NotBlank(message = "코드 값은 필수입니다")
    @Size(max = 50, message = "코드 값은 최대 50자까지 입력 가능합니다")
    @Schema(description = "코드 값", example = "ING", required = true)
    private String code;

    @NotBlank(message = "코드 이름은 필수입니다")
    @Size(max = 100, message = "코드 이름은 최대 100자까지 입력 가능합니다")
    @Schema(description = "코드 이름", example = "새 코드", required = true)
    private String codeName;

    @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다")
    @Schema(description = "코드 설명", example = "사용자 정의 코드에 대한 설명")
    private String description;

    @NotNull(message = "표시 순서는 필수입니다")
    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    @Schema(description = "표시 순서", example = "1", required = true)
    private Integer displayOrder;

    @NotNull(message = "활성화 여부는 필수입니다")
    @Schema(description = "활성화 여부", example = "true", required = true)
    private Boolean activeFlag;
}