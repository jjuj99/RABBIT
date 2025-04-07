package com.rabbit.global.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.rabbit.global.util.CustomZonedDateTimeDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // Java 날짜/시간 모듈에 대한 직렬화 설정
            JavaTimeModule module = new JavaTimeModule();

            // ZonedDateTime
            // 직렬화(Java → JSON)
            module.addSerializer(ZonedDateTime.class,
                    new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 역직렬화(JSON → Java)
            module.addDeserializer(ZonedDateTime.class,
                    new CustomZonedDateTimeDeserializer());

            builder.modules(module);
        };
    }
}
