package com.rabbit.bankApi.service;

import com.rabbit.auth.domain.entity.SsafyAccount;
import com.rabbit.auth.repository.SsafyAccountRepository;
import com.rabbit.bankApi.domain.dto.request.UpdateDepositRequestDTO;
import com.rabbit.bankApi.domain.dto.request.UserKeyRequestDTO;
import com.rabbit.bankApi.domain.dto.response.MyCreditResponseDTO;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.repository.RefundAccountRepository;
import com.rabbit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {

    private final UserRepository userRepository;
    private final SsafyAccountRepository ssafyAccountRepository;

    private final BankApiService bankApiService;

    /**
     * 신용등급 조회
     *
     * @param userId 유저 ID
     * @return 신용 등급 String
     */
    public String getCreditScore(int userId) {
        try {
            // 1. userId로 이메일 조회
            String email = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 사용자입니다."))
                    .getEmail();

            // 2. email로 userKey 조회
            String userKey = ssafyAccountRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록된 계좌가 없습니다. " + email))
                    .getUserKey();

            // 3. userKey로 신용등급 조회
            UserKeyRequestDTO request = UserKeyRequestDTO.builder()
                    .userKey(userKey)
                    .build();

            MyCreditResponseDTO myCreditResponse = bankApiService.myCredit(request).block();

            return myCreditResponse.getRec().getRatingName();
        } catch (Exception e) {
            log.warn("신용등급 조회 실패: userId={}, 이유={}", userId, e.getMessage());
            return "-";
        }
    }

    /**
     * 캐시 출금 (사용자의 통장으로 입금)
     *
     * @param userId 유저 ID
     * @param balance 금액
     */
    public void deposit(int userId, long balance) {
        // 1. userId로 이메일 조회
        String email = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 사용자입니다."))
                .getEmail();

        // 2. 싸피 은행 정보 가져오기
        SsafyAccount ssafyAccount = ssafyAccountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록된 계좌가 없습니다."));

        // 3. 사용자 통장으로 입금 시킴
        UpdateDepositRequestDTO updateDepositRequest = UpdateDepositRequestDTO.builder()
                .userKey(ssafyAccount.getUserKey())
                .accountNo(ssafyAccount.getAccountNo())
                .transactionBalance(String.valueOf(balance))
                .transactionSummary("(수시입출금) : 입금")
                .build();

        bankApiService.updateDeposit(updateDepositRequest);
    }
}
