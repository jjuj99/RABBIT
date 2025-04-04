package com.rabbit.loan.util;

import com.rabbit.blockchain.wrapper.RepaymentScheduler;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DataUtil {

    // 이자율 값 변환
    public static double getIrAsDouble(BigInteger ir) {
        return ir.divide(BigInteger.valueOf(100)).doubleValue();
    }

    // 차용증 상태, 정상 / 연체 중 반환
    public static String getStatusString(boolean overdueFlag) {
        return overdueFlag ? "연체 중" : "정상";
    }

    // 만기일까지 남은 일수를 계산
    public static int calculateRemainTerms(String matDt) {
        if (matDt == null || matDt.isBlank()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate maturityDate = LocalDate.parse(matDt); // ISO 8601 형식 (yyyy-MM-dd)

        return (int) ChronoUnit.DAYS.between(today, maturityDate);
    }

    // 차용증의 이번달 상환액을 계산
    public static Long calculateMonthlyPayment(RepaymentScheduler.RepaymentInfo repayInfo) {
        // 기본 상환액 계산 (상환 방식에 따라 다름)
        BigInteger basePayment;

        // 상환 방식에 따른 계산
        if ("EPIP".equals(repayInfo.repayType)) {
            // 원리금균등상환 - 고정 납부액
            basePayment = repayInfo.fixedPaymentAmount;
        } else if ("EPP".equals(repayInfo.repayType)) {
            // 원금균등상환 - 원금 분할 + 이자

            // 원금 분할액 = 초기원금 / 총납부횟수
            BigInteger principalPayment = repayInfo.initialPrincipal
                    .divide(repayInfo.totalPayments);

            // 이자 = 남은원금 * 연이자율 / 12 / 10000
            BigInteger interestPayment = repayInfo.remainingPrincipal
                    .multiply(repayInfo.ir)
                    .divide(BigInteger.valueOf(12 * 10000));

            basePayment = principalPayment.add(interestPayment);
        } else {
            // 만기일시상환(BP)
            if (repayInfo.remainingPayments.equals(BigInteger.ONE)) {
                // 마지막 납부 차례 - 원금 + 이자
                BigInteger interest = repayInfo.initialPrincipal
                        .multiply(repayInfo.ir)
                        .divide(BigInteger.valueOf(12 * 10000));
                basePayment = repayInfo.initialPrincipal.add(interest);
            } else {
                // 그 외 - 이자만 납부
                basePayment = repayInfo.remainingPrincipal
                        .multiply(repayInfo.ir)
                        .divide(BigInteger.valueOf(12 * 10000));
            }
        }

        return basePayment.longValue();
    }
}
