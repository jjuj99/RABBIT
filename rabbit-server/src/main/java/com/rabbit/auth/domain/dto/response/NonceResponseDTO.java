package com.rabbit.auth.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "서명 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonceResponseDTO {

    private String nonce;
}
