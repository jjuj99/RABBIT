package com.rabbit.promissorynote.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "중도 상환 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepaymentRequestDTO {

    private Long prepaymentAmount;
}
