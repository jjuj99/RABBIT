package com.rabbit.bankApi.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Api Key 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponseDTO {

    private String managerId;
    private String apiKey;
    private String creationDate;
    private String expirationData;
}
