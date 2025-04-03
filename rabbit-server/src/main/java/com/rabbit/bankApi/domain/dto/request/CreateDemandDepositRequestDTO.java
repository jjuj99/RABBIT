package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "수시입출금 계좌 상품 생성 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDemandDepositRequestDTO {

    private String bankCode;
    private String accountName;
    private String accountDescription;
}
