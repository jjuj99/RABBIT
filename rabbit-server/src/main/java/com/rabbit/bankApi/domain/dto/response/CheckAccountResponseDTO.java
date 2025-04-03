package com.rabbit.bankApi.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbit.bankApi.domain.api.Header.ApiResponseHeader;
import com.rabbit.bankApi.domain.api.response.CheckAccountApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "1원 인증 검증 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAccountResponseDTO {

    @JsonProperty("Header")
    ApiResponseHeader header;

    @JsonProperty("REC")
    CheckAccountApiResponse rec;
}
