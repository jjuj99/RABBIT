package com.rabbit.global.code.domain.enums;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * 시스템에서 사용되는 모든 상태 코드를 한 파일에서 관리하는 클래스
 * 이 클래스의 enum 정의가 시스템 전체의 단일 진실 공급원(Single Source of Truth)입니다.
 *
 * [새로운 상태 코드 추가 방법]
 * 1. 해당 코드 유형의 enum에 새 상태 코드 항목을 추가합니다. (예: Auction.NEW_STATUS)
 * 2. 새로운 코드 유형을 추가하려면:
 *    a. 새로운 enum 내부 클래스를 생성하고 SysCommonCodeEnum 인터페이스를 구현합니다.
 *    b. getAllCodeTypes 메서드에 (예: NEW.values()[0].getCodeType())
 *    c. service.impl 패키지에 해당 XXXCodeManager 클래스를 생성합니다.
 *    d. 애플리케이션 재시작 시 자동으로 레지스트리에 등록되고 DB에 동기화됩니다.
 */
@Component
public class SysCommonCodes {

    /**
     * 모든 코드 타입 목록 반환
     */
    public static Set<String> getAllCodeTypes() {
        return Set.of(
                Auction.values()[0].getCodeType(),
                EmailLog.values()[0].getCodeType(),
                PromissoryNote.values()[0].getCodeType(),
                CoinLog.values()[0].getCodeType(),
                Bid.values()[0].getCodeType(),
                Contract.values()[0].getCodeType(), // 계약 상태 코드 추가
                Repayment.values()[0].getCodeType(),
                NotificationType.values()[0].getCodeType(),
                NotificationRelatedType.values()[0].getCodeType()
                // 새 코드 타입 추가 시 여기에 추가
        );
    }

    /**
     * 코드 타입 유효성 검사
     */
    public static boolean isValidCodeType(String codeType) {
        return getAllCodeTypes().contains(codeType);
    }

    // 공통 enum 변환 로직
    private static <T extends Enum<?> & SysCommonCodeEnum> T fromCodeCommon(
            T[] values, String code, String codeType) {
        return Arrays.stream(values)
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CODE_NOT_FOUND, codeType, code));
    }

    /**
     * 경매 상태 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum Auction implements SysCommonCodeEnum {
        ING("진행중", "경매가 활성화되어 입찰을 받는 중", 1),
        COMPLETED("완료", "경매가 종료되어 낙찰이 완료됨", 2),
        FAILED("유찰", "경매가 실패하여 종료됨", 3),
        CANCELED("취소", "경매가 취소됨", 4);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "AUCTION_STATUS";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static Auction fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }

    /**
     * 입찰 상태 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum Bid implements SysCommonCodeEnum {
        PENDING("입찰중", "경매가 진행중인 입찰", 1),
        WON("낙찰", "경매에서 낙찰됨", 2),
        LOST("낙찰 실패", "경매에서 유찰됨", 3);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "BID_STATUS";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static Bid fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }

    /**
     * 계약 상태 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum Contract implements SysCommonCodeEnum {
        REQUESTED("요청", "계약이 요청됨", 1),
        MODIFICATION_REQUESTED("수정 요청됨", "계약 수정이 요청됨", 2),
        CONTRACTED("체결", "계약이 체결됨", 3),
        CANCELED("취소", "계약이 취소됨", 4),
        REJECTED("거절", "계약이 거절됨", 5);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "CONTRACT_STATUS";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static Contract fromCode(String code) {
            return Arrays.stream(values())
                    .filter(status -> status.getCode().equals(code))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.CODE_NOT_FOUND, CODE_TYPE, code));
        }
    }

    /**
     * 이메일 로그 상태 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum EmailLog implements SysCommonCodeEnum {
        PENDING("대기중", "이메일 전송 대기 중", 1),
        SENT("전송 완료", "이메일 전송이 완료됨", 2),
        FAILED("전송 실패", "이메일 전송이 실패함", 3);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "EMAIL_LOG_STATUS";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static EmailLog fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }

    /**
     * 차용증 상태 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum PromissoryNote implements SysCommonCodeEnum {
        DRAFT("초안", "차용증이 초안 상태로 작성됨", 1),
        PENDING("서명 대기중", "차용증 서명이 대기 중", 2),
        CANCELED("취소", "차용증 계약이 취소됨", 3),
        COMPLETED("완료", "차용증 계약이 완료됨", 4);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "PROMISSORY_NOTE_STATUS";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static PromissoryNote fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }

    /**
     * 코인 로그 타입 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum CoinLog implements SysCommonCodeEnum {
        DEPOSIT("입금", "메타마스크 계좌로 입금", 1),
        WITHDRAWAL("출금", "메타마스크 계좌에서 출금", 2);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "COIN_LOG_TYPE";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static CoinLog fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }

    /*
    * 알림 타입
    * */
    @Getter
    @RequiredArgsConstructor
    public enum NotificationType implements SysCommonCodeEnum {

        AUCTION_FAILED("경매 유찰", "경매에 낙찰자가 없어 유찰되었습니다.", 1),
        AUCTION_SUCCESS("경매 낙찰 성공", "경매에 낙찰되었습니다. NFT가 곧 전송됩니다.", 2),
        AUCTION_TRANSFERRED("NFT 전송 예정", "경매가 완료되어 NFT가 낙찰자에게 전송됩니다.", 3),
        BID_FAILED("입찰 실패", "다른 입찰자가 더 높은 금액을 입찰했습니다.", 4),

        // 기존 NotificationType enum에 추가
        CONTRACT_REQUESTED("계약 요청", "새로운 계약이 요청되었습니다.", 5),
        CONTRACT_COMPLETED("계약 체결", "계약이 성공적으로 체결되었습니다.", 6),
        CONTRACT_MODIFICATION_REQUESTED("계약 수정 요청", "계약 수정이 요청되었습니다.", 7),
        CONTRACT_CANCELED("계약 취소", "계약이 취소되었습니다.", 8);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "NOTIFICATION_TYPE";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static NotificationType fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum NotificationRelatedType implements SysCommonCodeEnum {

        AUCTION("경매", "경매 관련 알림", 1),
        CONTRACT("계약", "계약 관련 알림", 2);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "NOTIFICATION_RELATED_TYPE";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static NotificationRelatedType fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }
    }


    /**
     * 상환 방식 타입 코드 열거형
     */
    @Getter
    @RequiredArgsConstructor
    public enum Repayment implements SysCommonCodeEnum {
        EPIP("원리금 균등 상환", "원금과 이자를 매월 동일한 금액으로 상환", 1),
        EPP("원금 균등 상환", "원금을 매월 동일한 금액으로 상환하고 이자는 잔액에 따라 계산", 2),
        BP("만기 일시 상환", "만기일에 원금을 일시 상환하고 이자는 정기적으로 납부", 3);

        private final String codeName;
        private final String description;
        private final int displayOrder;

        private static final String CODE_TYPE = "REPAYMENT_TYPE";

        @Override
        public String getCode() {
            return this.name();
        }

        @Override
        public String getCodeType() {
            return CODE_TYPE;
        }

        public static Repayment fromCode(String code) {
            return fromCodeCommon(values(), code, CODE_TYPE);
        }

        public static Repayment fromCodeEnumName(String codeName) {
            return Arrays.stream(values())
                    .filter(e -> e.getCodeName().equals(codeName))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("유효하지 않은 상환 방식입니다: " + codeName));
        }

        public static String toCalculationType(String repaymentCode) {
            return switch (repaymentCode) {
                case "EPIP" -> "EQUAL_PAYMENT";
                case "EPP"  -> "EQUAL_PRINCIPAL";
                case "BP"   -> "BULLET";
                default     -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원되지 않는 상환 방식입니다: " + repaymentCode);
            };
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum MailTemplateType {

        /**
         * 경매 낙찰 알림 (낙찰자에게)
         */
        AUCTION_SUCCESS_WINNER(
                "[Rabbit] NFT 경매 낙찰을 축하드립니다!",
                "%s님, NFT [%s] 경매에 낙찰되셨습니다.\n곧 NFT가 전송됩니다. 감사합니다."
        ),

        /**
         * 경매 낙찰 알림 (양도인에게)
         */
        AUCTION_SUCCESS_SELLER(
                "[Rabbit] NFT 경매가 완료되었습니다.",
                "%s님, NFT [%s] 경매가 성공적으로 낙찰되었습니다.\nNFT는 곧 낙찰자에게 전송됩니다."
        ),

        /**
         * NFT 양도 완료 통지
         */
        TRANSFER_NOTIFY_DEBTOR(
                "[Rabbit] 채권 양도 통지서",
                """
                %s님께,
    
                귀하의 대출과 관련된 채권이 기존 채권자 [%s]로부터 새로운 채권자 [%s]에게 양도되었음을 알려드립니다.
    
                - 채권 ID: %s
                - 만기일: %s
                - 기존 채권자: %s
                - 신규 채권자: %s
    
                해당 채권에 대한 권리는 이제 새로운 채권자에게 있으며,
                상환 및 기타 문의는 새로운 채권자를 통해 진행해 주시기 바랍니다.
    
                감사합니다.
                Rabbit 팀 드림.
                """
        ),

        /**
         * 계약 요청 알림 (채권자에게)
         */
        CONTRACT_REQUESTED(
                "[Rabbit] 새로운 계약 요청이 도착했습니다",
                """
                %s님으로부터 새로운 계약 요청이 도착했습니다.
                
                - 계약 ID: %s
                - 요청 금액: %s
                
                웹사이트에서 계약 내용을 확인하고 승인하거나 수정을 요청할 수 있습니다.
                
                감사합니다.
                Rabbit 팀 드림.
                """
        ),

        /**
         * 계약 체결 완료 알림
         */
        CONTRACT_COMPLETED(
                "[Rabbit] 계약 체결이 완료되었습니다",
                """
                %s님과의 계약(#%s)이 성공적으로 체결되었습니다.
                
                첨부된 PDF 파일에서 계약 내용을 확인하실 수 있습니다.
                또한 웹사이트에서도 계약 내용을 확인하실 수 있습니다.
                
                추가 문의사항이 있으시면 고객센터로 연락해 주세요.
                
                감사합니다.
                Rabbit 팀 드림.
                """
        ),

        /**
         * 계약 수정 요청 알림
         */
        CONTRACT_MODIFICATION_REQUESTED(
                "[Rabbit] 계약 수정 요청",
                """
                %s님이 계약(#%s)의 수정을 요청하였습니다.
                
                요청 사유: %s
                
                웹사이트에서 계약 내용을 수정하여 다시 요청해 주세요.
                
                감사합니다.
                Rabbit 팀 드림.
                """
        ),

        /**
         * 계약 취소 알림
         */
        CONTRACT_CANCELED(
                "[Rabbit] 계약이 취소되었습니다",
                """
                %s님과의 계약(#%s)이 취소되었습니다.
                
                웹사이트에서 자세한 내용을 확인하실 수 있습니다.
                
                감사합니다.
                Rabbit 팀 드림.
                """
        ),

        /**
         * 계약 관련 문제 발생 시 폴백 메일
         */
        CONTRACT_FALLBACK(
                "[Rabbit] 계약 알림",
                """
                %s
                
                PDF 첨부 이메일 발송에 실패하였습니다. 웹사이트에서 계약서를 확인해주세요.
                
                감사합니다.
                Rabbit 팀 드림.
                """
        );

        private final String subject;
        private final String bodyFormat;

        public String buildBody(Object... args) {
            return String.format(bodyFormat, args);
        }
    }


}