package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Api Key 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyRequestDTO {

    private String managerId;
}
