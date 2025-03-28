package com.rabbit.coin.service;


import com.rabbit.coin.domain.dto.request.TossConfirmRequestDTO;
import com.rabbit.coin.domain.dto.request.TossWebhookDataDTO;
import com.rabbit.coin.domain.entity.CoinLog;
import com.rabbit.coin.domain.enums.CoinLogStatus;
import com.rabbit.coin.domain.enums.CoinLogType;
import com.rabbit.coin.repository.CoinLogRepository;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.repository.MetamaskWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinLogRepository coinLogRepository;
    private final MetamaskWalletRepository walletRepository;

    public void recordSuccess(TossConfirmRequestDTO req, Integer userId) {
        saveCoinLog(req, userId, CoinLogStatus.SUCCESS);
    }

    public void recordFailure(TossConfirmRequestDTO req, Integer userId) {
        saveCoinLog(req, userId, CoinLogStatus.FAIL);
    }

    private void saveCoinLog(TossConfirmRequestDTO req, Integer userId, CoinLogStatus status) {
        String account = walletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .map(MetamaskWallet::getWalletAddress)
                .orElse("UNKNOWN");

        coinLogRepository.save(CoinLog.builder()
                .userId(userId)
                .amount(req.getAmount())
                .type(CoinLogType.DEPOSIT)
                .orderId(req.getOrderId())
                .account(account)
                .paymentKey(req.getPaymentKey())
                .status(status)
                .createdAt(ZonedDateTime.now())
                .build());
    }

    public boolean processWebhook(TossWebhookDataDTO webHookData) {

        Optional<CoinLog> optionalLog = coinLogRepository.findByOrderId(webHookData.getOrderId());

        if (optionalLog.isEmpty()) {
            log.warn("CoinLog 없음 → 재시도 유도");
            return false;
        }

        CoinLog coinLog = optionalLog.get();

        if ("DONE".equals(webHookData.getStatus())) {
            log.info("성공");
            if (coinLog.getStatus() != CoinLogStatus.SUCCESS) {
                coinLog.setStatus(CoinLogStatus.SUCCESS);
                coinLogRepository.save(coinLog);
            }
        } else {
            log.info("실패");
            if (coinLog.getStatus() != CoinLogStatus.FAIL) {
                coinLog.setStatus(CoinLogStatus.FAIL);
                coinLogRepository.save(coinLog);
            }
        }
        return true;
    }
}
