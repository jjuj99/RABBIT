package com.rabbit.auction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbit.auction.domain.dto.AuctionRetryMessage;
import com.rabbit.auction.service.AuctionService;
import com.rabbit.global.config.RabbitMQConfig;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionDeadLetterListener {

    private final AuctionService auctionService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRY = 3;
    private static final int RETRY_DELAY_MS = 5000;

    @RabbitListener(queues = RabbitMQConfig.DEAD_QUEUE)
    public void handleAuctionEnd(String messageStr) {
        AuctionRetryMessage message;
        try {
            message = objectMapper.readValue(messageStr, AuctionRetryMessage.class);
            auctionService.processAuctionEnd(message.getAuctionId());

        } catch (Exception e) {
            log.error("[경매 종료 실패] message={}, error={}", messageStr, e.getMessage());

            // 실패한 메시지를 다시 객체로 역직렬화 (JSON → DTO)
            try {
                message = objectMapper.readValue(messageStr, AuctionRetryMessage.class);
            } catch (JsonProcessingException ex) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 종료 메시지 역직렬화 실패");
            }

            // 최대 재시도 횟수 초과 시 중단 (더 이상 재전송하지 않음)
            if (message.getRetryCount() >= MAX_RETRY) {
                log.error("[재시도 중단] auctionId={}, retryCount={}", message.getAuctionId(), message.getRetryCount());
                return;
            }

            // retryCount 1 증가시켜 새 메시지 객체 생성
            AuctionRetryMessage retryMessage = AuctionRetryMessage.builder()
                    .auctionId(message.getAuctionId())
                    .retryCount(message.getRetryCount() + 1)
                    .build();

            // 객체를 JSON 문자열로 직렬화
            String newMessage;
            try {
                newMessage = objectMapper.writeValueAsString(retryMessage);
            } catch (JsonProcessingException e1) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 종료 메시지 직렬화 실패");
            }

            // 딜레이를 위한 메시지 속성 설정 (5초 후 재전송)
            MessagePostProcessor processor = msg -> {
                msg.getMessageProperties().setExpiration(String.valueOf(RETRY_DELAY_MS));
                return msg;
            };

            // 딜레이 큐로 재시도 메시지 전송
            rabbitTemplate.convertAndSend(
                    "", // default exchange
                    RabbitMQConfig.DELAY_QUEUE,
                    newMessage,
                    processor
            );

            log.info("[재시도 전송] auctionId={}, retryCount={}", retryMessage.getAuctionId(), retryMessage.getRetryCount());
        }
    }
}

