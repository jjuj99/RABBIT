package com.rabbit.loan.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    /**
     * 만기일까지 남은 일수를 계산한다.
     * @param matDt 만기일 (yyyy-MM-dd 형식의 문자열)
     * @return 오늘 기준으로 만기일까지 남은 일수 (음수일 경우 이미 만기 지남)
     */
    public static int calculateRemainTerms(String matDt) {
        if (matDt == null || matDt.isBlank()) return 0;

        LocalDate today = LocalDate.now();
        LocalDate maturityDate = LocalDate.parse(matDt); // ISO 8601 형식 (yyyy-MM-dd)

        return (int) ChronoUnit.DAYS.between(today, maturityDate);
    }
}
