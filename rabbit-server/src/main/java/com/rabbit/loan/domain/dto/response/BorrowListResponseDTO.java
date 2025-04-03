package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Schema(description = "부채 목록 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowListResponseDTO {

    private Integer contractId;
    private String tokenId;
    private String nftImage;
    private String crName;
    private String crWallet;
    private BigInteger la;
    private Double ir;
    private String matDt;
    private Integer remainTerms;
    private String pnStatus;
    private String nextMpDt;
    private Long nextAmount;
    private Long aoi;
    private Integer aoiDays;
}
