package com.rabbit.global.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =========== Common ===========
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 타입입니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),

    // =========== Auth ===========
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),

    // =========== Business ===========
    EXAMPLE_NOT_FOUND(HttpStatus.NOT_FOUND, "예시 항목을 찾을 수 없습니다"),
    INVALID_PAGE_PARAMETERS(HttpStatus.BAD_REQUEST, "유효하지 않은 페이지 파라미터입니다");

    private final HttpStatus status;
    private final String message;
}