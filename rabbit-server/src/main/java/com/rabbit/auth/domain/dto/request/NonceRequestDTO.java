package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "난수 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonceRequestDTO {

    @Schema(description = "메타마스크 지갑 주소", example = "0xdbd1cb333978e1f6774e759d9742e8955cad3f6b")
    @NotBlank(message = "지갑 주소를 입력하지 않았습니다.")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "지갑 주소 형식이 올바르지 않습니다.")
    private String walletAddress;
}
