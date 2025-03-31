package com.rabbit.global.util;

import com.rabbit.global.code.domain.entity.SysCommonCode;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;

/**
 * 상태 열거형 관련 유틸리티 클래스
 * @deprecated 추후 필요하다면 가공 예정
 */
public class StatusCodeUtils {

    /**
     * 코드 문자열로부터 경매 상태 열거형 조회
     */
    public static SysCommonCodes.Auction getAuctionStatus(String code) {
        return SysCommonCodes.Auction.fromCode(code);
    }

    /**
     * 코드 문자열로부터 이메일 로그 상태 열거형 조회
     */
    public static SysCommonCodes.EmailLog getEmailLogStatus(String code) {
        return SysCommonCodes.EmailLog.fromCode(code);
    }

    /**
     * 코드 문자열로부터 차용증 상태 열거형 조회
     */
    public static SysCommonCodes.PromissoryNote getPromissoryNoteStatus(String code) {
        return SysCommonCodes.PromissoryNote.fromCode(code);
    }

    /**
     * 코드 문자열로부터 코인 로그 타입 열거형 조회
     */
    public static SysCommonCodes.CoinLog getCoinLogType(String code) {
        return SysCommonCodes.CoinLog.fromCode(code);
    }

    /**
     * CommonCode 객체로부터 적절한 enum 타입으로 변환
     */
    public static <T extends Enum<?>> T getEnumFromCommonCode(SysCommonCode sysCommonCode) {
        String codeType = sysCommonCode.getCodeType();
        String code = sysCommonCode.getCode();

        switch (codeType) {
            case "AUCTION_STATUS":
                return (T) SysCommonCodes.Auction.fromCode(code);
            case "EMAIL_LOG_STATUS":
                return (T) SysCommonCodes.EmailLog.fromCode(code);
            case "PROMISSORY_NOTE_STATUS":
                return (T) SysCommonCodes.PromissoryNote.fromCode(code);
            case "COIN_LOG_TYPE":
                return (T) SysCommonCodes.CoinLog.fromCode(code);
            default:
                throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, codeType);
        }
    }

    /**
     * 특정 타입의 CommonCode 변환 (타입 안전성 보장)
     */
    public static SysCommonCodes.Auction getAuctionStatusFromCommonCode(SysCommonCode sysCommonCode) {
        validateCodeType(sysCommonCode, "AUCTION_STATUS");
        return SysCommonCodes.Auction.fromCode(sysCommonCode.getCode());
    }

    /**
     * 특정 타입의 CommonCode 변환 (타입 안전성 보장)
     */
    public static SysCommonCodes.EmailLog getEmailLogStatusFromCommonCode(SysCommonCode sysCommonCode) {
        validateCodeType(sysCommonCode, "EMAIL_LOG_STATUS");
        return SysCommonCodes.EmailLog.fromCode(sysCommonCode.getCode());
    }

    /**
     * 특정 타입의 CommonCode 변환 (타입 안전성 보장)
     */
    public static SysCommonCodes.PromissoryNote getPromissoryNoteStatusFromCommonCode(SysCommonCode sysCommonCode) {
        validateCodeType(sysCommonCode, "PROMISSORY_NOTE_STATUS");
        return SysCommonCodes.PromissoryNote.fromCode(sysCommonCode.getCode());
    }

    /**
     * 특정 타입의 CommonCode 변환 (타입 안전성 보장)
     */
    public static SysCommonCodes.CoinLog getCoinLogTypeFromCommonCode(SysCommonCode sysCommonCode) {
        validateCodeType(sysCommonCode, "COIN_LOG_TYPE");
        return SysCommonCodes.CoinLog.fromCode(sysCommonCode.getCode());
    }

    /**
     * 코드 타입 검증 로직
     */
    private static void validateCodeType(SysCommonCode sysCommonCode, String expectedType) {
        if (!sysCommonCode.getCodeType().equals(expectedType)) {
            throw new BusinessException(
                    ErrorCode.CODE_TYPE_INVALID,
                    "코드 타입이 " + expectedType + "가 아닙니다: " + sysCommonCode.getCodeType());
        }
    }
}