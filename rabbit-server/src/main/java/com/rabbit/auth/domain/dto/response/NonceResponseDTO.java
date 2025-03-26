package com.rabbit.auth.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "서명 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonceResponseDTO {

    private String nonce;
}
