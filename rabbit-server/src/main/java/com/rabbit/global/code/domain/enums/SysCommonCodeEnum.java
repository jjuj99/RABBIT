package com.rabbit.global.code.domain.enums;

/**
 * 모든 상태 코드 enum이 구현해야 하는 공통 인터페이스
 */
public interface SysCommonCodeEnum {
    String getCode();
    String getCodeName();
    String getDescription();
    int getDisplayOrder();
    String getCodeType();
}