package com.rabbit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
}