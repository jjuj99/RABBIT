package com.rabbit.global.code.service;

import com.rabbit.global.code.domain.enums.SysCommonCodeEnum;

/**
 * 코드 타입에 대한 enum 값 배열을 제공하는 인터페이스
 */
public interface SysCommonCodeEnumProvider {

    /**
     * 지원하는 코드 타입 반환
     * @return 코드 타입 문자열
     */
    String getCodeType();

    /**
     * 코드 타입에 해당하는 StatusCodeEnum 배열 반환
     * @return StatusCodeEnum 배열
     */
    SysCommonCodeEnum[] getEnumValues();
}