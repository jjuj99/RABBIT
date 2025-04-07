package com.rabbit.loan.util;

import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.global.util.LoanUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

    // 연체 반환 타입
    public static String getRepayTypeString(String type) {
        if (type.equals("EPIP")) return "원리금 균등 상환";
        else if (type.equals("EPP")) return "원금 균등 상환";
        else return "만기 일시 상환";
    }

    // 만기일까지 남은 일수를 계산
    public static int calculateRemainTerms(String matDt) {
        if (matDt == null || matDt.isBlank()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate maturityDate = LocalDate.parse(matDt); // ISO 8601 형식 (yyyy-MM-dd)

        long days = ChronoUnit.DAYS.between(today, maturityDate);
        return (int) Math.max(0, days); // 음수 방지
    }

    public static BigDecimal parseInterestRate(BigInteger ir) {
        return new BigDecimal(ir).divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP);
    }

    // 차용증의 이번달 상환액을 계산
    public static Long calculateMonthlyPayment(RepaymentScheduler.RepaymentInfo repayInfo) {
        // 남은 원금 & 남은 납부 횟수
        BigDecimal remainingPrincipal = new BigDecimal(repayInfo.remainingPrincipal);
        int remainingMonths = repayInfo.remainingPayments.intValue();
        BigDecimal annualRate = parseInterestRate(repayInfo.ir); // ir은 10000 = 1%

        LoanUtil.RoundingStrategy roundingStrategy = LoanUtil.RoundingStrategy.HALF_UP;
        LoanUtil.TruncationStrategy truncationStrategy = LoanUtil.TruncationStrategy.WON;
        LoanUtil.LegalLimits legalLimits = LoanUtil.LegalLimits.getDefaultLimits();

        if ("EPIP".equalsIgnoreCase(repayInfo.repayType)) {
            // 원리금 균등 상환
            BigDecimal monthlyPayment = LoanUtil.calculateEqualPayment(
                    remainingPrincipal, annualRate, remainingMonths,
                    roundingStrategy, truncationStrategy, legalLimits
            );
            return monthlyPayment.setScale(0, RoundingMode.DOWN).longValue();

        } else if ("EPP".equalsIgnoreCase(repayInfo.repayType)) {
            // 원금 균등 상환
            List<LoanUtil.PaymentSchedule> schedules = LoanUtil.calculateEqualPrincipal(
                    remainingPrincipal, annualRate, remainingMonths,
                    roundingStrategy, truncationStrategy, legalLimits
            );

            // 다음 회차가 첫 번째인 경우
            if (!schedules.isEmpty()) {
                return schedules.get(0).getTotalPayment().setScale(0, RoundingMode.DOWN).longValue();
            } else {
                return 0L;
            }

        } else if ("BP".equalsIgnoreCase(repayInfo.repayType)) {
            // 만기일시상환
            List<LoanUtil.PaymentSchedule> schedules = LoanUtil.calculateBulletPayment(
                    remainingPrincipal, annualRate, remainingMonths,
                    roundingStrategy, truncationStrategy, legalLimits
            );

            if (!schedules.isEmpty()) {
                return schedules.get(0).getTotalPayment().setScale(0, RoundingMode.DOWN).longValue();
            } else {
                return 0L;
            }
        }

        throw new IllegalArgumentException("지원되지 않는 상환 방식: " + repayInfo.repayType);
    }

    public static Long calculateTotalAmount(RepaymentScheduler.RepaymentInfo repayInfo) {
        // 남은 원금 & 남은 납부 횟수
        BigDecimal remainingPrincipal = new BigDecimal(repayInfo.remainingPrincipal);
        int remainingMonths = repayInfo.remainingPayments.intValue();
        BigDecimal annualRate = new BigDecimal(repayInfo.ir).divide(BigDecimal.valueOf(10000), 10, RoundingMode.HALF_UP); // ir = 10000 → 1%

        String repaymentType;
        switch (repayInfo.repayType) {
            case "EPIP": repaymentType = "EQUAL_PAYMENT"; break;
            case "EPP":  repaymentType = "EQUAL_PRINCIPAL"; break;
            case "BP":   repaymentType = "BULLET"; break;
            default: throw new IllegalArgumentException("지원되지 않는 상환 방식: " + repayInfo.repayType);
        }

        // 옵션 설정
        LoanUtil.RoundingStrategy roundingStrategy = LoanUtil.RoundingStrategy.HALF_UP;
        LoanUtil.TruncationStrategy truncationStrategy = LoanUtil.TruncationStrategy.WON;
        LoanUtil.LegalLimits legalLimits = LoanUtil.LegalLimits.getDefaultLimits();

        BigDecimal totalRepayment = LoanUtil.calculateTotalRepaymentAmount(
                remainingPrincipal,
                annualRate,
                remainingMonths,
                repaymentType,
                roundingStrategy,
                truncationStrategy,
                legalLimits
        );

        return totalRepayment.setScale(0, RoundingMode.DOWN).longValue(); // 원단위 정리
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
        double roundedProgress = new BigDecimal(progress)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return Math.min(roundedProgress, 100.0); // 최대 100%로 제한
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
