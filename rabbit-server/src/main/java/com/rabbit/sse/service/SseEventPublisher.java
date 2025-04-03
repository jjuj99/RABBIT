package com.rabbit.sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbit.sse.domain.dto.response.SseEventResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEventPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String type, String key, Object data) {
        try {
            SseEventResponseDTO message = new SseEventResponseDTO(type, key, data);
            String payload = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend("notifications", payload);
        } catch (Exception e) {
            log.error("SSE Redis publish 실패", e);
        }
    }
}
