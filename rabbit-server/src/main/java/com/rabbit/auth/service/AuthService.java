package com.rabbit.auth.service;

import com.rabbit.auth.domain.dto.request.*;
import com.rabbit.auth.domain.dto.response.*;
import com.rabbit.auth.domain.entity.UserToken;
import com.rabbit.auth.repository.UserTokenRepository;
import com.rabbit.auth.service.dto.LoginServiceResult;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.util.JwtUtil;
import com.rabbit.global.util.SignatureUtil;
import com.rabbit.user.domain.entity.*;
import com.rabbit.user.repository.MetamaskWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MetamaskWalletRepository metamaskWalletRepository;
    private final UserTokenRepository userTokenRepository;

    private final SignatureUtil signatureUtil;
    private final JwtUtil jwtUtil;

    public NonceResponseDTO nonce(NonceRequestDTO request) {
        // 존재하는 회원인지 확인
        return metamaskWalletRepository.findByWalletAddress(request.getWalletAddress())
                .map(wallet -> NonceResponseDTO.builder()
                            .nonce(createNonce()) // 난수 생성
                            .build()
                )
                .orElseGet(() -> null);
    }

    private String createNonce() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @Transactional
    public LoginServiceResult login(LoginRequestDTO request) {
        // 지갑 주소로 회원 정보 불러오기
        User user = metamaskWalletRepository.findByWalletAddress(request.getWalletAddress())
                .map(MetamaskWallet::getUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TYPE_VALUE, "존재하지 않는 지갑 주소입니다."));

        // 서명에서 주소 복원
        String recoverAddress = signatureUtil.recoverAddress(request.getSignature(), request.getNonce());

        // 서명 검증
        if (!request.getWalletAddress().equalsIgnoreCase(recoverAddress)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "서명이 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.createRefreshToken(String.valueOf(user.getUserId()));
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getUserId()));

        UserToken userToken = UserToken.builder()
                .userId(user.getUserId())
                .refreshToken(refreshToken)
                .createdAt(ZonedDateTime.now())
                .build();
        userTokenRepository.save(userToken);

        return LoginServiceResult.builder()
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
