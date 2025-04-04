package com.rabbit.user.service;

import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.MetamaskWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
