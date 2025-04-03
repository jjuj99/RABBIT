package com.rabbit.loan.util;

import com.rabbit.blockchain.domain.dto.response.RepaymentInfoDTO;
import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LoanCalculationUtil {

    /**
     * 단일 차용증의 이번 달 상환액을 계산합니다.
     */
    public static BigInteger calculateMonthlyPayment(RepaymentInfoDTO repayInfo) {
        // 기본 상환액 계산 (상환 방식에 따라 다름)
        BigInteger basePayment;

        // 상환 방식에 따른 계산
        if ("EPIP".equals(repayInfo.getRepaymentType())) {
            // 원리금균등상환 - 고정 납부액
            basePayment = repayInfo.getFixedPaymentAmount();
        }
        else if ("EPP".equals(repayInfo.getRepaymentType())) {
            // 원금균등상환 - 원금 분할 + 이자

            // 원금 분할액 = 초기원금 / 총납부횟수
            BigInteger principalPayment = repayInfo.getInitialPrincipal()
                    .divide(BigInteger.valueOf(repayInfo.getTotalPayments()));

            // 이자 = 남은원금 * 연이자율 / 12 / 10000
            BigInteger interestPayment = repayInfo.getRemainingPrincipal()
                    .multiply(repayInfo.getInterestRate())
                    .divide(BigInteger.valueOf(12 * 10000));

            basePayment = principalPayment.add(interestPayment);
        }
        else {
            // 만기일시상환(BP)
            if (repayInfo.getRemainingPayments() == 1) {
                // 마지막 납부 차례 - 원금 + 이자
                basePayment = repayInfo.getRemainingPrincipal().add(
                        repayInfo.getRemainingPrincipal()
                                .multiply(repayInfo.getInterestRate())
                                .divide(BigInteger.valueOf(12 * 10000))
                );
            } else {
                // 그 외 - 이자만 납부
                basePayment = repayInfo.getRemainingPrincipal()
                        .multiply(repayInfo.getInterestRate())
                        .divide(BigInteger.valueOf(12 * 10000));
            }
        }

        // 연체 상태인 경우, 누적된 연체 이자를 추가
        if (repayInfo.getOverdueFlag()) {
            basePayment = basePayment.add(repayInfo.getAoi());
        }

        return basePayment;
    }
}
