package com.rabbit.auction.domain.dto.response;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionMyListResponseDTO {
    private Integer auctionId;       // 경매 ID
    private Long price;              // 경매 금액
    private ZonedDateTime endDate;   // 경매 종료일
    private BigInteger tokenId;      // 토큰 ID
    private String nftImageUrl;     // NFT 이미지
    private SysCommonCodes.Auction auctionStatus; // 경매 상태
}