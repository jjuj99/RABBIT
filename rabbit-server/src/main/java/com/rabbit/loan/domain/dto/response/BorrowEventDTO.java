package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "부채 관련 이벤트 DTO")
public class BorrowEventDTO {

    @Schema(description = "이벤트 종류", example = "상환")
    private String eventType;

    @Schema(description = "금액", example = "91")
    private Integer intAmt;

    @Schema(description = "보낸 주소", example = "0x3i3o...")
    private String from;

    @Schema(description = "받는 주소", example = "0xpwk3...")
    private String to;

    @Schema(description = "타임스탬프(hex)", example = "0x8fe2...3a7b")
    private String timestamp;
}
