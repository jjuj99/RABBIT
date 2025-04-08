package com.rabbit.user.service;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.MetamaskWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final MetamaskWalletRepository metamaskWalletRepository;

    /**
     * 사용자의 주 지갑 정보 조회
     */
    public String getUserPrimaryWalletAddress(User user) {
        if (user == null) {
            return null;
        }

        return metamaskWalletRepository.findByUserAndPrimaryFlagTrue(user)
                .map(MetamaskWallet::getWalletAddress)
                .orElse(null);
    }

    /**
     * 사용자 ID로 주 지갑 정보 조회
     */
    public String getUserPrimaryWalletAddressById(Integer userId) {
        if (userId == null) {
            return null;
        }

        return metamaskWalletRepository.findPrimaryWalletAddressByUserId(userId)
                .orElse(null);
    }

    /**
     * 사용자 ID로 지갑 주소 조회
     *
     * @param userId 사용자 ID
     * @return 지갑 주소
     */
    public String getWalletAddress(Integer userId) {
        return getUserPrimaryWalletAddressById(userId);
    }

    /**
     * 사용자 ID로 지갑 Credentials 조회
     *
     * @param userId 사용자 ID
     * @return 지갑 Credentials
     */
    public Credentials getUserCredentials(Integer userId) {
        // 메타마스크 지갑을 사용하는 경우에는 서버 측에서 Credentials를 관리하지 않음
        // 클라이언트 측 서명이 필요한 경우, 클라이언트(메타마스크)에 서명 요청을 전송해야 함
        throw new BusinessException(ErrorCode.FUNCTIONALITY_NOT_SUPPORTED,
                "메타마스크 지갑은 서버 측 서명을 지원하지 않습니다. 클라이언트 측 서명이 필요합니다.");
    }
}
