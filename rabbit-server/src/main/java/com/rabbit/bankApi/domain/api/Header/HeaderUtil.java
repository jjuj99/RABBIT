package com.rabbit.bankApi.domain.api.Header;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class HeaderUtil {

    public static final String SEQUENCE_NUMBER = "009982";
    public static final String INSTITUTION_CODE = "00100";
    public static final String FINTECH_APP_NO = "001";

    public static ApiRequestHeader createHeader(String apiName, String apiKey) {
        return baseHeaderBuilder()
                .apiName(apiName)
                .apiServiceCode(apiName)
                .apiKey(apiKey)
                .build();
    }

    public static ApiRequestHeader createHeader(String apiName, String apiKey, String userKey) {
        return baseHeaderBuilder()
                .apiName(apiName)
                .apiServiceCode(apiName)
                .apiKey(apiKey)
                .userKey(userKey)
                .build();
    }

    public static ApiRequestHeader.ApiRequestHeaderBuilder baseHeaderBuilder() {
        LocalDateTime now = LocalDateTime.now();

        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String uniqueNo = transmissionDate + transmissionTime + SEQUENCE_NUMBER;

        log.info("[SSAFY API 호출] Header institutionCode 확인 : {}", uniqueNo);

        return ApiRequestHeader.builder()
                .transmissionDate(transmissionDate)
                .transmissionTime(transmissionTime)
                .institutionCode(INSTITUTION_CODE)
                .fintechAppNo(FINTECH_APP_NO)
                .institutionTransactionUniqueNo(uniqueNo);
    }
}
