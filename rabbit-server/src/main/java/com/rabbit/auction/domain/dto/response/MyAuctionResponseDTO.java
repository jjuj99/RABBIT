package com.rabbit.auction.domain.dto.response;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import lombok.*;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyAuctionResponseDTO {
    private Integer auctionId;
    private ZonedDateTime bidDate;
    private BigInteger tokenId;
    private SysCommonCodes.Auction auctionStatus;
    @Setter
    private String auctionStatusName; // 국제화된 상태명 필드 추가
    private Long price;
    private Long bidAmount;
    private String bidStatus; // enum에서 String으로 변경
    @Setter
    private String bidStatusName; // 국제화된 상태명 필드 추가
    private Long bidderNum; //입찰자 수
    private String nftImageUrl;

    public MyAuctionResponseDTO(
            Integer auctionId,
            ZonedDateTime bidDate,
            BigInteger tokenId,
            SysCommonCodes.Auction auctionStatus,
            Long price,
            Long bidAmount,
            String bidStatus,
            Long bidderNum
    ) {
        this.auctionId = auctionId;
        this.bidDate = bidDate;
        this.tokenId = tokenId;
        this.auctionStatus = auctionStatus;
        this.price = price;
        this.bidAmount = bidAmount;
        this.bidStatus = bidStatus;
        this.bidderNum = bidderNum;
    }

    public void setNftImage(String nftImage) {
        this.nftImageUrl = nftImage;
    }
}
