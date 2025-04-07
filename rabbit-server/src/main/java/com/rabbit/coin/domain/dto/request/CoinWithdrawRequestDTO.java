package com.rabbit.coin.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 코인 출금 요청 DTO
 */

@Schema(description = "코인 출금 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinWithdrawRequestDTO {
    @Schema(description = "예금주", example = "이재용")
    private String name;

    @Schema(description = "계좌번호", example = "123-45678-9012-34")
    private String accountNumber;

    @Schema(description = "출금액", example = "10000")
    private long amount;
}
