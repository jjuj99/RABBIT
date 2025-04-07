package com.rabbit.global.code.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 코드 수정 요청 DTO")
public class CommonCodeUpdateRequestDTO {

    @Schema(description = "코드명 (변경 시에만 입력)", example = "수정된 코드명")
    @Size(max = 100, message = "코드명은 최대 100자까지 입력 가능합니다")
    private String codeName;

    @Schema(description = "코드 설명 (변경 시에만 입력)", example = "수정된 설명")
    @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다")
    private String description;

    @Schema(description = "표시 순서 (변경 시에만 입력)", example = "2")
    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    private Integer displayOrder;

    @Schema(description = "활성화 여부 (변경 시에만 입력)", example = "false")
    private Boolean activeFlag;
}