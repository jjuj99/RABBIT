package com.rabbit.auction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbit.auction.domain.dto.AuctionRetryMessage;
import com.rabbit.global.config.RabbitMQConfig;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void scheduleAuctionEnd(Integer auctionId, ZonedDateTime endDate) {
        // endDate가 UTC라면 KST로 바꿔서 비교
        ZonedDateTime adjustedEndDate = endDate; //<- 시간대 맞춰서
        long delay = Duration.between(ZonedDateTime.now(), adjustedEndDate).toMillis();

        if (delay <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 종료 시간이 현재보다 이전일 수 없습니다.");
        }

        AuctionRetryMessage message = AuctionRetryMessage.builder()
                .auctionId(auctionId)
                .retryCount(0)
                .build();

        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 메시지 직렬화 실패");
        }

        MessagePostProcessor processor = msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(delay));
            return msg;
        };

        rabbitTemplate.convertAndSend(
                "",
                RabbitMQConfig.DELAY_QUEUE,
                jsonMessage,
                processor
        );
    }

}
