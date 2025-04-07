package com.rabbit.coin.controller;

import com.rabbit.coin.domain.dto.request.CoinWithdrawRequestDTO;
import com.rabbit.coin.domain.dto.request.TossConfirmRequestDTO;
import com.rabbit.coin.domain.dto.request.TossWebhookDTO;
import com.rabbit.coin.domain.dto.request.TossWebhookDataDTO;
import com.rabbit.coin.controller.swagger.CoinControllerSwagger;
import com.rabbit.coin.domain.dto.response.CoinLogListResponseDTO;
import com.rabbit.coin.service.CoinService;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.rabbit.global.util.TossErrorUtil.extractErrorMessage;

@RestController
@RequestMapping("/api/v1/coins")
@RequiredArgsConstructor
@Slf4j
public class CoinController {
    private final CoinService coinService;
    private final RestTemplate restTemplate;

    @Value("${toss.secret}")
    private String secretKey;

    @CoinControllerSwagger.TossConfirmApi
    @PostMapping("/confirm")
    public ResponseEntity<CustomApiResponse<?>> confirm(Authentication authentication, @RequestBody TossConfirmRequestDTO request) {
        String userId = (String) authentication.getPrincipal();

        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedKey);

        Map<String, Object> body = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);

            coinService.recordSuccess(request, Integer.parseInt(userId)); // 응답 성공

            return ResponseEntity.status(HttpStatus.CREATED).body(CustomApiResponse.success(MessageResponse.of("계좌 이체 성공했습니다.")));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            coinService.recordFailure(request, Integer.parseInt(userId)); // 실패 기록

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.error(
                            HttpStatus.BAD_REQUEST.value(),
                            extractErrorMessage(e)
                    ));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody TossWebhookDTO payload) {
        TossWebhookDataDTO webHookData = payload.getData();

        if (webHookData == null || webHookData.getOrderId() == null) {
            return ResponseEntity.badRequest().build(); // 400
        }

        boolean success = coinService.processWebhook(webHookData);

        if (!success) {
            // Toss가 다시 webhook 보내도록 500 error 리턴
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }

    @CoinControllerSwagger.WithdrawAPI
    @PostMapping("/withdraw")
    public ResponseEntity<CustomApiResponse<MessageResponse>> withdraw(@Valid @RequestBody CoinWithdrawRequestDTO coinWithdrawRequestDTO, Authentication authentication){
        String userId = (String) authentication.getPrincipal();

        coinService.withdrawCoin(Integer.parseInt(userId), coinWithdrawRequestDTO);

        return ResponseEntity.ok(CustomApiResponse.success(
                MessageResponse.of("출금이 완료되었습니다")
        ));
    }

    @CoinControllerSwagger.GetTransactionsAPI
    @GetMapping("/transactions")
    public ResponseEntity<CustomApiResponse<List<CoinLogListResponseDTO>>> getTransactions(Authentication authentication){
        String userId = (String) authentication.getPrincipal();

        List<CoinLogListResponseDTO> response = coinService.getTransactions(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
