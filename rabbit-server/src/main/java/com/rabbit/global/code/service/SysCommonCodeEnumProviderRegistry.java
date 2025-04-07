package com.rabbit.global.code.service;

import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * StatusCodeEnumProvider 인스턴스들을 관리하는 레지스트리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysCommonCodeEnumProviderRegistry {

    private final List<SysCommonCodeEnumProvider> providers;
    private final Map<String, SysCommonCodeEnumProvider> providerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        providerMap.clear();

        for (SysCommonCodeEnumProvider provider : providers) {
            String codeType = provider.getCodeType();
            providerMap.put(codeType, provider);
            log.info("코드 enum 제공자 등록: {}", codeType);
        }
    }

    /**
     * 지정된 코드 타입에 대한 제공자 반환
     * @param codeType 코드 타입
     * @return StatusCodeEnumProvider 인스턴스
     * @throws BusinessException 유효하지 않은 코드 타입인 경우
     */
    public SysCommonCodeEnumProvider getProvider(String codeType) {
        SysCommonCodeEnumProvider provider = providerMap.get(codeType);

        if (provider == null) {
            throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, codeType);
        }

        return provider;
    }

    /**
     * 지원하는 모든 코드 타입 반환
     * @return 코드 타입 집합
     */
    public Set<String> getSupportedCodeTypes() {
        return providerMap.keySet();
    }

    /**
     * 코드 타입 유효성 검사
     * @param codeType 코드 타입
     * @return 유효성 여부
     */
    public boolean isValidCodeType(String codeType) {
        return providerMap.containsKey(codeType);
    }
}