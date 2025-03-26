package com.rabbit.auction.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class AuctionRequestDTO {
    private Long minimumBid;
    private ZonedDateTime endDate;
    private String tokenId;
    private String sellerSign;
}
