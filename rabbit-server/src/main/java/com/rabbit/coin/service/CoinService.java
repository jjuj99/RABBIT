package com.rabbit.coin.service;


import com.rabbit.bankApi.service.BankService;
import com.rabbit.blockchain.service.RabbitCoinService;
import com.rabbit.coin.domain.dto.request.CoinPermitRequestDTO;
import com.rabbit.coin.domain.dto.request.CoinWithdrawRequestDTO;
import com.rabbit.coin.domain.dto.request.TossConfirmRequestDTO;
import com.rabbit.coin.domain.dto.request.TossWebhookDataDTO;
import com.rabbit.coin.domain.dto.response.CoinLogListResponseDTO;
import com.rabbit.coin.domain.entity.CoinLog;
import com.rabbit.coin.domain.enums.CoinLogStatus;
import com.rabbit.coin.domain.enums.CoinLogType;
import com.rabbit.coin.repository.CoinLogRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.RefundAccount;
import com.rabbit.user.repository.MetamaskWalletRepository;
import com.rabbit.user.repository.RefundAccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {
    private final CoinLogRepository coinLogRepository;
    private final MetamaskWalletRepository walletRepository;
    private final RabbitCoinService rabbitCoinService;
    private final RefundAccountRepository refundAccountRepository;
    private final BankService bankService;

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

    @Transactional
    public void withdrawCoin(Integer userId, CoinWithdrawRequestDTO coinWithdrawRequestDTO){
        log.info("RAB 코인 출금 시작");

        // 환불 계좌가 로그인 유저에게 등록되어 있는지 확인
        RefundAccount refundAccount = refundAccountRepository.findByUserIdAndAccountNumber(userId, coinWithdrawRequestDTO.getAccountNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NUMBER_NOT_FOUND, "계좌 정보를 찾을 수 없습니다"));
        log.debug("해당 유저의 환불 계좌 = {}", refundAccount);

        // 출금할 메타마스크 계좌 확인
        String account = walletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .map(MetamaskWallet::getWalletAddress)
                .orElseThrow(() -> new BusinessException(ErrorCode.WALLET_NOT_FOUND, "사용자의 주 지갑을 찾을 수 없습니다"));
        log.debug("출금할 메타마스크 계좌 = {}", account);

        // RabbitCoin 컨트랙트의 balanceOf 함수 호출 - 잔액 확인
        BigInteger balanceRAB = rabbitCoinService.balanceOf(account);
        if(balanceRAB.compareTo(BigInteger.valueOf(coinWithdrawRequestDTO.getAmount())) < 0){
            log.error("RAB 부족으로 출금 실패 RAB = {}, 출금액 = {}", balanceRAB, coinWithdrawRequestDTO.getAmount());
            throw new BusinessException(ErrorCode.INSUFFICIENT_RAB_BALANCE, "보유한 RAB이 출금 요청액보다 부족합니다");
        }
        log.debug("보유 RAB = {}, 출금액 = {}", balanceRAB, coinWithdrawRequestDTO.getAmount());

        // 출금 금액만큼 RAB 소각
        burnRabbitCoin(account, BigInteger.valueOf(coinWithdrawRequestDTO.getAmount()));
        log.debug("사용자의 RAB 코인 소각 완료");

        // 싸피은행 입금 처리
        bankService.deposit(userId, coinWithdrawRequestDTO.getAmount());
        log.debug("환불 계좌에 입금 완료");

        // 코인 출금 로그 저장
        coinLogRepository.save(CoinLog.builder()
                .userId(userId)
                .amount(coinWithdrawRequestDTO.getAmount())
                .type(CoinLogType.WITHDRAWAL)
                .orderId(null)
                .account(account)
                .paymentKey(null)
                .status(CoinLogStatus.SUCCESS)
                .createdAt(ZonedDateTime.now())
                .build());
    }

    public void burnRabbitCoin(String account, BigInteger amount){
        String zeroAddress = "0x0000000000000000000000000000000000000000";
        try {
            // RabbitCoin 컨트랙트의 transfer를 통해 0주소로 RAB 전송
            TransactionReceipt receipt = rabbitCoinService.transferFrom(account, zeroAddress, amount);

            // 트랜잭션 성공 여부 확인
            boolean isSuccess = "0x1".equals(receipt.getStatus());
            if (isSuccess) {
                log.info("RAB 0주소로 전송 성공: {} {}", account, amount);
            } else {
                log.error("RAB 0주소로 전송 실패. 트랜잭션 상태: {}", receipt.getStatus());
            }
        } catch (Exception e) {
            log.error("RAB 전송 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.RAB_TRANSFER_FAIL, "RAB 전송 중 오류가 발생했습니다");
        }
    }

    public List<CoinLogListResponseDTO> getTransactions(Integer userId){
        List<CoinLog> coinLogs = coinLogRepository.findByUserId(userId);

        // 조회된 로그가 없는 경우 null 반환
        if (coinLogs == null || coinLogs.isEmpty()) {
            return null;
        }

        return coinLogs.stream()
                .map(coinLog -> CoinLogListResponseDTO.builder()
                        .type(coinLog.getType())
                        .amount(coinLog.getAmount())
                        .createdAt(coinLog.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void permit(@Valid CoinPermitRequestDTO requestDTO) {
        try {
            // String을 byte[] 형태로 변환
            byte[] signature = Numeric.hexStringToByteArray(requestDTO.getSignature());

            // RabbitCoin 컨트랙트의 permit을 통해 승인
            TransactionReceipt receipt = rabbitCoinService.permit(
                    requestDTO.getOwner(),
                    requestDTO.getSpender(),
                    BigInteger.valueOf(requestDTO.getValue()),
                    BigInteger.valueOf(requestDTO.getDeadline()),
                    signature
            );

            // 트랜잭션 성공 여부 확인
            boolean isSuccess = "0x1".equals(receipt.getStatus());
            if (isSuccess) {
                log.info("RAB permit 성공: {} -> {}, {}", requestDTO.getOwner(), requestDTO.getSpender(), requestDTO.getValue());
            } else {
                log.error("RAB permit 실패. 트랜잭션 상태: {}", receipt.getStatus());
            }
        } catch (Exception e) {
            log.error("RAB permit 오류: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.RAB_PERMIT_FAIL, "RAB permit 중 오류가 발생했습니다");
        }
    }
}
