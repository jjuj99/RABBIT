package com.rabbit.auction.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResponseDTO {
    private Integer auctionId;          // 경매 ID
    private Long price;              // 금액
    private ZonedDateTime endDate;   // 경매 종료일
    private BigDecimal ir;           // 이자율
    private ZonedDateTime createdAt; // 최초 등록일
    private String repayType;        // 상환 방식
    private Long totalAmount;             // 만기수취액
    private ZonedDateTime matDt;     // 만기일
    private BigDecimal dir;          // 연체 이자율
    private Long la;    //원금
    private Boolean earlypayFlag;   // 중도상환 가능 여부
    private BigDecimal earlypayFee;  // 중도 상환 수수료
    private Integer creditScore;     // 신용점수
    private Integer defCnt;          // 연체 횟수
}

