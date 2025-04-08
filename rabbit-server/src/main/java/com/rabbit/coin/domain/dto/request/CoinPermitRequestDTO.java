package com.rabbit.coin.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;

@Schema(description = "코인 permit 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPermitRequestDTO {
    private String owner;
    private String spender;
    private BigInteger value;
    private BigInteger deadline;
    private String signature;
}