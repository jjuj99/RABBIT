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
import com.rabbit.user.repository.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final RefundAccountRepository refundAccountRepository;
    private final MetamaskWalletRepository metamaskWalletRepository;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 지갑 주소입니다."));

        // 서명에서 주소 복원
//        String recoverAddress = signatureUtil.recoverAddress(request.getSignature(), request.getNonce());
//
//        // 서명 검증
//        if (!request.getWalletAddress().equalsIgnoreCase(recoverAddress)) {
//            throw new BusinessException(ErrorCode.UNAUTHORIZED, "서명이 일치하지 않습니다.");
//        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(String.valueOf(user.getUserId()));
        String refreshToken = jwtUtil.createRefreshToken(String.valueOf(user.getUserId()));

        UserToken userToken = UserToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .createdAt(ZonedDateTime.now())
                .build();
        userTokenRepository.save(userToken);

        return LoginServiceResult.builder()
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void signup(SignupRequestDTO request) {
        // 이미 존재하는 이메일인지 확인
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 등록된 이메일입니다.");
                });

        // 이미 존재하는 지갑 주소인지 확인
        metamaskWalletRepository.findByWalletAddress(request.getWalletAddress())
                .ifPresent(metamaskWallet -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 등록된 지갑 주소입니다.");
                });

        // 유저 엔티티 생성
        User user = userRepository.save(User.builder()
                .email(request.getEmail())
                .userName(request.getUserName())
                .nickname(request.getNickname())
                .passCode("Temp_Pass_Code") // 임시 데이터 저장
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .withdrawnFlag(false)
                .build()
        );

        // 환불 계좌 엔티티 생성
        refundAccountRepository.save(RefundAccount.builder()
                .userId(user.getUserId())
                .bankId(request.getBankId())
                .accountNumber(request.getRefundAccount())
                .primaryFlag(true)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build()
        );

        // 메타마스크 지갑 엔티티 생성
        metamaskWalletRepository.save(MetamaskWallet.builder()
                .user(user)
                .walletAddress(request.getWalletAddress())
                .primaryFlag(true)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build()
        );
    }

    @Transactional
    public RefreshResponseDTO refresh(String refreshToken) {
        String userId = parseUserId(refreshToken);

        UserToken userToken = userTokenRepository.findByUser_UserId(Integer.parseInt(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.JWT_INVALID));

        if (!refreshToken.equals(userToken.getRefreshToken())) {
            throw new BusinessException(ErrorCode.JWT_INVALID);
        }

        User user = userToken.getUser();
        String accessToken = jwtUtil.createAccessToken(String.valueOf(user.getUserId()));

        return RefreshResponseDTO.builder()
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .build();
    }

    private String parseUserId(String token) {
        try {
            return jwtUtil.getUserIdFromToken(token);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            throw  new BusinessException(ErrorCode.JWT_EXPIRED);
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e) {
            // 유효하지 않은 토큰 (구조 오류, 서명 오류 등)
            throw  new BusinessException(ErrorCode.JWT_INVALID);
        }
    }
}
