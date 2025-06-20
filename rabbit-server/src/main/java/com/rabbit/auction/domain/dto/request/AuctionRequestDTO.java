package com.rabbit.auction.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionRequestDTO {
    private Long minimumBid;
    private ZonedDateTime endDate;
    private BigInteger tokenId;
}
