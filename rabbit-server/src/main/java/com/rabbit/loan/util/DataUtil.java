package com.rabbit.loan.util;

import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DataUtil {

    // 이자율 값 변환
    public static double getRateAsDouble(BigInteger rate) {
        return rate.doubleValue() / 100.0;
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

    // 만기수취액 계산
    public static Long calculateTotalAmount(PromissoryNote.PromissoryMetadata promissoryMetadata, RepaymentScheduler.RepaymentInfo repayInfo) {
        int remainTerms = calculateRemainTerms(promissoryMetadata.matDt);
        Long mp = calculateMonthlyPayment(repayInfo);

        return remainTerms * mp;
    }

    // 진행률 계산
    public static double calculateProgressRate(String contractDt, String matDt) {
        LocalDate today = LocalDate.now();
        LocalDate contractDate = LocalDate.parse(contractDt);
        LocalDate maturityDate = LocalDate.parse(matDt);

        long totalDays = ChronoUnit.DAYS.between(contractDate, maturityDate);
        long passedDays = ChronoUnit.DAYS.between(contractDate, today);

        if (totalDays <= 0) return 100.0; // 만기일이 시작일보다 빠르거나 같을 경우
        if (passedDays <= 0) return 0.0;  // 아직 시작 안했으면 진행률 0%

        double progress = ((double) passedDays / totalDays) * 100.0;
        return Math.min(progress, 100.0); // 최대 100%로 제한
    }

    // 다음 상환일 초에서 날짜로 반환
    public static String getNextMpDt(BigInteger nextMpDt) {
        // BigInteger를 long으로 변환
        long nextMpDtSeconds = nextMpDt.longValue();

        // Unix timestamp(초)를 LocalDateTime으로 변환
        LocalDateTime nextPaymentDate = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(nextMpDtSeconds),
                ZoneId.systemDefault()
        );

        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 포맷팅된 날짜 문자열 반환
        return nextPaymentDate.format(formatter);
    }

    // 가장 빠른 상환일
    public static String getFastestNextPaymentDate(List<BigInteger> paymentDates) {
        if (paymentDates == null || paymentDates.isEmpty()) {
            return "납부일 정보가 없습니다";
        }

        // 현재 시간의 Unix timestamp (초)
        long currentTimeSec = Instant.now().getEpochSecond();

        // 미래 납부일만 필터링
        List<BigInteger> futureDates = paymentDates.stream()
                .filter(date -> date.longValue() > currentTimeSec)
                .sorted()  // 오름차순 정렬
                .toList();

        if (futureDates.isEmpty()) {
            return "남은 납부일이 없습니다";
        }

        // 가장 빠른 미래 납부일 (정렬했으므로 첫 번째 요소)
        BigInteger nextDate = futureDates.get(0);

        // 날짜 포맷팅
        LocalDateTime nextDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(nextDate.longValue()),
                ZoneId.systemDefault()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return nextDateTime.format(formatter);
    }
}
