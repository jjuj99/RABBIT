package com.rabbit.sse.controller;

import com.rabbit.global.util.JwtUtil;
import com.rabbit.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
public class SseController {

    private final SseService sseService;
    private final JwtUtil jwtUtil;

    // 인증이 필요한 개인 SSE
    @GetMapping(value = "/subscribe/user", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeUser(
            @RequestParam String token,
            @RequestHeader(value = "Last-Event-Id", required = false) String lastEventId
    ) {
        String userId = jwtUtil.getUserIdFromToken(token);
        String key = "user-" + userId;
        return sseService.subscribe(key);
    }

    // 인증 없이 구독 가능한 경매 SSE
    @GetMapping(value = "/subscribe/auction", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeAuction(
            @RequestParam Long id,
            @RequestHeader(value = "Last-Event-Id", required = false) String lastEventId
    ) {
        String key = "auction-" + id;
        return sseService.subscribe(key);
    }

}