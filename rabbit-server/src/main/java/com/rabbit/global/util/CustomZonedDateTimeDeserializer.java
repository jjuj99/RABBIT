package com.rabbit.global.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CustomZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
    private static final ZoneId ZONE_ID;

    static {
        String timeZone = System.getProperty("jackson.time-zone", "Asia/Seoul");
        ZONE_ID = ZoneId.of(timeZone);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateTimeString = p.getText();
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }

        try {
            // 1. 전체 형식(날짜+시간) 시도
            if (dateTimeString.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                return ZonedDateTime.parse(
                        dateTimeString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZONE_ID)
                );
            }
            // 2. 날짜만 있는 경우
            else if (dateTimeString.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return ZonedDateTime.parse(
                        dateTimeString + " 00:00:00",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZONE_ID)
                );
            }
            // 3. ISO-8601 형식 시도 (ZonedDateTime의 기본 형식)
            else {
                return ZonedDateTime.parse(dateTimeString);
            }
        } catch (Exception e) {
            throw new IOException("날짜 형식이 올바르지 않습니다: " + dateTimeString, e);
        }
    }
}