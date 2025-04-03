package com.rabbit.blockchain.domain.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigInteger;

@Data
@Builder
@Getter
public class RepaymentInfoDTO {
    private BigInteger tokenId;              // 토큰 ID
    private BigInteger initialPrincipal;     // 초기 원금
    private BigInteger remainingPrincipal;   // 남은 원금
    private BigInteger interestRate;         // 연이자율
    private BigInteger defaultInterestRate;  // 연체 이자율
    private String nextPaymentDate;          // 다음 납부일 (YYYY-MM-DD)
    private Integer totalPayments;           // 총 납부 횟수
    private Integer remainingPayments;       // 남은 납부 횟수
    private BigInteger fixedPaymentAmount;   // EPIP 방식의 고정 납부액
    private String repaymentType;            // 상환 방식
    private Boolean overdueFlag;             // 연체 상태
    private BigInteger aoi;                  // 누적 연체 이자 (Accumulated Overdue Interest)
}
