package com.rabbit.global.service;

import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 계약별 고유 암호화 키 관리 및 암호화/복호화 서비스
 * Contract 엔티티에 직접 암호화 키를 저장하는 방식 사용
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractEncryptionService {

    private final ContractRepository contractRepository;

    // 마스터 키 - 계약 키를 보호하는 데 사용
    @Value("${app.encryption.master-key:DEFAULT_MASTER_KEY_SHOULD_BE_CHANGED}")
    private String masterKeyString;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final SecureRandom secureRandom = new SecureRandom();

    // 현재 키 버전 (키 순환 대비)
    private static final int CURRENT_KEY_VERSION = 1;

    /**
     * 계약에 대한 새로운 암호화 키 생성 및 저장
     * @param contract 계약 엔티티
     * @return 생성된 키의 Base64 인코딩된 문자열
     */
    @Transactional
    public String generateAndStoreKeyForContract(Contract contract) {
        try {
            // 새로운 AES-256 키 생성
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            String contractKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            // 계약 키를 마스터 키로 암호화
            String encryptedContractKey = encryptWithMasterKey(contractKey);

            // 계약 엔티티에 저장
            contract.setEncryptionKey(encryptedContractKey, CURRENT_KEY_VERSION);
            contractRepository.save(contract);

            log.info("계약 ID {}에 대한 새 암호화 키 생성 및 저장 완료", contract.getContractId());

            return contractKey;
        } catch (NoSuchAlgorithmException e) {
            log.error("계약 ID {}에 대한 암호화 키 생성 중 오류 발생", contract.getContractId(), e);
            throw new RuntimeException("암호화 키 생성 중 오류가 발생했습니다", e);
        }
    }

    /**
     * 계약의 암호화 키 조회
     * @param contract 계약 엔티티
     * @return 복호화된 계약 키
     */
    @Transactional(readOnly = true)
    public String getContractKey(Contract contract) {
        String encryptedKey = contract.getEncryptedContractKey();

        if (encryptedKey != null) {
            // 마스터 키로 복호화
            return decryptWithMasterKey(encryptedKey);
        } else {
            log.error("계약 ID {}에 대한 암호화 키가 없음", contract.getContractId());
            throw new RuntimeException("계약에 대한 암호화 키를 찾을 수 없습니다");
        }
    }

    /**
     * 계약의 키를 사용하여 데이터 암호화
     * @param contract 계약 엔티티
     * @param plainText 암호화할 평문
     * @return 암호화된 데이터 (Base64 인코딩)
     */
    public String encryptWithContractKey(Contract contract, String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }

        try {
            // 계약 키 조회 또는 새로 생성
            String contractKey;

            if (contract.getEncryptedContractKey() == null) {
                // 키가 없는 경우 새로 생성
                contractKey = generateAndStoreKeyForContract(contract);
                log.info("계약 ID {}에 대한 암호화 키가 없어 새로 생성", contract.getContractId());
            } else {
                // 기존 키 사용
                contractKey = getContractKey(contract);
            }

            // 키를 사용하여 암호화
            SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(contractKey), "AES");

            // IV 생성
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // 암호화
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // IV와 암호화된 데이터 결합
            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("계약 ID {}의 키로 암호화 중 오류 발생", contract.getContractId(), e);
            return "ENC_ERROR";
        }
    }

    /**
     * 계약의 키를 사용하여 데이터 복호화
     * @param contract 계약 엔티티
     * @param encryptedText 암호화된 데이터 (Base64 인코딩)
     * @return 복호화된 평문
     */
    public String decryptWithContractKey(Contract contract, String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty() || encryptedText.equals("ENC_ERROR")) {
            return null;
        }

        try {
            // 계약 키 조회
            String contractKey = getContractKey(contract);
            SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(contractKey), "AES");

            // Base64 디코딩
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // IV 추출
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // 암호화된 데이터 추출
            byte[] encryptedData = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encryptedData, 0, encryptedData.length);

            // 복호화
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("계약 ID {}의 키로 복호화 중 오류 발생", contract.getContractId(), e);
            return "DEC_ERROR";
        }
    }

    /**
     * 암호화된 데이터를 식별 가능하도록 프리픽스 추가
     * @param contract 계약 엔티티
     * @param plainText 원본 텍스트
     * @return 암호화된 텍스트 (프리픽스 포함)
     */
    public String encryptWithPrefix(Contract contract, String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return "N/A";
        }
        return "ENC:" + encryptWithContractKey(contract, plainText);
    }

    /**
     * 암호화 프리픽스 검사 및 제거 후 복호화
     * @param contract 계약 엔티티
     * @param text 암호화된 텍스트 (프리픽스 포함)
     * @return 복호화된 텍스트
     */
    public String decryptWithPrefix(Contract contract, String text) {
        if (text == null || text.isEmpty() || text.equals("N/A")) {
            return null;
        }

        if (text.startsWith("ENC:")) {
            return decryptWithContractKey(contract, text.substring(4));
        }

        return text; // 이미 암호화되지 않은 경우
    }

    // 마스터 키를 사용한 암호화/복호화 메서드
    private String encryptWithMasterKey(String plainText) {
        try {
            SecretKey secretKey = new SecretKeySpec(
                    masterKeyString.getBytes(StandardCharsets.UTF_8), "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("마스터 키로 암호화 중 오류 발생", e);
            throw new RuntimeException("마스터 키로 암호화 중 오류 발생", e);
        }
    }

    private String decryptWithMasterKey(String encryptedText) {
        try {
            SecretKey secretKey = new SecretKeySpec(
                    masterKeyString.getBytes(StandardCharsets.UTF_8), "AES");

            byte[] combined = Base64.getDecoder().decode(encryptedText);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            byte[] encryptedData = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encryptedData, 0, encryptedData.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("마스터 키로 복호화 중 오류 발생", e);
            throw new RuntimeException("마스터 키로 복호화 중 오류 발생", e);
        }
    }

    /**
     * 계약 ID로 암호화 키를 관리하는 편의 메서드 (ID만 아는 경우 사용)
     * @param contractId 계약 ID
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열
     */
    @Transactional
    public String encryptWithContractId(Integer contractId, String plainText) {
        Contract contract = contractRepository.findByContractIdAndDeletedFlagFalse(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다. ID: " + contractId));

        return encryptWithContractKey(contract, plainText);
    }

    /**
     * 계약 ID로 복호화하는 편의 메서드 (ID만 아는 경우 사용)
     * @param contractId 계약 ID
     * @param encryptedText 암호화된 문자열
     * @return 복호화된 평문
     */
    @Transactional(readOnly = true)
    public String decryptWithContractId(Integer contractId, String encryptedText) {
        Contract contract = contractRepository.findByContractIdAndDeletedFlagFalse(contractId)
                .orElseThrow(() -> new RuntimeException("계약을 찾을 수 없습니다. ID: " + contractId));

        return decryptWithContractKey(contract, encryptedText);
    }
}