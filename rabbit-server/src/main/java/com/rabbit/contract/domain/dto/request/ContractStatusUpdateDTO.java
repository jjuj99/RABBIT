package com.rabbit.contract.domain.dto.request;

import com.rabbit.global.code.domain.enums.SysCommonCodes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "계약 상태 업데이트 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractStatusUpdateDTO {

    @Schema(description = "변경할 계약 상태", example = "CANCELED", required = true,
            allowableValues = {"REQUESTED", "MODIFICATION_REQUESTED", "CONTRACTED", "CANCELED"})
    @NotNull(message = "상태는 필수입니다")
    private SysCommonCodes.Contract contractStatus;

    @Schema(description = "상태 변경 사유", example = "사정이 생겨 취소합니다")
    private String reason;
}