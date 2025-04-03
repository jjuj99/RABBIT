package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Schema(description = "부채 상세 정보 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDetailResponseDTO {

    private String contractId;
    private String tokenId;
    private String nftImage;
    private String crName;
    private String crWallet;
    private BigInteger la;
    private Long totalAmount;
    private String repayType;
    private Double ir;
    private Integer dir;
    private Integer defCnt;
    private String contractDt;
    private String matDt;
    private Integer remainTerms;
    private Double progressRate;
    private String pnStatus;
    private String nextMpDt;
    private Long nextAmount;
    private Long aoi;
    private Integer aoiDays;
    private Boolean earlypayFlag;
    private Double earlypayFee;
    private Integer accel;
    private Double accelDir;
    private List<String> addTerms;
    private List<BorrowEventDTO> eventList;
}
