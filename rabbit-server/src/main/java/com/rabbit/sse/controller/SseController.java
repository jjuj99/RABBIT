package com.rabbit.sse.controller;

import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.util.JwtUtil;
import com.rabbit.sse.controller.swagger.SseControllerSwagger;
import com.rabbit.sse.controller.swagger.SseTestControllerSwagger;
import com.rabbit.sse.domain.dto.response.NotiResponseDTO;
import com.rabbit.sse.service.SseEventPublisher;
import com.rabbit.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
@Slf4j
public class SseController {

    private final SseService sseService;
    private final SseEventPublisher sseEventPublisher;
    private final JwtUtil jwtUtil;

    @SseControllerSwagger.SubscribeSseApi
    @GetMapping(value = "/subscribe/user", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeUser(
            @RequestParam(name = "token") String token,
            @RequestHeader(value = "Last-Event-Id", required = false) String lastEventId
    ) {
        String userId = jwtUtil.getUserIdFromToken(token);
        String key = "user-" + userId;
        log.info("[SSE/사용자구독] 사용자ID={} 구독 시작", userId);
        return sseService.subscribe(key);
    }

    @SseControllerSwagger.SubscribeSseApi
    @GetMapping(value = "/subscribe/auction", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeAuction(
            @RequestParam(name = "id") Long id,
            @RequestHeader(value = "Last-Event-Id", required = false) String lastEventId
    ) {
        String key = "auction-" + id;
        log.info("[SSE/경매구독] 경매ID={} 구독 시작", id);
        return sseService.subscribe(key);
    }

    @SseTestControllerSwagger.PublishTestEventApi
    @PostMapping("/test/publish")
    public CustomApiResponse<String> publishTestEvent(
            @RequestParam("type") String type,
            @RequestParam("key") String key,
            @RequestParam("message") String message
    ) {
        log.info("[SSE/테스트이벤트] 발행 요청 - 타입: {}, 키: {}, 메시지: {}", type, key, message);

        try {
            // 테스트용 이벤트 객체 생성
            NotiResponseDTO eventData = NotiResponseDTO.builder()
                    .type(type)
                    .message(message)
                    .tokenId(key.replace("auction-", ""))
                    .build();

            // Redis를 통한 이벤트 발행
            sseEventPublisher.publish(type, key, eventData);

            // 직접 서비스를 통한 이벤트 발행 (Redis가 작동하지 않는 경우 테스트용)
            sseService.publish(key, type, eventData);

            log.info("[SSE/테스트이벤트] 발행 성공 - 타입: {}, 키: {}", type, key);
            return CustomApiResponse.success("이벤트 발행 성공");
        } catch (Exception e) {
            log.error("[SSE/테스트이벤트] 발행 실패 - 타입: {}, 키: {}, 오류: {}", type, key, e.getMessage(), e);
//            return CustomApiResponse.error("이벤트 발행 실패: " + e.getMessage());
            return null;
        }
    }

    @SseTestControllerSwagger.GetActiveSubscribersApi
    @GetMapping("/test/subscribers")
    public CustomApiResponse<Map<String, Integer>> getActiveSubscribers() {
        log.info("[SSE/구독자조회] 활성 구독자 조회 요청");
        Map<String, Integer> subscribers = sseService.getActiveSubscribers();
        log.debug("[SSE/구독자조회] 활성 구독자 수: {}", subscribers.size());
        return CustomApiResponse.success(subscribers);
    }
}