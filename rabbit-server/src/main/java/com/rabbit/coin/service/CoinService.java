package com.rabbit.coin.service;


import com.rabbit.blockchain.service.RabbitCoinService;
import com.rabbit.coin.domain.dto.request.TossConfirmRequestDTO;
import com.rabbit.coin.domain.dto.request.TossWebhookDataDTO;
import com.rabbit.coin.domain.entity.CoinLog;
import com.rabbit.coin.domain.enums.CoinLogStatus;
import com.rabbit.coin.domain.enums.CoinLogType;
import com.rabbit.coin.repository.CoinLogRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.repository.MetamaskWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinLogRepository coinLogRepository;
    private final MetamaskWalletRepository walletRepository;
    private final RabbitCoinService rabbitCoinService;

    public void recordSuccess(TossConfirmRequestDTO req, Integer userId) {
        String account = walletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .map(MetamaskWallet::getWalletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND, "사용자의 주 지갑을 찾을 수 없습니다"));

        mintRabbitCoin(account, req.getAmount());
        saveCoinLog(req, userId, account, CoinLogStatus.SUCCESS);
    }

    public void recordFailure(TossConfirmRequestDTO req, Integer userId) {
        String account = walletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .map(MetamaskWallet::getWalletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND, "사용자의 주 지갑을 찾을 수 없습니다"));

        saveCoinLog(req, userId, account, CoinLogStatus.FAIL);
    }

    private void saveCoinLog(TossConfirmRequestDTO req, Integer userId, String account, CoinLogStatus status) {
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

    // 사용자의 메타마스크 계좌에 RAB 민팅
    public void mintRabbitCoin(String account, long amount) {
        try {
            // RabbitCoin 컨트랙트의 mint 함수 호출
            TransactionReceipt receipt = rabbitCoinService.mint(account, BigInteger.valueOf(amount));

            // 트랜잭션 성공 여부 확인
            boolean isSuccess = "0x1".equals(receipt.getStatus());
            if (isSuccess) {
                log.info("RAB 민팅 성공: {} {}", amount, account);
            } else {
                log.error("RAB 민팅 실패. 트랜잭션 상태: {}", receipt.getStatus());
            }
        } catch (Exception e) {
            log.error("RAB 민팅 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.RAB_MINT_FAIL, "RAB 발행 중 오류가 발생했습니다");
        }
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
