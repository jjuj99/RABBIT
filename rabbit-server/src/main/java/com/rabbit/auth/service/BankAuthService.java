package com.rabbit.auth.service;

import com.rabbit.auth.domain.dto.request.AccountAuthSendRequestDTO;
import com.rabbit.auth.domain.dto.request.AccountAuthVerifyRequestDTO;
import com.rabbit.auth.domain.dto.response.AccountAuthVerifyResponseDTO;
import com.rabbit.auth.domain.entity.SsafyAccount;
import com.rabbit.auth.repository.SsafyAccountRepository;
import com.rabbit.bankApi.domain.dto.request.CheckAccountRequestDTO;
import com.rabbit.bankApi.domain.dto.request.CreateDemandAccountRequestDTO;
import com.rabbit.bankApi.domain.dto.request.MemberRequestDTO;
import com.rabbit.bankApi.domain.dto.request.OpenAccountRequestDTO;
import com.rabbit.bankApi.domain.dto.response.CheckAccountResponseDTO;
import com.rabbit.bankApi.domain.dto.response.CreateDemandAccountResponseDTO;
import com.rabbit.bankApi.domain.dto.response.MemberResponseDTO;
import com.rabbit.bankApi.service.BankApiService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.entity.Bank;
import com.rabbit.user.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAuthService {
    static class AccountInfo {
        String userKey;
        String accountNo;

        AccountInfo(String userKey, String accountNo) {
            this.userKey = userKey;
            this.accountNo = accountNo;
        }
    }

    private final SsafyAccountRepository ssafyBankRepository;
    private final BankRepository bankRepository;

    private final BankApiService bankApiService;

    public List<Bank> findAllBanks() {
        return bankRepository.findAll();
    }

    public void accountAuthSend(AccountAuthSendRequestDTO request) {
        AccountInfo accountInfo = ssafyBankRepository.findByEmail(request.getEmail())
                // 0. 이미 싸피은행 정보가 존재하면, 해당 정보 사용
                .map(account -> new AccountInfo(account.getUserKey(), account.getAccountNo()))
                // 없다면 생성
                .orElseGet(() -> {
                    // 1. 싸피 은행 유저인지 확인
                    MemberRequestDTO memberRequest = MemberRequestDTO.builder()
                            .userId(request.getEmail())
                            .build();
                    MemberResponseDTO memberResponse = bankApiService.searchMember(memberRequest).block();

                    // 2. 싸피 은행 유저가 아니면 계정 생성
                    if (memberResponse == null) {
                        memberResponse = bankApiService.createMember(memberRequest).block();
                    }

                    String userKey = memberResponse.getUserKey();

                    // 3. 계좌 생성
                    CreateDemandAccountRequestDTO createDemandAccountRequest = CreateDemandAccountRequestDTO.builder()
                            .userKey(userKey)
                            .build();

                    CreateDemandAccountResponseDTO createDemandAccountResponse = bankApiService.createDemandAccount(createDemandAccountRequest).block();
                    String accountNo = createDemandAccountResponse.getRec().getAccountNo();

                    // 4. 우리 DB에 email, accountNo, userKey 저장
                    ssafyBankRepository.save(SsafyAccount.builder()
                            .email(request.getEmail())
                            .accountNo(accountNo)
                            .userKey(userKey)
                            .createdAt(ZonedDateTime.now())
                            .build()
                    );

                    return new AccountInfo(userKey, accountNo);
                });

        // 5. 해당 계좌로 인증번호 전송
        OpenAccountRequestDTO openAccountRequest = OpenAccountRequestDTO.builder()
                .userKey(accountInfo.userKey)
                .accountNo(accountInfo.accountNo)
                .build();

        bankApiService.openAccount(openAccountRequest).block();
    }

    public AccountAuthVerifyResponseDTO accountAuthVerify(AccountAuthVerifyRequestDTO request) {
        // 1. 이메일로 userKey, accountNo 호출
        SsafyAccount ssafyAccount = ssafyBankRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "계좌 인증을 다시 시도해주세요."));

        // 2. 작성한 코드로 1원 검증 호출
        CheckAccountRequestDTO checkAccountRequest = CheckAccountRequestDTO.builder()
                .userKey(ssafyAccount.getUserKey())
                .accountNo(ssafyAccount.getAccountNo())
                .authCode(request.getAuthCode())
                .build();

        CheckAccountResponseDTO checkAccountResponse = bankApiService.checkAccount(checkAccountRequest).block();
        
        if (checkAccountResponse == null) {
            return AccountAuthVerifyResponseDTO.builder()
                    .isVerified(false)
                    .message("계좌 인증에 실패했습니다")
                    .build();
        }

        return AccountAuthVerifyResponseDTO.builder()
                .isVerified(true)
                .message("계좌 인증에 성공했습니다")
                .build();
    }
}
