package com.rabbit.sse.controller;

import com.rabbit.sse.controller.swagger.SseControllerSwagger;
import com.rabbit.sse.domain.dto.request.SseRequestDTO;
import com.rabbit.sse.service.SseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sse")
public class SseController {

    private final SseService sseService;

    @SseControllerSwagger.SubscribeSseApi
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @Valid SseRequestDTO request,
            @RequestHeader(value = "Last-Event-Id", required = false) String lastEventId
    ) {
        String key = request.toKey();
        return sseService.subscribe(key);
    }
}