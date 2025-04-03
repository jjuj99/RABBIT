package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "계좌 생성 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDemandAccountRequestDTO {

    private String userKey;
    private String accountTypeUniqueNo;
}
