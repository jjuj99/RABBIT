package com.rabbit.bankApi.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbit.bankApi.domain.api.Header.ApiResponseHeader;
import com.rabbit.bankApi.domain.api.response.CreateDemandAccountApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "계좌 생성 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDemandAccountResponseDTO {

    @JsonProperty("Header")
    ApiResponseHeader header;

    @JsonProperty("REC")
    CreateDemandAccountApiResponse rec;
}
