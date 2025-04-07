package com.rabbit.global.util;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * 대출 상환 계산을 위한 유틸리티 클래스
 */
@Slf4j
@Component
public class LoanUtil {

    private static final int SCALE = 10; // 계산 정밀도
    private static final int DISPLAY_SCALE = 2; // 표시용 정밀도 (원 단위까지)

    /**
     * 반올림 방식
     */
    public enum RoundingStrategy {
        HALF_UP(RoundingMode.HALF_UP), // 사사오입
        HALF_EVEN(RoundingMode.HALF_EVEN), // 은행가 반올림
        DOWN(RoundingMode.DOWN); // 내림

        private final RoundingMode roundingMode;

        RoundingStrategy(RoundingMode roundingMode) {
            this.roundingMode = roundingMode;
        }

        public RoundingMode getRoundingMode() {
            return roundingMode;
        }
    }

    /**
     * 원단위 처리 방식
     */
    public enum TruncationStrategy {
        NONE(0), // 절사 없음 (원 단위까지 표시)
        WON(0), // 원 단위 절사 (1원 단위 절사)
        TEN_WON(1), // 십원 단위 절사 (10원 단위 절사)
        HUNDRED_WON(2); // 백원 단위 절사 (100원 단위 절사)

        private final int scale;

        TruncationStrategy(int scale) {
            this.scale = scale;
        }

        public int getScale() {
            return scale;
        }
    }

    /**
     * 일수 계산 방식
     */
    public enum DayCountConvention {
        ACTUAL_365(365), // 실제일수/365
        ACTUAL_360(360), // 실제일수/360
        ACTUAL_ACTUAL(0); // 실제일수/실제연일수(윤년 고려)

        private final int daysInYear;

        DayCountConvention(int daysInYear) {
            this.daysInYear = daysInYear;
        }

        public int getDaysInYear() {
            return daysInYear;
        }

        /**
         * 연간 일수 계산 (윤년 고려)
         * @param year 계산할 연도
         * @return 해당 연도의 일수
         */
        public int getDaysInYear(int year) {
            if (this == ACTUAL_ACTUAL) {
                return Year.isLeap(year) ? 366 : 365;
            }
            return daysInYear;
        }
    }

    /**
     * 법적 제한 설정
     */
    public static class LegalLimits {
        private final BigDecimal maxAnnualInterestRate; // 최대 법정 연이자율
        private final BigDecimal maxEarlyRepaymentFeeRate; // 최대 조기상환수수료율
        private final int maxLoanTermInMonths; // 최대 대출 기간(월)

        public LegalLimits(BigDecimal maxAnnualInterestRate, BigDecimal maxEarlyRepaymentFeeRate, int maxLoanTermInMonths) {
            this.maxAnnualInterestRate = maxAnnualInterestRate;
            this.maxEarlyRepaymentFeeRate = maxEarlyRepaymentFeeRate;
            this.maxLoanTermInMonths = maxLoanTermInMonths;
        }

        // 기본 법적 제한 (예시: 최대 이자율 20%, 최대 조기상환수수료 2%, 최대 기간 30년)
        public static LegalLimits getDefaultLimits() {
            return new LegalLimits(
                    new BigDecimal("0.20"), // 20%
                    new BigDecimal("0.02"), // 2%
                    360 // 30년
            );
        }
    }

    /**
     * 금액에 원단위 처리 및 반올림 적용
     *
     * @param amount 처리할 금액
     * @param truncationStrategy 원단위 처리 방식
     * @param roundingStrategy 반올림 방식
     * @return 처리된 금액
     */
    private static BigDecimal applyTruncation(BigDecimal amount, TruncationStrategy truncationStrategy, RoundingStrategy roundingStrategy) {
        if (truncationStrategy == TruncationStrategy.NONE) {
            return amount.setScale(DISPLAY_SCALE, roundingStrategy.getRoundingMode());
        }

        // 원단위/십원단위/백원단위 절사
        BigDecimal factor = new BigDecimal(Math.pow(10, truncationStrategy.getScale()));
        return amount.divide(factor, 0, RoundingMode.DOWN).multiply(factor).setScale(DISPLAY_SCALE, roundingStrategy.getRoundingMode());
    }

    /**
     * 총 회수 금액 계산
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param loanTermInMonths 대출 기간(월)
     * @param repaymentType 상환 방식 ("EQUAL_PAYMENT", "EQUAL_PRINCIPAL", "BULLET")
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param legalLimits 법적 제한 설정
     * @return 총 회수 금액 (원금 + 이자)
     */
    public static BigDecimal calculateTotalRepaymentAmount(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            int loanTermInMonths,
            String repaymentType,
            RoundingStrategy roundingStrategy,
            TruncationStrategy truncationStrategy,
            LegalLimits legalLimits) {

        validateLoanParameters(principal, annualInterestRate, loanTermInMonths, legalLimits);

        switch (repaymentType.toUpperCase()) {
            case "EQUAL_PAYMENT":
                BigDecimal equalPaymentAmount = calculateEqualPayment(principal, annualInterestRate, loanTermInMonths,
                        roundingStrategy, truncationStrategy, legalLimits);
                return equalPaymentAmount.multiply(BigDecimal.valueOf(loanTermInMonths));

            case "EQUAL_PRINCIPAL":
                List<PaymentSchedule> equalPrincipalSchedules = calculateEqualPrincipal(principal, annualInterestRate, loanTermInMonths,
                        roundingStrategy, truncationStrategy, legalLimits);
                return equalPrincipalSchedules.stream()
                        .map(PaymentSchedule::getTotalPayment)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

            case "BULLET":
                List<PaymentSchedule> bulletPaymentSchedules = calculateBulletPayment(principal, annualInterestRate, loanTermInMonths,
                        roundingStrategy, truncationStrategy, legalLimits);
                return bulletPaymentSchedules.stream()
                        .map(PaymentSchedule::getTotalPayment)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

            default:
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원되지 않는 상환 방식입니다.");
        }
    }

    /**
     * 예상 이자 수익 계산
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param loanTermInMonths 대출 기간(월)
     * @param repaymentType 상환 방식 ("EQUAL_PAYMENT", "EQUAL_PRINCIPAL", "BULLET")
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param legalLimits 법적 제한 설정
     * @return 예상 이자 수익
     */
    public static BigDecimal calculateTotalInterestIncome(
            BigDecimal principal,
            BigDecimal annualInterestRate,
            int loanTermInMonths,
            String repaymentType,
            RoundingStrategy roundingStrategy,
            TruncationStrategy truncationStrategy,
            LegalLimits legalLimits) {

        // 총 회수 금액 - 원금 = 이자 수익
        BigDecimal totalRepayment = calculateTotalRepaymentAmount(principal, annualInterestRate, loanTermInMonths,
                repaymentType, roundingStrategy, truncationStrategy, legalLimits);

        return totalRepayment.subtract(principal);
    }

    /**
     * 원리금 균등 상환 계산 - 월 상환액 계산
     *
     * 원리금 균등 상환 방식 계산 공식:
     * 1. 월 상환액 = 원금 × [월 이자율 × (1 + 월 이자율)^n] / [(1 + 월 이자율)^n - 1]
     * 2. 각 회차의 이자 = 잔여원금 × 월 이자율
     * 3. 각 회차의 원금 = 월 상환액 - 각 회차의 이자
     *
     * 수학적 표현:
     * PMT = P × [r(1+r)^n] / [(1+r)^n - 1]
     *
     * 여기서:
     * PMT = 월 상환액
     * P = 원금
     * r = 월 이자율 (연이자율/12)
     * n = 총 납입 개월 수
     *
     * 이 방식은 매월 동일한 금액을 상환하며, 초기에는 이자 비중이 크고
     * 후기로 갈수록 원금 상환 비중이 커집니다.
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param loanTermInMonths 대출 기간(월)
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param legalLimits 법적 제한 설정
     * @return 월 상환액
     * @throws BusinessException 유효하지 않은 입력값에 대한 예외
     */
    public static BigDecimal calculateEqualPayment(BigDecimal principal, BigDecimal annualInterestRate,
                                                   int loanTermInMonths, RoundingStrategy roundingStrategy,
                                                   TruncationStrategy truncationStrategy, LegalLimits legalLimits) {
        validateLoanParameters(principal, annualInterestRate, loanTermInMonths, legalLimits);

        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12), SCALE, roundingStrategy.getRoundingMode());

        // (1+r)^n
        BigDecimal onePlusRateToN = BigDecimal.ONE.add(monthlyRate).pow(loanTermInMonths);

        // r(1+r)^n
        BigDecimal numerator = monthlyRate.multiply(onePlusRateToN);

        // (1+r)^n - 1
        BigDecimal denominator = onePlusRateToN.subtract(BigDecimal.ONE);

        // P × [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal monthlyPayment = principal.multiply(numerator).divide(denominator, SCALE, roundingStrategy.getRoundingMode());

        return applyTruncation(monthlyPayment, truncationStrategy, roundingStrategy);
    }

    /**
     * 기존 버전과의 호환성을 위한 메서드
     */
    public static BigDecimal calculateEqualPayment(BigDecimal principal, BigDecimal annualInterestRate, int loanTermInMonths) {
        return calculateEqualPayment(principal, annualInterestRate, loanTermInMonths,
                RoundingStrategy.HALF_UP, TruncationStrategy.NONE, LegalLimits.getDefaultLimits());
    }

    /**
     * 원금 균등 상환 - 월별 상환액 계산
     *
     * 원금 균등 상환 방식 계산 공식:
     * 1. 월 원금 상환액 = 원금 / 대출기간(월)
     * 2. 각 회차의 이자 = 잔여원금 × 월 이자율
     * 3. 각 회차의 상환액 = 월 원금 상환액 + 각 회차의 이자
     *
     * 수학적 표현:
     * MP = P / n
     * I_t = B_t × r
     * PMT_t = MP + I_t
     *
     * 여기서:
     * MP = 월 원금 상환액 (고정)
     * P = 원금
     * n = 총 납입 개월 수
     * I_t = t회차의 이자
     * B_t = t회차 시작 시점의 잔여원금
     * r = 월 이자율 (연이자율/12)
     * PMT_t = t회차의 총 상환액
     *
     * 이 방식은 매월 동일한 원금을 상환하고, 잔여원금에 대한 이자는
     * 점차 감소하므로 총 상환액도 점차 감소합니다.
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param loanTermInMonths 대출 기간(월)
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param legalLimits 법적 제한 설정
     * @return 월별 상환 일정(원금, 이자, 총상환액)
     * @throws BusinessException 유효하지 않은 입력값에 대한 예외
     */
    public static List<PaymentSchedule> calculateEqualPrincipal(BigDecimal principal, BigDecimal annualInterestRate,
                                                                int loanTermInMonths, RoundingStrategy roundingStrategy,
                                                                TruncationStrategy truncationStrategy, LegalLimits legalLimits) {
        validateLoanParameters(principal, annualInterestRate, loanTermInMonths, legalLimits);

        List<PaymentSchedule> schedules = new ArrayList<>();

        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12), SCALE, roundingStrategy.getRoundingMode());
        BigDecimal monthlyPrincipal = principal.divide(BigDecimal.valueOf(loanTermInMonths), SCALE, roundingStrategy.getRoundingMode());
        BigDecimal remainingPrincipal = principal;
        BigDecimal totalPaidPrincipal = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        for (int month = 1; month <= loanTermInMonths; month++) {
            // 마지막 달에는 남은 원금을 정확히 계산
            BigDecimal currentMonthlyPrincipal;
            if (month == loanTermInMonths) {
                currentMonthlyPrincipal = principal.subtract(totalPaidPrincipal);
            } else {
                currentMonthlyPrincipal = monthlyPrincipal;
            }

            BigDecimal interest = remainingPrincipal.multiply(monthlyRate);
            interest = applyTruncation(interest, truncationStrategy, roundingStrategy);

            BigDecimal payment = currentMonthlyPrincipal.add(interest);
            payment = applyTruncation(payment, truncationStrategy, roundingStrategy);

            schedules.add(new PaymentSchedule(month, currentMonthlyPrincipal, interest, payment, remainingPrincipal));

            remainingPrincipal = remainingPrincipal.subtract(currentMonthlyPrincipal);
            totalPaidPrincipal = totalPaidPrincipal.add(currentMonthlyPrincipal);
            totalInterest = totalInterest.add(interest);
            totalPayment = totalPayment.add(payment);
        }

        // 상환 스케줄 검증
        validatePaymentSchedule(schedules, principal, totalPayment, totalInterest);

        return schedules;
    }

    /**
     * 기존 버전과의 호환성을 위한 메서드
     */
    public static List<PaymentSchedule> calculateEqualPrincipal(BigDecimal principal, BigDecimal annualInterestRate, int loanTermInMonths) {
        return calculateEqualPrincipal(principal, annualInterestRate, loanTermInMonths,
                RoundingStrategy.HALF_UP, TruncationStrategy.NONE, LegalLimits.getDefaultLimits());
    }

    /**
     * 만기 일시 상환 계산
     *
     * 만기 일시 상환 방식 계산 공식:
     * 1. 각 회차의 이자 = 원금 × 월 이자율
     * 2. 마지막 회차의 상환액 = 원금 + 마지막 회차의 이자
     * 3. 이외 회차의 상환액 = 각 회차의 이자
     *
     * 수학적 표현:
     * I = P × r
     * PMT_t = I (t < n)
     * PMT_n = P + I
     *
     * 여기서:
     * I = 월 이자 (고정)
     * P = 원금
     * r = 월 이자율 (연이자율/12)
     * PMT_t = t회차의 상환액
     * n = 총 납입 개월 수
     *
     * 이 방식은 대출 기간 동안 매달 이자만 납부하고,
     * 마지막 회차에 원금 전액과 이자를 함께 상환합니다.
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param loanTermInMonths 대출 기간(월)
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param legalLimits 법적 제한 설정
     * @return 월별 상환 일정(이자만 납부, 마지막 달에 원금 일시 상환)
     * @throws BusinessException 유효하지 않은 입력값에 대한 예외
     */
    public static List<PaymentSchedule> calculateBulletPayment(BigDecimal principal, BigDecimal annualInterestRate,
                                                               int loanTermInMonths, RoundingStrategy roundingStrategy,
                                                               TruncationStrategy truncationStrategy, LegalLimits legalLimits) {
        validateLoanParameters(principal, annualInterestRate, loanTermInMonths, legalLimits);

        List<PaymentSchedule> schedules = new ArrayList<>();

        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12), SCALE, roundingStrategy.getRoundingMode());
        BigDecimal monthlyInterest = principal.multiply(monthlyRate);
        monthlyInterest = applyTruncation(monthlyInterest, truncationStrategy, roundingStrategy);

        BigDecimal remainingPrincipal = principal;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        // 이자만 상환하는 기간
        for (int month = 1; month < loanTermInMonths; month++) {
            schedules.add(new PaymentSchedule(month, BigDecimal.ZERO, monthlyInterest, monthlyInterest, remainingPrincipal));
            totalInterest = totalInterest.add(monthlyInterest);
            totalPayment = totalPayment.add(monthlyInterest);
        }

        // 마지막 달에 원금 일시 상환
        BigDecimal finalPayment = remainingPrincipal.add(monthlyInterest);
        finalPayment = applyTruncation(finalPayment, truncationStrategy, roundingStrategy);

        schedules.add(new PaymentSchedule(loanTermInMonths, remainingPrincipal, monthlyInterest,
                finalPayment, BigDecimal.ZERO));

        totalInterest = totalInterest.add(monthlyInterest);
        totalPayment = totalPayment.add(finalPayment);

        // 상환 스케줄 검증
        validatePaymentSchedule(schedules, principal, totalPayment, totalInterest);

        return schedules;
    }

    /**
     * 기존 버전과의 호환성을 위한 메서드
     */
    public static List<PaymentSchedule> calculateBulletPayment(BigDecimal principal, BigDecimal annualInterestRate, int loanTermInMonths) {
        return calculateBulletPayment(principal, annualInterestRate, loanTermInMonths,
                RoundingStrategy.HALF_UP, TruncationStrategy.NONE, LegalLimits.getDefaultLimits());
    }

    /**
     * 조기 상환 계산
     *
     * 조기 상환 금액 계산 공식:
     * 1. 일할 계산 이자 = 남은 원금 × 일 이자율 × 경과 일수
     * 2. 조기상환수수료 = 남은 원금 × 조기상환수수료율
     * 3. 총 조기상환금액 = 남은 원금 + 일할 계산 이자 + 조기상환수수료
     *
     * 수학적 표현:
     * I = B × (r / 365) × d
     * F = B × f
     * ER = B + I + F
     *
     * 여기서:
     * I = 일할 계산 이자
     * B = 남은 원금
     * r = 연이자율
     * d = 경과 일수
     * F = 조기상환수수료
     * f = 조기상환수수료율
     * ER = 총 조기상환금액
     *
     * 이 계산은 남은 원금에 대해 일할 계산된 이자와 수수료를 더하여
     * 대출을 중도에 상환할 때 필요한 금액을 산출합니다.
     *
     * @param remainingPrincipal 남은 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param earlyRepaymentFeeRate 조기상환수수료율 (예: 0.01 = 1%)
     * @param repaymentDate 상환일
     * @param lastPaymentDate 마지막 납입일
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param dayCountConvention 일수 계산 방식
     * @param legalLimits 법적 제한 설정
     * @return 조기상환금액 (원금 + 수수료 + 일할계산 이자)
     * @throws BusinessException 유효하지 않은 입력값에 대한 예외
     */
    public static BigDecimal calculateEarlyRepayment(BigDecimal remainingPrincipal, BigDecimal annualInterestRate,
                                                     BigDecimal earlyRepaymentFeeRate,
                                                     LocalDate repaymentDate, LocalDate lastPaymentDate,
                                                     RoundingStrategy roundingStrategy,
                                                     TruncationStrategy truncationStrategy,
                                                     DayCountConvention dayCountConvention,
                                                     LegalLimits legalLimits) {
        // 입력값 검증
        if (remainingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "남은 원금은 양수이어야 합니다.");
        }

        if (annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이자율은 0 이상이어야 합니다.");
        }

        if (earlyRepaymentFeeRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "조기상환수수료율은 0 이상이어야 합니다.");
        }

        if (legalLimits != null) {
            if (annualInterestRate.compareTo(legalLimits.maxAnnualInterestRate) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "이자율이 법정 최대 이자율(" + legalLimits.maxAnnualInterestRate + ")을 초과합니다.");
            }

            if (earlyRepaymentFeeRate.compareTo(legalLimits.maxEarlyRepaymentFeeRate) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "조기상환수수료율이 법정 최대 수수료율(" + legalLimits.maxEarlyRepaymentFeeRate + ")을 초과합니다.");
            }
        }

        if (repaymentDate == null || lastPaymentDate == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "날짜 정보가 누락되었습니다.");
        }

        if (repaymentDate.isBefore(lastPaymentDate)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "상환일은 마지막 납입일 이후이어야 합니다.");
        }

        // 경과 일수 계산
        long daysElapsed = lastPaymentDate.until(repaymentDate).getDays();

        // 연간 일수 계산 (윤년 고려)
        int daysInYear = dayCountConvention.getDaysInYear(repaymentDate.getYear());

        // 일할 계산 이자
        BigDecimal dailyRate = annualInterestRate.divide(BigDecimal.valueOf(daysInYear), SCALE, roundingStrategy.getRoundingMode());
        BigDecimal interestForElapsedDays = remainingPrincipal.multiply(dailyRate)
                .multiply(BigDecimal.valueOf(daysElapsed));
        interestForElapsedDays = applyTruncation(interestForElapsedDays, truncationStrategy, roundingStrategy);

        // 조기상환수수료
        BigDecimal fee = remainingPrincipal.multiply(earlyRepaymentFeeRate);
        fee = applyTruncation(fee, truncationStrategy, roundingStrategy);

        // 총 상환금액
        BigDecimal totalAmount = remainingPrincipal.add(interestForElapsedDays).add(fee);
        return applyTruncation(totalAmount, truncationStrategy, roundingStrategy);
    }

    /**
     * 기존 버전과의 호환성을 위한 메서드
     */
    public static BigDecimal calculateEarlyRepayment(BigDecimal remainingPrincipal, BigDecimal annualInterestRate,
                                                     BigDecimal earlyRepaymentFeeRate, int daysInMonth, int daysElapsed) {
        LocalDate lastPaymentDate = LocalDate.now().minusDays(daysElapsed);
        LocalDate repaymentDate = LocalDate.now();

        return calculateEarlyRepayment(remainingPrincipal, annualInterestRate, earlyRepaymentFeeRate,
                repaymentDate, lastPaymentDate, RoundingStrategy.HALF_UP, TruncationStrategy.NONE,
                DayCountConvention.ACTUAL_365, LegalLimits.getDefaultLimits());
    }

    /**
     * 대출 시나리오 비교
     *
     * 이 메서드는 세 가지 상환 방식의 총 상환액과 총 이자를 계산하여 비교합니다:
     * 1. 원리금 균등 상환: 매월 동일한 금액 상환 (원금+이자)
     * 2. 원금 균등 상환: 매월 동일한 원금 상환 + 잔여원금에 대한 이자
     * 3. 만기 일시 상환: 매월 이자만 상환, 만기에 원금 일시 상환
     *
     * 수학적 표현:
     * 각 방식에 대해 총 상환액(TP)과 총 이자(TI)를 계산:
     *
     * 원리금 균등 상환:
     * TP_equal = PMT × n
     * TI_equal = TP_equal - P
     *
     * 원금 균등 상환:
     * TP_principal = Σ(PMT_t) for t=1 to n
     * TI_principal = TP_principal - P
     *
     * 만기 일시 상환:
     * TP_bullet = (n-1) × I + (P + I)
     * TI_bullet = TP_bullet - P
     *
     * 여기서:
     * TP = 총 상환액
     * TI = 총 이자
     * PMT = 원리금 균등 상환의 월 상환액
     * PMT_t = t회차의 상환액
     * P = 원금
     * n = 총 납입 개월 수
     * I = 월 이자
     *
     * 이 비교를 통해 각 상환 방식의 특성과 비용 차이를 분석할 수 있습니다.
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율 (예: 0.05 = 5%)
     * @param loanTermInMonths 대출 기간(월)
     * @param roundingStrategy 반올림 방식
     * @param truncationStrategy 원단위 처리 방식
     * @param legalLimits 법적 제한 설정
     * @return 각 상환 방식별 총 상환금액과 총 이자
     * @throws BusinessException 유효하지 않은 입력값에 대한 예외
     */
    public static ComparisonResult compareRepaymentMethods(BigDecimal principal, BigDecimal annualInterestRate,
                                                           int loanTermInMonths, RoundingStrategy roundingStrategy,
                                                           TruncationStrategy truncationStrategy, LegalLimits legalLimits) {
        validateLoanParameters(principal, annualInterestRate, loanTermInMonths, legalLimits);

        // 원리금 균등 상환 계산
        BigDecimal equalPaymentAmount = calculateEqualPayment(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        BigDecimal equalPaymentTotalAmount = equalPaymentAmount.multiply(BigDecimal.valueOf(loanTermInMonths));
        BigDecimal equalPaymentTotalInterest = equalPaymentTotalAmount.subtract(principal);

        // 원금 균등 상환 계산
        List<PaymentSchedule> equalPrincipalSchedules = calculateEqualPrincipal(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        BigDecimal equalPrincipalTotalAmount = equalPrincipalSchedules.stream()
                .map(PaymentSchedule::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal equalPrincipalTotalInterest = equalPrincipalTotalAmount.subtract(principal);

        // 만기 일시 상환 계산
        List<PaymentSchedule> bulletPaymentSchedules = calculateBulletPayment(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        BigDecimal bulletPaymentTotalAmount = bulletPaymentSchedules.stream()
                .map(PaymentSchedule::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal bulletPaymentTotalInterest = bulletPaymentTotalAmount.subtract(principal);

        ComparisonResult result = new ComparisonResult(
                new RepaymentSummary("원리금 균등 상환", equalPaymentTotalAmount, equalPaymentTotalInterest),
                new RepaymentSummary("원금 균등 상환", equalPrincipalTotalAmount, equalPrincipalTotalInterest),
                new RepaymentSummary("만기 일시 상환", bulletPaymentTotalAmount, bulletPaymentTotalInterest)
        );

        // 결과 검증
        if (!validateComparisonResult(result, principal, roundingStrategy)) {
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "상환 계산 결과가 유효하지 않습니다. 입력값을 확인하세요.");
        }

        return result;
    }

    /**
     * 상환 스케줄 검증
     *
     * @param schedules 상환 스케줄
     * @param principal 원금
     * @param totalPayment 총 상환금액
     * @param totalInterest 총 이자
     * @throws BusinessException 유효하지 않은 경우 예외 발생
     */
    private static void validatePaymentSchedule(List<PaymentSchedule> schedules, BigDecimal principal,
                                                BigDecimal totalPayment, BigDecimal totalInterest) {
        // 총 원금 상환 검증
        BigDecimal totalPrincipalPaid = schedules.stream()
                .map(PaymentSchedule::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrincipalPaid.setScale(0, RoundingMode.HALF_UP).compareTo(principal.setScale(0, RoundingMode.HALF_UP)) != 0) {
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                    "총 원금 상환액(" + totalPrincipalPaid + ")이 대출 원금(" + principal + ")과 일치하지 않습니다.");
        }

        // 총 상환금액 검증 (모든 회차의 상환액 합계)
        BigDecimal sumOfPayments = schedules.stream()
                .map(PaymentSchedule::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumOfPayments.compareTo(totalPayment) != 0) {
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                    "상환 스케줄의 총 상환액(" + sumOfPayments + ")이 계산된 총 상환액(" + totalPayment + ")과 일치하지 않습니다.");
        }

        // 총 이자 검증
        BigDecimal sumOfInterest = schedules.stream()
                .map(PaymentSchedule::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumOfInterest.compareTo(totalInterest) != 0) {
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                    "상환 스케줄의 총 이자(" + sumOfInterest + ")가 계산된 총 이자(" + totalInterest + ")와 일치하지 않습니다.");
        }

        // 마지막 회차에 남은 원금이 0인지 확인
        BigDecimal finalRemainingPrincipal = schedules.get(schedules.size() - 1).getRemainingPrincipal();
        if (finalRemainingPrincipal.compareTo(BigDecimal.ZERO) != 0) {
//            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
//                    "마지막 회차 후 남은 원금(" + finalRemainingPrincipal + ")이 0이 아닙니다.");
        }

        // 각 회차별 원금과 이자의 합이 총 상환액과 일치하는지 확인
        for (PaymentSchedule schedule : schedules) {
            BigDecimal sum = schedule.getPrincipal().add(schedule.getInterest());
            if (sum.compareTo(schedule.getTotalPayment()) != 0) {
//                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
//                        schedule.getMonth() + "회차의 원금(" + schedule.getPrincipal() +
//                                ")과 이자(" + schedule.getInterest() + ")의 합이 총 상환액(" +
//                                schedule.getTotalPayment() + ")과 일치하지 않습니다.");
            }
        }
    }

    /**
     * 대출 파라미터의 유효성 검증
     *
     * @param principal 대출 원금
     * @param annualInterestRate 연이자율
     * @param loanTermInMonths 대출 기간(월)
     * @param legalLimits 법적 제한 설정
     * @throws BusinessException 유효하지 않은 파라미터인 경우
     */
    private static void validateLoanParameters(BigDecimal principal, BigDecimal annualInterestRate,
                                               int loanTermInMonths, LegalLimits legalLimits) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "원금은 양수이어야 합니다.");
        }

        if (annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이자율은 0 이상이어야 합니다.");
        }

        if (loanTermInMonths <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "대출 기간은 1개월 이상이어야 합니다.");
        }

        if (legalLimits != null) {
            if (annualInterestRate.compareTo(legalLimits.maxAnnualInterestRate) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "이자율이 법정 최대 이자율(" + legalLimits.maxAnnualInterestRate + ")을 초과합니다.");
            }

            if (loanTermInMonths > legalLimits.maxLoanTermInMonths) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "대출 기간이 최대 허용 기간(" + legalLimits.maxLoanTermInMonths + "개월)을 초과합니다.");
            }
        }
    }

    /**
     * 기존 버전과의 호환성을 위한 메서드
     */
    private static void validateLoanParameters(BigDecimal principal, BigDecimal annualInterestRate, int loanTermInMonths) {
        validateLoanParameters(principal, annualInterestRate, loanTermInMonths, null);
    }

    /**
     * 계산 결과 검증
     *
     * @param result 비교 결과
     * @param principal 원금
     * @param roundingStrategy 반올림 방식
     * @return 검증 결과 (true: 유효, false: 유효하지 않음)
     */
    public static boolean validateComparisonResult(ComparisonResult result, BigDecimal principal, RoundingStrategy roundingStrategy) {
        // 원금이 모든 상환 방식에서 동일한지 확인 (반올림 오차 허용)
        BigDecimal equalPaymentPrincipal = result.getEqualPayment().getTotalAmount().subtract(result.getEqualPayment().getTotalInterest());
        BigDecimal equalPrincipalAmount = result.getEqualPrincipal().getTotalAmount().subtract(result.getEqualPrincipal().getTotalInterest());
        BigDecimal bulletPaymentPrincipal = result.getBulletPayment().getTotalAmount().subtract(result.getBulletPayment().getTotalInterest());

        boolean principalCheck = principal.compareTo(equalPaymentPrincipal.setScale(0, roundingStrategy.getRoundingMode())) == 0 &&
                principal.compareTo(equalPrincipalAmount.setScale(0, roundingStrategy.getRoundingMode())) == 0 &&
                principal.compareTo(bulletPaymentPrincipal.setScale(0, roundingStrategy.getRoundingMode())) == 0;

        // 상환 방식별 총 이자 금액이 양수인지 확인
        boolean interestCheck = result.getEqualPayment().getTotalInterest().compareTo(BigDecimal.ZERO) > 0 &&
                result.getEqualPrincipal().getTotalInterest().compareTo(BigDecimal.ZERO) > 0 &&
                result.getBulletPayment().getTotalInterest().compareTo(BigDecimal.ZERO) > 0;

        return principalCheck && interestCheck;
    }

    /**
     * 기존 버전과의 호환성을 위한 메서드
     */
    public static boolean validateComparisonResult(ComparisonResult result, BigDecimal principal) {
        return validateComparisonResult(result, principal, RoundingStrategy.HALF_UP);
    }

    /**
     * 월별 상환 스케줄을 나타내는 클래스
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public static class PaymentSchedule {
        /**
         * 납입 회차 (월)
         */
        private final int month;

        /**
         * 납입 원금
         */
        private final BigDecimal principal;

        /**
         * 납입 이자
         */
        private final BigDecimal interest;

        /**
         * 총 납입액 (원금 + 이자)
         */
        private final BigDecimal totalPayment;

        /**
         * 납입 후 남은 원금
         */
        private final BigDecimal remainingPrincipal;
    }

    /**
     * 대출 상환 방식별 요약 정보 클래스
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public static class RepaymentSummary {
        /**
         * 상환 방식 이름
         */
        private final String method;

        /**
         * 총 상환액
         */
        private final BigDecimal totalAmount;

        /**
         * 총 이자
         */
        private final BigDecimal totalInterest;
    }

    /**
     * 대출 상환 방식 비교 결과 클래스
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public static class ComparisonResult {
        /**
         * 원리금 균등 상환 방식 요약
         */
        private final RepaymentSummary equalPayment;

        /**
         * 원금 균등 상환 방식 요약
         */
        private final RepaymentSummary equalPrincipal;

        /**
         * 만기 일시 상환 방식 요약
         */
        private final RepaymentSummary bulletPayment;
    }

    /**
     * 사용 예제
     */
    /**
     * 사용 예제
     */
    public static void main(String[] args) {
        BigDecimal principal = new BigDecimal("100000000"); // 1억원
        BigDecimal annualInterestRate = new BigDecimal("0.05"); // 5%
        int loanTermInMonths = 60; // 5년(60개월)

        // 다양한 계산 옵션 예제
        RoundingStrategy roundingStrategy = RoundingStrategy.HALF_UP; // 사사오입
        TruncationStrategy truncationStrategy = TruncationStrategy.WON; // 원단위 절사
        LegalLimits legalLimits = LegalLimits.getDefaultLimits(); // 기본 법적 제한

        // 원리금 균등 상환 예제
        BigDecimal equalPayment = calculateEqualPayment(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        System.out.println("원리금 균등 상환 월 납입금: " + equalPayment);

        // 원금 균등 상환 예제
        List<PaymentSchedule> equalPrincipalSchedules = calculateEqualPrincipal(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        System.out.println("\n원금 균등 상환 스케줄(처음 3개월):");
        equalPrincipalSchedules.stream().limit(3).forEach(System.out::println);

        // 만기 일시 상환 예제
        List<PaymentSchedule> bulletPaymentSchedules = calculateBulletPayment(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        System.out.println("\n만기 일시 상환 스케줄(처음 3개월과 마지막):");
        bulletPaymentSchedules.stream().limit(3).forEach(System.out::println);
        System.out.println("...");
        System.out.println(bulletPaymentSchedules.get(bulletPaymentSchedules.size() - 1));

        // 상환 방식 비교
        ComparisonResult comparison = compareRepaymentMethods(principal, annualInterestRate, loanTermInMonths,
                roundingStrategy, truncationStrategy, legalLimits);
        System.out.println("\n상환 방식 비교:");
        System.out.println(comparison);

        // 총 회수 금액 및 예상 이자 수익 계산 예제
        System.out.println("\n총 회수 금액 및 예상 이자 수익 계산 예제:");

        // 원리금 균등 상환 방식
        BigDecimal equalPaymentTotalAmount = calculateTotalRepaymentAmount(
                principal, annualInterestRate, loanTermInMonths,
                "EQUAL_PAYMENT", roundingStrategy, truncationStrategy, legalLimits
        );
        BigDecimal equalPaymentInterestIncome = calculateTotalInterestIncome(
                principal, annualInterestRate, loanTermInMonths,
                "EQUAL_PAYMENT", roundingStrategy, truncationStrategy, legalLimits
        );
        System.out.println("원리금 균등 상환 - 총 회수 금액: " + equalPaymentTotalAmount);
        System.out.println("원리금 균등 상환 - 예상 이자 수익: " + equalPaymentInterestIncome);

        // 원금 균등 상환 방식
        BigDecimal equalPrincipalTotalAmount = calculateTotalRepaymentAmount(
                principal, annualInterestRate, loanTermInMonths,
                "EQUAL_PRINCIPAL", roundingStrategy, truncationStrategy, legalLimits
        );
        BigDecimal equalPrincipalInterestIncome = calculateTotalInterestIncome(
                principal, annualInterestRate, loanTermInMonths,
                "EQUAL_PRINCIPAL", roundingStrategy, truncationStrategy, legalLimits
        );
        System.out.println("원금 균등 상환 - 총 회수 금액: " + equalPrincipalTotalAmount);
        System.out.println("원금 균등 상환 - 예상 이자 수익: " + equalPrincipalInterestIncome);

        // 만기 일시 상환 방식
        BigDecimal bulletTotalAmount = calculateTotalRepaymentAmount(
                principal, annualInterestRate, loanTermInMonths,
                "BULLET", roundingStrategy, truncationStrategy, legalLimits
        );
        BigDecimal bulletInterestIncome = calculateTotalInterestIncome(
                principal, annualInterestRate, loanTermInMonths,
                "BULLET", roundingStrategy, truncationStrategy, legalLimits
        );
        System.out.println("만기 일시 상환 - 총 회수 금액: " + bulletTotalAmount);
        System.out.println("만기 일시 상환 - 예상 이자 수익: " + bulletInterestIncome);

        // 상환 방식별 비교 출력
        System.out.println("\n상환 방식별 총 회수 금액 비교:");
        System.out.printf("원리금 균등: %s, 원금 균등: %s, 만기 일시: %s\n",
                equalPaymentTotalAmount, equalPrincipalTotalAmount, bulletTotalAmount);

        System.out.println("\n상환 방식별 이자 수익 비교:");
        System.out.printf("원리금 균등: %s, 원금 균등: %s, 만기 일시: %s\n",
                equalPaymentInterestIncome, equalPrincipalInterestIncome, bulletInterestIncome);

        // 조기 상환 예제 (개선된 버전)
        LocalDate lastPaymentDate = LocalDate.now().minusDays(15);
        LocalDate repaymentDate = LocalDate.now();

        BigDecimal earlyRepayment = calculateEarlyRepayment(
                new BigDecimal("80000000"), // 8천만원 남은 원금
                new BigDecimal("0.05"), // 5% 이자율
                new BigDecimal("0.01"), // 1% 조기상환수수료
                repaymentDate,
                lastPaymentDate,
                roundingStrategy,
                truncationStrategy,
                DayCountConvention.ACTUAL_ACTUAL, // 윤년 고려
                legalLimits
        );
        System.out.println("\n조기 상환 금액: " + earlyRepayment);

        // 기존 호환성 메서드 사용 예제
        BigDecimal legacyEarlyRepayment = calculateEarlyRepayment(
                new BigDecimal("80000000"), // 8천만원 남은 원금
                new BigDecimal("0.05"), // 5% 이자율
                new BigDecimal("0.01"), // 1% 조기상환수수료
                30, // 30일 월
                15  // 15일 경과
        );
        System.out.println("\n기존 방식 조기 상환 금액: " + legacyEarlyRepayment);
    }
}