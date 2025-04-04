package com.rabbit.contract.domain.dto.request;

import com.rabbit.global.code.domain.enums.SysCommonCodes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "계약 검색 조건")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractSearchDTO {

    @Schema(description = "계약 유형 (sent: 보낸 계약, received: 받은 계약)", example = "sent", defaultValue = "sent")
    private String type = "sent";

    @Schema(description = "검색 키워드 (채권자/채무자 이름)", example = "홍길동")
    private String keyword;

    @Schema(description = "계약 상태", example = "PENDING")
    private SysCommonCodes.Contract contractStatus;

    @Schema(description = "시작 날짜", example = "2025-04-01")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다")
    private String startDate;

    @Schema(description = "종료 날짜", example = "2025-04-30")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다")
    private String endDate;
}