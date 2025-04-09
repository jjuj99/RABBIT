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
        log.info("[경매 종료 메시지 수신] 데드레터 큐에서 메시지 수신: {}", messageStr);

        AuctionRetryMessage message;
        try {
            // 메시지 역직렬화 시도
            log.debug("[메시지 역직렬화 시작] 원본 메시지: {}", messageStr);
            message = objectMapper.readValue(messageStr, AuctionRetryMessage.class);
            log.info("[메시지 역직렬화 성공] 경매 ID: {}, 재시도 횟수: {}", message.getAuctionId(), message.getRetryCount());

            // 경매 종료 처리 시도
            log.info("[경매 종료 처리 시작] 경매 ID: {}", message.getAuctionId());
            auctionService.processAuctionEnd(message.getAuctionId());
            log.info("[경매 종료 처리 완료] 경매 ID: {} 성공적으로 처리됨", message.getAuctionId());

        } catch (Exception e) {
            log.error("[경매 종료 처리 실패] 메시지: {}, 오류 내용: {}, 오류 클래스: {}",
                    messageStr, e.getMessage(), e.getClass().getName());
            log.debug("[경매 종료 처리 실패 상세] 스택 트레이스: ", e);

            // 실패한 메시지를 다시 객체로 역직렬화 (JSON → DTO)
            try {
                log.debug("[재시도용 메시지 역직렬화 시작] 원본 메시지: {}", messageStr);
                message = objectMapper.readValue(messageStr, AuctionRetryMessage.class);
                log.info("[재시도용 메시지 역직렬화 성공] 경매 ID: {}, 현재 재시도 횟수: {}",
                        message.getAuctionId(), message.getRetryCount());
            } catch (JsonProcessingException ex) {
                log.error("[메시지 역직렬화 실패] 원본 메시지: {}, 오류 내용: {}", messageStr, ex.getMessage());
                log.debug("[메시지 역직렬화 실패 상세] 스택 트레이스: ", ex);
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 종료 메시지 역직렬화 실패");
            }

            // 최대 재시도 횟수 초과 시 중단 (더 이상 재전송하지 않음)
            if (message.getRetryCount() >= MAX_RETRY) {
                log.error("[재시도 횟수 초과로 처리 중단] 경매 ID: {}, 재시도 횟수: {}/{}, 마지막 오류: {}",
                        message.getAuctionId(), message.getRetryCount(), MAX_RETRY, e.getMessage());
                log.info("[경매 종료 처리 최종 실패] 경매 ID: {}의 처리가 실패하여 별도 처리 필요", message.getAuctionId());
                return;
            }

            // retryCount 1 증가시켜 새 메시지 객체 생성
            int newRetryCount = message.getRetryCount() + 1;
            log.info("[재시도 메시지 생성] 경매 ID: {}, 새 재시도 횟수: {}/{}",
                    message.getAuctionId(), newRetryCount, MAX_RETRY);

            AuctionRetryMessage retryMessage = AuctionRetryMessage.builder()
                    .auctionId(message.getAuctionId())
                    .retryCount(newRetryCount)
                    .build();

            // 객체를 JSON 문자열로 직렬화
            String newMessage;
            try {
                log.debug("[재시도 메시지 직렬화 시작] 메시지 객체: {}", retryMessage);
                newMessage = objectMapper.writeValueAsString(retryMessage);
                log.debug("[재시도 메시지 직렬화 완료] 직렬화된 메시지: {}", newMessage);
            } catch (JsonProcessingException e1) {
                log.error("[재시도 메시지 직렬화 실패] 경매 ID: {}, 오류 내용: {}",
                        message.getAuctionId(), e1.getMessage());
                log.debug("[재시도 메시지 직렬화 실패 상세] 스택 트레이스: ", e1);
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 종료 메시지 직렬화 실패");
            }

            // 딜레이를 위한 메시지 속성 설정 (5초 후 재전송)
            log.info("[재시도 딜레이 설정] 경매 ID: {}, 딜레이: {}ms", message.getAuctionId(), RETRY_DELAY_MS);
            MessagePostProcessor processor = msg -> {
                msg.getMessageProperties().setExpiration(String.valueOf(RETRY_DELAY_MS));
                return msg;
            };

            // 딜레이 큐로 재시도 메시지 전송
            log.info("[재시도 메시지 전송 시작] 큐: {}, 경매 ID: {}, 재시도 횟수: {}/{}",
                    RabbitMQConfig.DELAY_QUEUE, message.getAuctionId(), newRetryCount, MAX_RETRY);

            rabbitTemplate.convertAndSend(
                    "", // default exchange
                    RabbitMQConfig.DELAY_QUEUE,
                    newMessage,
                    processor
            );

            log.info("[재시도 메시지 전송 완료] 경매 ID: {}, 재시도 횟수: {}/{}, 다음 실행 예정: {}ms 후",
                    message.getAuctionId(), newRetryCount, MAX_RETRY, RETRY_DELAY_MS);
        }
    }
}