package com.rabbit.auction.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ComparisonAuctionResponseDTO {
    private Integer auctionId;
    private Long rp;    //잔여 원금
    private Integer rd; //잔여 상환일
    private BigDecimal rr;  //수익률
}
