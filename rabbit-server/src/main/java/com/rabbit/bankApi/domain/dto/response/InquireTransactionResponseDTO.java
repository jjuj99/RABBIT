package com.rabbit.bankApi.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbit.bankApi.domain.api.Header.ApiResponseHeader;
import com.rabbit.bankApi.domain.api.response.InquireTransactionApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "계좌 내역 조회 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquireTransactionResponseDTO {

    @JsonProperty("Header")
    ApiResponseHeader header;

    @JsonProperty("REC")
    InquireTransactionApiResponse rec;
}
