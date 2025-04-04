package com.rabbit.global.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =========== Common ===========
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "error.invalid.input", "유효하지 않은 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "error.invalid.type", "올바르지 않은 타입입니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.resource.not.found", "리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.server", "서버 오류가 발생했습니다"),

    // =========== Auth ===========
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.auth.unauthorized", "인증이 필요합니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "error.auth.access.denied", "접근 권한이 없습니다"),

    // =========== Metamask Wallet Address ===========
    INVALID_WALLET_ADDRESS(HttpStatus.BAD_REQUEST, "error.wallet.address.invalid", "유효하지 않은 지갑 주소 형식입니다"),

    // =========== JWT ===========
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "error.jwt.expired", "토큰이 만료되었습니다"),
    JWT_INVALID(HttpStatus.FORBIDDEN, "error.jwt.invalid", "유효하지 않은 토큰입니다"),
    JWT_REQUIRED(HttpStatus.UNAUTHORIZED, "error.jwt.required", "로그인 후 이용해주세요"),

    // =========== Business ===========
    EXAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.example.not.found", "예시 항목을 찾을 수 없습니다"),
    INVALID_PAGE_PARAMETERS(HttpStatus.BAD_REQUEST, "error.invalid.page", "유효하지 않은 페이지 파라미터입니다"),

    // =========== Common Code ===========
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.code.not.found", "코드를 찾을 수 없습니다: 타입={0}, 코드={1}"),
    CODE_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.code.already.exists", "이미 존재하는 코드입니다: 타입={0}, 코드={1}"),
    CODE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "error.code.type.invalid", "유효하지 않은 코드 타입입니다: {0}"),

    ALREADY_EXISTS(HttpStatus.CONFLICT, "error.already.exists", "이미 존재하는 리소스입니다: {0}"),
    BUSINESS_LOGIC_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "error.business", "처리할 수 없는 요청입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "error.method.not.allowed", "지원하지 않는 HTTP 메소드입니다"),

    BLOCKCHAIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.blockchain", "블록체인 처리 중 오류가 발생했습니다"),

    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "error.file.upload.failed", "파일 업로드 중 오류가 발생했습니다");

    private final HttpStatus status;
    private final String messageKey;
    private final String defaultMessage;

    /**
     * 기본 메시지를 반환합니다 (기존 코드와의 호환성 유지).
     * @return 기본 오류 메시지
     */
    public String getMessage() {
        return defaultMessage;
    }

    /**
     * MessageSource를 통해 국제화된 메시지를 반환합니다.
     * @param messageSource 메시지 소스
     * @param args 메시지 파라미터
     * @return 국제화된 메시지
     */
    public String getMessage(MessageSource messageSource, Object... args) {
        return messageSource.getMessage(
                messageKey,
                args,
                defaultMessage,
                LocaleContextHolder.getLocale()
        );
    }
}