package com.rabbit.sse.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbit.sse.domain.dto.response.SseEventResponseDTO;
import com.rabbit.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseRedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseService sseService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String raw = new String(message.getBody(), StandardCharsets.UTF_8);
            SseEventResponseDTO event = objectMapper.readValue(raw, SseEventResponseDTO.class);

            System.out.println(event.getData());
            log.info("[Redis] SSE 이벤트 수신: {}", event);

            sseService.publish(event.getKey(), event.getType(), event.getData());

        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패", e);
        }
    }
}
