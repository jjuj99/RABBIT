package com.rabbit.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "1원 인증 송금 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountAuthSendRequestDTO {

    private String email;
    private String accountNumber;
}
