package com.rabbit.loan.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LentAuctionResponseDTO {
    private Integer crId;
    private String crName;

    private ZonedDateTime matDt;
    private BigInteger tokenId;

    private Long la;
    private BigDecimal ir;
    private Long totalAmount;

    private String repayType;
    private BigDecimal dir;

    private Boolean earlypayFlag;
    private BigDecimal earlypayFee;

    private Integer defCnt;
    private String creditScore;

    private String nftImageUrl;
}
