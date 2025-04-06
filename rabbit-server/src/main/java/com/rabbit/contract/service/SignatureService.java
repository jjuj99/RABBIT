package com.rabbit.contract.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 서명 관련 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureService {

    private final WalletService walletService;

    /**
     * 계약에 대한 사용자 서명 가져오기
     * (실제 구현에서는 DB에서 저장된 서명 조회)
     *
     * @param userId 사용자 ID
     * @param contractId 계약 ID
     * @return 서명 문자열
     */
    public String getSignature(Integer userId, Integer contractId) {
        // 1. 서명 DB에서 조회 시도
        Optional<String> existingSignature = findExistingSignature(userId, contractId);
        if (existingSignature.isPresent()) {
            return existingSignature.get();
        }

        // 메타마스크 환경에서는 서버 측에서 서명을 생성할 수 없음
        // 실제 구현에서는 클라이언트에서 서명을 받아와야 함
        log.warn("서명 정보를 찾을 수 없습니다. userId: {}, contractId: {}", userId, contractId);

        // 개발용 더미 서명 반환 (실제 환경에서는 제거 필요)
        return "0x0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    }

    /**
     * 기존 서명 조회
     *
     * @param userId 사용자 ID
     * @param contractId 계약 ID
     * @return 서명 Optional
     */
    private Optional<String> findExistingSignature(Integer userId, Integer contractId) {
        // 실제 구현에서는 DB에서 서명 조회 로직 구현
        // 여기서는 간단한 예시로 빈 Optional 반환
        return Optional.empty();
    }

    /**
     * 서명 저장
     *
     * @param userId 사용자 ID
     * @param contractId 계약 ID
     * @param signature 서명
     */
    public void saveSignature(Integer userId, Integer contractId, String signature) {
        // 실제 구현에서는 DB에 서명 저장 로직 구현
        log.info("서명 저장 - userId: {}, contractId: {}, signature: {}", userId, contractId, signature);
    }

    /**
     * 서명 검증
     *
     * @param signature 서명
     * @param message 원본 메시지
     * @param address 서명한 지갑 주소
     * @return 유효 여부
     */
    public boolean verifySignature(String signature, String message, String address) {
        try {
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            if (signatureBytes.length != 65) {
                return false;
            }

            byte[] r = new byte[32];
            byte[] s = new byte[32];
            System.arraycopy(signatureBytes, 0, r, 0, 32);
            System.arraycopy(signatureBytes, 32, s, 0, 32);
            byte v = signatureBytes[64];

            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] messageHash = Hash.sha3(messageBytes);

            Sign.SignatureData signatureData = new Sign.SignatureData(new byte[]{v}, r, s);
            BigInteger publicKey = Sign.signedMessageHashToKey(messageHash, signatureData);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            return recoveredAddress.equalsIgnoreCase(address);
        } catch (Exception e) {
            log.error("서명 검증 중 오류가 발생했습니다.", e);
            return false;
        }
    }
}