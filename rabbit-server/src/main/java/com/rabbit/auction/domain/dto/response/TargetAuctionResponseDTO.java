package com.rabbit.auction.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class TargetAuctionResponseDTO {
    private Integer auctionId;
    private Long rp;
    private Integer rd;
    private BigDecimal rr;
    private Integer percentile;
}
