package com.rabbit.global.code.service;

import com.rabbit.global.code.domain.entity.SysCommonCode;

import java.util.List;

/**
 * 특정 코드 타입에 대한 초기 코드 값을 제공하는 인터페이스
 */
public interface SysCommonCodeDataProvider {

    /**
     * 코드 타입 반환
     * @return 코드 타입
     */
    String getCodeType();

    /**
     * 기본 코드 목록 반환
     * @return 기본 코드 목록
     */
    List<SysCommonCode> getDefaultCodes();
}