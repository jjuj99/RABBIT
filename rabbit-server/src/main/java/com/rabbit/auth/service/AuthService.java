package com.rabbit.auth.service;

import com.rabbit.auth.domain.dto.request.LoginRequestDTO;
import com.rabbit.auth.domain.dto.request.NonceRequestDTO;
import com.rabbit.auth.domain.dto.request.SignupRequestDTO;
import com.rabbit.auth.domain.dto.response.CheckDuplicatedResponseDTO;
import com.rabbit.auth.domain.dto.response.NonceResponseDTO;
import com.rabbit.auth.domain.dto.response.RefreshResponseDTO;
import com.rabbit.auth.domain.entity.SsafyAccount;
import com.rabbit.auth.domain.entity.UserToken;
import com.rabbit.auth.repository.SsafyAccountRepository;
import com.rabbit.auth.repository.UserTokenRepository;
import com.rabbit.auth.service.dto.LoginServiceResult;
import com.rabbit.bankApi.service.BankApiService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.util.JwtUtil;
import com.rabbit.global.util.SignatureUtil;
import com.rabbit.global.util.WalletAddressUtil;
import com.rabbit.user.domain.entity.Bank;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.domain.entity.RefundAccount;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.BankRepository;
import com.rabbit.user.repository.MetamaskWalletRepository;
import com.rabbit.user.repository.RefundAccountRepository;
import com.rabbit.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final RefundAccountRepository refundAccountRepository;
    private final MetamaskWalletRepository metamaskWalletRepository;
    private final SsafyAccountRepository ssafyAccountRepository;
    private final BankRepository bankRepository;

    private final BankApiService bankApiService;
    private final JwtUtil jwtUtil;

    public NonceResponseDTO nonce(NonceRequestDTO request) {
        // 전체 스트림을 호출 -> 대소문자 무시하여 지갑 주소 비교
        return metamaskWalletRepository.findByPrimaryFlagTrue().stream()
                .filter(wallet -> {
                    return WalletAddressUtil.compareAddresses(request.getWalletAddress(), wallet.getWalletAddress());
                })
                .findFirst()
                .map(wallet -> NonceResponseDTO.builder()
                        .nonce(SignatureUtil.createNonce())
                        .build()
                )
                .orElseGet(() -> null);
    }

    @Transactional
    public LoginServiceResult login(LoginRequestDTO request) {
        // 지갑 주소로 회원 정보 불러오기
        User user = metamaskWalletRepository.findByPrimaryFlagTrue().stream()
                .filter(wallet -> {
                    return WalletAddressUtil.compareAddresses(request.getWalletAddress(), wallet.getWalletAddress());
                })
                .findFirst()
                .map(MetamaskWallet::getUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 지갑 주소입니다."));

        userTokenRepository.deleteByUser_UserId(user.getUserId());

        String recoverAddress = SignatureUtil.recoverAddress(request.getSignature(), request.getNonce());

        // 서명 검증
        if (!WalletAddressUtil.compareAddresses(request.getWalletAddress(), recoverAddress)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "서명이 일치하지 않습니다.");
        }

        // usreId로 계좌 정보 불러오기
        RefundAccount refundAccount = refundAccountRepository.findByUserIdAndPrimaryFlagTrue(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록된 환불 계좌 정보가 없습니다."));

        // 은행 정보 불러오기
        Bank bank = bankRepository.findById(refundAccount.getBankId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록되지 않은 은행 정보입니다."));

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
                .email(user.getEmail())
                .bank(bank)
                .refundAccount(refundAccount.getAccountNumber())
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

        // 이미 존재하는 닉네임인지 확인
        userRepository.findByNickname(request.getNickname())
                .ifPresent(user -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 등록된 닉네임입니다.");
                });

        // 이미 존재하는 지갑 주소인지 확인
        boolean walletExists = metamaskWalletRepository.findAll().stream()
                .anyMatch(wallet -> {
                    return WalletAddressUtil.compareAddresses(request.getWalletAddress(), wallet.getWalletAddress());
                });
        if (walletExists) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "이미 등록된 지갑 주소입니다.");
        }

        // 싸피 은행 계정 정보 호출
//        SsafyAccount ssafyAccount = ssafyAccountRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "계좌 인증을 다시 시도해주세요."));

//        if (ssafyAccount.getUserKey() == null || ssafyAccount.getAccountNo() == null) {
//            throw new BusinessException(ErrorCode.UNAUTHORIZED, "계좌 인증을 다시 시도해주세요.");
//        }

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
        // 표준 체크섬 주소로 변환
        String checksumAddress = WalletAddressUtil.toChecksumAddress(request.getWalletAddress());
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
    public void logout(int userId) {
        userTokenRepository.deleteByUser_UserId(userId);
    }

    @Transactional(readOnly = true)
    public CheckDuplicatedResponseDTO checkEmail(String email) {
        boolean duplicated = userRepository.existsByEmail(email);

        return CheckDuplicatedResponseDTO.builder()
                .duplicated(duplicated)
                .build();
    }

    @Transactional(readOnly = true)
    public CheckDuplicatedResponseDTO checkNickname(String nickname) {
        boolean duplicated = userRepository.existsByNickname(nickname);

        return CheckDuplicatedResponseDTO.builder()
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

        RefundAccount refundAccount = refundAccountRepository.findByUserIdAndPrimaryFlagTrue(user.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록된 환불 계좌 정보가 없습니다."));

        Bank bank = bankRepository.findById(refundAccount.getBankId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록되지 않은 은행 정보입니다."));

        String accessToken = jwtUtil.createAccessToken(userId);

        return RefreshResponseDTO.builder()
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .bankId(bank.getBankId())
                .bankName(bank.getBankName())
                .refundAccount(refundAccount.getAccountNumber())
                .refundAccount(refundAccount.getAccountNumber())
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
