package com.rabbit.auth.service;

import com.rabbit.auth.domain.dto.request.LoginRequestDTO;
import com.rabbit.auth.domain.dto.request.NonceRequestDTO;
import com.rabbit.auth.domain.dto.request.SignupRequestDTO;
import com.rabbit.auth.domain.dto.response.CheckNicknameResponseDTO;
import com.rabbit.auth.domain.dto.response.NonceResponseDTO;
import com.rabbit.auth.domain.dto.response.RefreshResponseDTO;
import com.rabbit.auth.domain.entity.UserToken;
import com.rabbit.auth.repository.UserTokenRepository;
import com.rabbit.auth.service.dto.LoginServiceResult;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.util.JwtUtil;
import com.rabbit.global.util.SignatureUtil;
import com.rabbit.global.util.WalletAddressUtil;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.RefundAccount;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.MetamaskWalletRepository;
import com.rabbit.user.repository.RefundAccountRepository;
import com.rabbit.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.ZonedDateTime;

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
        // 표준 체크섬 주소로 변환
        String checksumAddress = toChecksumAddress(request.getWalletAddress());

        // 존재하는 회원인지 확인
        return metamaskWalletRepository.findByWalletAddress(checksumAddress)
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
        // 표준 체크섬 주소로 변환
        String checksumAddress = toChecksumAddress(request.getWalletAddress());

        // 지갑 주소로 회원 정보 불러오기
        User user = metamaskWalletRepository.findByWalletAddress(checksumAddress)
                .map(MetamaskWallet::getUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 지갑 주소입니다."));

        // 서명에서 주소 복원
        String recoverAddress = signatureUtil.recoverAddress(request.getSignature(), request.getNonce());

        // 서명 검증
        if (!WalletAddressUtil.compareAddresses(checksumAddress, recoverAddress)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "서명이 일치하지 않습니다.");
        }

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

    private String toChecksumAddress(String walletAddress) {
        // 입력된 지갑 주소가 유효한지 확인
        if (WalletAddressUtil.isValidAddress(walletAddress)) {
            throw new BusinessException(ErrorCode.INVALID_WALLET_ADDRESS);
        }

        // 표준 체크섬 주소로 변환한 주소 반환
        return WalletAddressUtil.toChecksumAddress(walletAddress);
    }

    @Transactional
    public void signup(SignupRequestDTO request) {
        // 이미 존재하는 이메일인지 확인
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 등록된 이메일입니다.");
                });

        // 이미 존재하는 닉네임인지 확인
        userRepository.findByNickname(request.getNickname())
                .ifPresent(user -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 등록된 닉네임입니다.");
                });

        // 표준 체크섬 주소로 변환
        String checksumAddress = toChecksumAddress(request.getWalletAddress());

        // 이미 존재하는 지갑 주소인지 확인
        metamaskWalletRepository.findByWalletAddress(checksumAddress)
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
                .walletAddress(checksumAddress)
                .primaryFlag(true)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        String userId = jwtUtil.getUserIdFromToken(refreshToken);

        userTokenRepository.deleteByUser_UserId(Integer.parseInt(userId));
    }

    @Transactional(readOnly = true)
    public CheckNicknameResponseDTO checkNickname(String nickname) {
        boolean duplicated = userRepository.existsByNickname(nickname);

        return CheckNicknameResponseDTO.builder()
                .duplicated(duplicated)
                .build();
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
