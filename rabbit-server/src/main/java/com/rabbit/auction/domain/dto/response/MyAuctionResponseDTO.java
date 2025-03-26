package com.rabbit.auction.domain.dto.response;

import com.rabbit.auction.domain.enums.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyAuctionResponseDTO {
    private Integer auctionId;
    private ZonedDateTime bidDate;
    private AuctionStatus auctionStatus;
    private Long price;
    private Long bidAmount;
    private String bidStatus;
    private Long bidderNum; //입찰자 수
}
