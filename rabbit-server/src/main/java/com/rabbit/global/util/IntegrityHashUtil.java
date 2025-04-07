package com.rabbit.global.util;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class IntegrityHashUtil {

    private static final String DELIMITER = "|"; // 구분자, 반드시 고정
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * 이름, 이메일, 메타마스크 주소를 해싱하여 무결성 해시값 생성
     * @param name 이름
     * @param email 이메일
     * @param walletAddress 메타마스크 지갑 주소
     * @return base64 인코딩된 SHA-256 해시값
     */
    public static String generateIntegrityHash(String name, String email, String walletAddress) {
        String raw = name + DELIMITER + email + DELIMITER + walletAddress;
        return hash(raw);
    }

    /**
     * 블록체인에 저장된 해시와 현재 입력값 기반 해시를 비교하여 무결성 검증
     * @param originalHash 블록체인 등 저장된 해시
     * @param name 이름
     * @param email 이메일
     * @param walletAddress 메타마스크 지갑 주소
     * @return true = 무결성 검증 통과, false = 위변조 가능성
     */
    public static boolean verifyIntegrity(String originalHash, String name, String email, String walletAddress) {
        String currentHash = generateIntegrityHash(name, email, walletAddress);
        return currentHash.equals(originalHash);
    }

    /**
     * 내부 해시 생성 함수 (SHA-256 + Base64 인코딩)
     */
    private static String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes); // or Hex.encodeHexString() for hex
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "해시 알고리즘이 지원되지 않습니다: " + HASH_ALGORITHM);
        }
    }
}