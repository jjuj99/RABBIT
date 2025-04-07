package com.rabbit.auction.domain.dto.response;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyAuctionResponseDTO {
    private Integer auctionId;
    private ZonedDateTime bidDate;
    private SysCommonCodes.Auction auctionStatus;
    @Setter
    private String auctionStatusName; // 국제화된 상태명 필드 추가
    private Long price;
    private Long bidAmount;
    private String bidStatus; // enum에서 String으로 변경
    @Setter
    private String bidStatusName; // 국제화된 상태명 필드 추가
    private Long bidderNum; //입찰자 수

    // 생성자 수정 - String bidStatus
    public MyAuctionResponseDTO(Integer auctionId, ZonedDateTime bidDate,
                                SysCommonCodes.Auction auctionStatus, Long price,
                                Long bidAmount, String bidStatus, Long bidderNum) {
        this.auctionId = auctionId;
        this.bidDate = bidDate;
        this.auctionStatus = auctionStatus;
        this.price = price;
        this.bidAmount = bidAmount;
        this.bidStatus = bidStatus;
        this.bidderNum = bidderNum;
    }
}
