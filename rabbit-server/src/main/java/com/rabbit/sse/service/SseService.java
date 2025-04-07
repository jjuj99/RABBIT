package com.rabbit.sse.service;


import com.rabbit.auction.domain.dto.response.BidResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseService {

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String key) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃
        emitters.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> emitters.get(key).remove(emitter));
        emitter.onTimeout(() -> {
            emitters.get(key).remove(emitter);
            emitter.complete();
        });
        emitter.onError(e -> {
            emitters.get(key).remove(emitter);
            emitter.completeWithError(e);
        });

        return emitter;
    }

    public void publish(String key, String eventName, Object data) {
        List<SseEmitter> list = emitters.getOrDefault(key, List.of());
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .id("event-" + System.currentTimeMillis())
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
