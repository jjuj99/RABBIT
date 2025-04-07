package com.rabbit.global.code.service;

import com.rabbit.global.code.domain.entity.SysCommonCode;
import com.rabbit.global.code.domain.enums.SysCommonCodeEnum;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 열거형 기반 상태 코드 관리 추상 클래스
 * 초기화(CodeInitializer)와 제공(StatusCodeEnumProvider) 역할을 모두 수행
 */
public abstract class SysCommonCodeEnumManager<T extends Enum<?> & SysCommonCodeEnum>
        implements SysCommonCodeDataProvider, SysCommonCodeEnumProvider {

    /**
     * Enum 값 목록 반환 (내부용)
     */
    protected abstract List<T> retrieveEnumValues();

    @Override
    public String getCodeType() {
        // enum 값의 첫 번째 요소로부터 코드 타입을 가져옴
        return retrieveEnumValues().get(0).getCodeType();
    }

    @Override
    public List<SysCommonCode> getDefaultCodes() {
        return retrieveEnumValues().stream()
                .map(this::buildCommonCode)
                .collect(Collectors.toList());
    }

    /**
     * StatusCodeEnumProvider 구현
     * enum 값을 StatusCodeEnum 배열로 변환하여 반환
     */
    @Override
    public SysCommonCodeEnum[] getEnumValues() {
        List<T> values = retrieveEnumValues();
        return values.toArray(new SysCommonCodeEnum[0]);
    }

    /**
     * Enum 값으로부터 CommonCode 객체 생성
     */
    protected SysCommonCode buildCommonCode(T enumValue) {
        return SysCommonCode.builder()
                .codeType(enumValue.getCodeType())
                .code(enumValue.getCode())
                .codeName(enumValue.getCodeName())
                .description(enumValue.getDescription())
                .displayOrder(enumValue.getDisplayOrder())
                .activeFlag(true)
                .build();
    }
}