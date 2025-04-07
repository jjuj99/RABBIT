package com.rabbit.auction.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionRetryMessage {
    private Integer auctionId;
    private int retryCount;
}
