package com.rabbit.global.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeUtils {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public static ZonedDateTime toZonedDateTimeAtEndOfDay(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        try {
            // 형식이 yyyy-MM-dd일 경우
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return ZonedDateTime.of(
                        LocalDate.parse(dateStr),
                        LocalTime.of(23, 59, 59),
                        ZONE_ID
                );
            }

            // ZonedDateTime 형식일 경우 그대로 파싱
            return ZonedDateTime.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("날짜 변환 실패: " + dateStr, e);
        }
    }
}
