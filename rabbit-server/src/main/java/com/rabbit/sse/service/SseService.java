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
        log.info("[SSE구독시작] 키={}", key);

        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간 타임아웃
        log.debug("[SSE이미터생성] 키={}, 타임아웃=1시간", key);

        emitters.computeIfAbsent(key, k -> {
            log.debug("[SSE구독목록생성] 키={} 최초 구독 등록", k);
            return new CopyOnWriteArrayList<>();
        }).add(emitter);

        int currentSubscribers = emitters.getOrDefault(key, List.of()).size();
        log.debug("[SSE구독자수] 키={}, 현재구독자수={}", key, currentSubscribers);

        // 이벤트 핸들러 등록
        emitter.onCompletion(() -> {
            emitters.get(key).remove(emitter);
            log.debug("[SSE구독완료] 키={}, 구독완료처리, 남은구독자수={}",
                    key, emitters.getOrDefault(key, List.of()).size());
        });

        emitter.onTimeout(() -> {
            emitters.get(key).remove(emitter);
            emitter.complete();
            log.debug("[SSE구독타임아웃] 키={}, 타임아웃발생, 남은구독자수={}",
                    key, emitters.getOrDefault(key, List.of()).size());
        });

        emitter.onError(e -> {
            emitters.get(key).remove(emitter);
            emitter.completeWithError(e);
            log.error("[SSE구독오류] 키={}, 오류발생: {}, 남은구독자수={}",
                    key, e.getMessage(), emitters.getOrDefault(key, List.of()).size(), e);
        });

        try {
            // 초기 연결 테스트를 위한 더미 이벤트 전송
            emitter.send(SseEmitter.event()
                    .id("connected-" + System.currentTimeMillis())
                    .name("connect")
                    .data("연결 성공"));
            log.debug("[SSE초기연결] 키={}, 초기 연결 확인 이벤트 전송 완료", key);
        } catch (IOException e) {
            log.error("[SSE초기연결실패] 키={}, 초기 연결 확인 중 오류 발생: {}", key, e.getMessage(), e);
            emitter.completeWithError(e);
            return new SseEmitter(); // 실패 시 새 이미터 반환
        }

        log.info("[SSE구독완료] 키={}, 구독처리완료", key);
        return emitter;
    }

    public void publish(String key, String eventName, Object data) {
        log.info("[SSE이벤트발행시작] 키={}, 이벤트명={}", key, eventName);

        List<SseEmitter> list = emitters.getOrDefault(key, List.of());
        if (list.isEmpty()) {
            log.debug("[SSE발행취소] 키={}, 구독자가 없음", key);
            return;
        }

        log.debug("[SSE발행정보] 키={}, 이벤트명={}, 발행대상={}, 데이터유형={}",
                key, eventName, list.size(), data != null ? data.getClass().getSimpleName() : "null");

        int successCount = 0;
        int failCount = 0;

        for (SseEmitter emitter : list) {
            try {
                String eventId = "event-" + System.currentTimeMillis();
                emitter.send(SseEmitter.event()
                        .id(eventId)
                        .name(eventName)
                        .data(data));

                successCount++;
                log.trace("[SSE발행성공] 키={}, 이벤트명={}, 이벤트ID={}", key, eventName, eventId);
            } catch (IOException e) {
                failCount++;
                log.error("[SSE발행실패] 키={}, 이벤트명={}, 오류: {}", key, eventName, e.getMessage());
                emitter.completeWithError(e);
            }
        }

        log.info("[SSE이벤트발행완료] 키={}, 이벤트명={}, 성공={}, 실패={}",
                key, eventName, successCount, failCount);
    }

    /**
     * 특정 키에 대한 모든 SSE 연결을 종료합니다.
     * @param key 종료할 연결의 키
     * @return 종료된 연결 수
     */
    public int closeAllConnections(String key) {
        log.info("[SSE전체연결종료시작] 키={}", key);

        List<SseEmitter> list = emitters.getOrDefault(key, List.of());
        if (list.isEmpty()) {
            log.debug("[SSE전체종료취소] 키={}, 종료할 연결 없음", key);
            return 0;
        }

        int count = list.size();
        for (SseEmitter emitter : list) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.warn("[SSE연결종료오류] 키={}, 종료 중 오류: {}", key, e.getMessage());
            }
        }

        list.clear();
        emitters.remove(key);

        log.info("[SSE전체연결종료완료] 키={}, 종료된연결수={}", key, count);
        return count;
    }

    /**
     * 현재 활성화된 모든 키와 구독자 수를 조회합니다.
     * @return 키별 구독자 수 정보
     */
    public Map<String, Integer> getActiveSubscribers() {
        log.debug("[SSE구독상태조회] 활성화된 키 수={}", emitters.size());

        Map<String, Integer> result = new ConcurrentHashMap<>();
        emitters.forEach((key, list) -> {
            int count = list.size();
            result.put(key, count);
            log.trace("[SSE구독상태상세] 키={}, 구독자수={}", key, count);
        });

        return result;
    }
}