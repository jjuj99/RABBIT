package com.rabbit.blockchain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class RepaymentInfo {
    public final BigInteger tokenId;
    public final BigInteger initialPrincipal;
    public final BigInteger remainingPrincipal;
    public final BigInteger ir;
    public final BigInteger dir;
    public final BigInteger mpDt;
    public final BigInteger nextMpDt;
    public final BigInteger totalPayments;
    public final BigInteger remainingPayments;
    public final BigInteger fixedPaymentAmount;
    public final String repayType;
    public final String drWalletAddress;
    public final Boolean activeFlag;
    public final Boolean overdueFlag;
    public final BigInteger overdueStartDate;
    public final BigInteger overdueDays;
    public final BigInteger aoi;
    public final BigInteger defCnt;
    public final BigInteger accel;
    public final BigInteger currentIr;
    public final BigInteger totalDefCnt;
}
