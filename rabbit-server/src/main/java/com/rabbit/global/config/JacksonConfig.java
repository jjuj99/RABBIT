package com.rabbit.global.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // Java 날짜/시간 모듈에 대한 직렬화 설정
            JavaTimeModule module = new JavaTimeModule();

            // ZonedDateTime에 대한 커스텀 직렬화기 등록
            module.addSerializer(ZonedDateTime.class,
                    new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // LocalDateTime에 대한 커스텀 직렬화기도 필요하다면 추가
//            module.addSerializer(LocalDateTime.class,
//                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            builder.modules(module);
        };
    }
}
