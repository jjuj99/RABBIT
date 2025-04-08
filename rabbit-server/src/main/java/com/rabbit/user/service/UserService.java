package com.rabbit.user.service;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.dto.response.*;
import com.rabbit.user.domain.entity.*;
import com.rabbit.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final RefundAccountRepository refundAccountRepository;
    private final MetamaskWalletRepository metamaskWalletRepository;

    @Transactional(readOnly = true)
    public LoginInfoResponseDTO getLoginInfo(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 회원입니다"));

        RefundAccount refundAccount = refundAccountRepository.findByUserIdAndPrimaryFlagTrue(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록된 환불 계좌 정보가 없습니다."));

        Bank bank = bankRepository.findById(refundAccount.getBankId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록되지 않은 은행 정보입니다."));

        return LoginInfoResponseDTO.builder()
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bankId(bank.getBankId())
                .bankName(bank.getBankName())
                .refundAccount(refundAccount.getAccountNumber())
                .build();
    }

    @Transactional(readOnly = true)
    public ProfileInfoResponseDTO getProfileInfo(int userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 회원입니다"));

        // 환불 계좌 조회
        RefundAccount refundAccount = refundAccountRepository.findByUserIdAndPrimaryFlagTrue(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "환불 계좌를 찾을 수 없습니다"));

        // 은행 정보 조회
        Bank bank = bankRepository.findById(refundAccount.getBankId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "은행 정보를 찾을 수 없습니다"));

        // 메타마스크 지갑 조회
        MetamaskWallet metamaskWallet = metamaskWalletRepository.findByUserUserIdAndPrimaryFlagTrue(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "메타마스크 지갑을 찾을 수 없습니다"));

        return ProfileInfoResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .bankName(bank.getBankName())
                .refundAccount(refundAccount.getAccountNumber())
                .walletAddress(metamaskWallet.getWalletAddress())
                .build();
    }

    @Transactional(readOnly = true)
    public SearchUserResponseDTO searchUserByEmail(String searchEmail) {
        return userRepository.findByEmail(searchEmail)
                .map(user -> {
                    String walletAddress = metamaskWalletRepository.findByUser_UserIdAndPrimaryFlagTrue(user.getUserId())
                                    .map(MetamaskWallet::getWalletAddress)
                                    .orElseGet(() -> null);

                    return SearchUserResponseDTO.builder()
                            .userId(user.getUserId())
                            .email(user.getEmail())
                            .userName(user.getUserName())
                            .nickname(user.getNickname())
                            .walletAddress(walletAddress)
                            .build();
                })
                .orElseGet(() -> null);
    }

    @Transactional
    public void deleteByUserId(int userId) {
        userRepository.deleteById(userId);
    }

    public MetamaskWallet getWalletByUserIdAndPrimaryFlagTrue(Integer userId) {
        return metamaskWalletRepository.findByUser_UserIdAndPrimaryFlagTrue(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 유저의 지갑 정보가 없습니다."));
    }

    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 유저를 찾을 수 없습니다."));
    }
}
