package com.rabbit.coin.domain.dto.response;

import com.rabbit.coin.domain.enums.CoinLogType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Schema(description = "코인 입출금 내역 반환")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinLogListResponseDTO {
    private CoinLogType type;
    private Long amount;
    private ZonedDateTime createdAt;
}
