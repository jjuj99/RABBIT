package com.rabbit.contract.service;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.user.domain.entity.User;

import lombok.extern.slf4j.Slf4j;

/**
 * 해시 생성 서비스 - Keccak-256(이더리움 표준) 사용
 */
@Slf4j
@Service
public class HashingService {

    /**
     * 사용자 정보를 해싱하여 반환
     *
     * @param user 사용자 객체
     * @return 사용자 정보 해시값
     */
    public String hashUserInfo(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "사용자 정보가 없습니다.");
        }

        // 사용자 주요 식별 정보를 조합하여 해시
        String userInfo = String.format("%d:%s:%s",
                user.getUserId(),
                user.getUserName(),
                user.getEmail());

        return hashText(userInfo);
    }

    /**
     * 텍스트를 해싱하여 반환 (Keccak-256 사용 - 이더리움 표준)
     *
     * @param text 해싱할 텍스트
     * @return 해시값 (16진수 문자열, 0x 접두사 포함)
     */
    public String hashText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        try {
            // web3j의 Hash 클래스를 사용해 Keccak-256 해시 수행
            String hash = Hash.sha3String(text);

            // 이더리움 스타일 접두사 추가 (Hash.sha3String은 0x 접두사를 포함하지 않음)
            return "0x" + hash;
        } catch (Exception e) {
            log.error("해시 생성 중 오류가 발생했습니다.", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "해시 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 솔트를 적용한 해시 생성 (추가 보안이 필요한 경우)
     *
     * @param text 해싱할 텍스트
     * @param salt 적용할 솔트
     * @return 해시값 (16진수 문자열, 0x 접두사 포함)
     */
    public String hashTextWithSalt(String text, String salt) {
        if (text == null) {
            return "";
        }

        String saltedText = text + salt;
        return hashText(saltedText);
    }
}