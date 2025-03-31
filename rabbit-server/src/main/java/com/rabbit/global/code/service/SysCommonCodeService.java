package com.rabbit.global.code.service;

import com.rabbit.global.code.domain.dto.response.CommonCodeResponseDTO;
import com.rabbit.global.code.domain.entity.SysCommonCode;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.domain.enums.SysCommonCodeEnum;
import com.rabbit.global.code.repository.SysCommonCodeRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 시스템 공통 코드 서비스
 * enum 상수를 기반으로 하는 애플리케이션 로직과 UI에 표시할 국제화된 메시지를 연결합니다.
 * DB 접근 없이 enum에서 직접 데이터를 가져옵니다.
 * DB와 enum 간의 동기화를 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysCommonCodeService {

    private final MessageSource messageSource;
    private final SysCommonCodeEnumProviderRegistry sysCommonCodeEnumProviderRegistry;
    private final SysCommonCodeRepository sysCommonCodeRepository;
    private final SysCommonCodes sysCommonCodes;

    /**
     * 코드 메시지 키 형식
     */
    private static final String CODE_MESSAGE_KEY_FORMAT = "code.%s.%s";

    /**
     * 애플리케이션 시작 시 실행되어 enum과 DB 코드를 동기화합니다.
     * enum이 시스템의 단일 진실 공급원이므로, 모든 enum 값이 DB에 정확히 반영되도록 합니다.
     */
    @PostConstruct
    public void initSysCommonCodesBootstrap() {
        try {
            log.info("시스템 코드 초기화 프로세스 시작...");
            // 애플리케이션 시작 시 비동기로 초기화 처리
            syncSysCommonCodesAsync();
        } catch (Exception e) {
            log.error("시스템 코드 초기화 중 오류 발생", e);
        }
    }

    /**
     * 모든 시스템 코드 타입의 동기화를 비동기적으로 수행합니다.
     */
    @Async
    public CompletableFuture<Void> syncSysCommonCodesAsync() {
        try {
            log.info("시스템 코드 비동기 동기화 시작...");
            syncAllSysCommonCodes();
            log.info("시스템 코드 비동기 동기화 성공적으로 완료");
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("시스템 코드 비동기 동기화 실패", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 모든 시스템 코드 타입의 동기화를 수행합니다.
     * 전체 과정이 단일 트랜잭션으로 처리됩니다.
     */
    @Transactional
    public void syncAllSysCommonCodes() {
        try {
            log.info("시스템 코드 동기화 시작...");

            // 모든 시스템 코드 타입에 대해 동기화 수행
            sysCommonCodes.getAllCodeTypes().forEach(this::syncSysCommonCodeType);

            log.info("시스템 코드 동기화 완료");
        } catch (Exception e) {
            log.error("시스템 코드 동기화 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 다시 던지기
        }
    }

    /**
     * 특정 시스템 코드 타입의 동기화를 수행합니다.
     *
     * @param codeType 코드 타입
     */
    @Transactional
    public void syncSysCommonCodeType(String codeType) {
        try {
            log.info("시스템 코드 타입 [{}] 동기화 중", codeType);

            // 해당 코드 타입의 Provider 가져오기
            SysCommonCodeEnumProvider provider = sysCommonCodeEnumProviderRegistry.getProvider(codeType);

            // enum에서 정의된 기본 코드 목록 가져오기
            List<SysCommonCode> defaultCodes = convertEnumsToCodes(provider.getEnumValues());

            // 현재 DB에 있는 해당 타입의 코드 목록 조회
            List<SysCommonCode> existingCodes = sysCommonCodeRepository.findByCodeTypeOrderByDisplayOrder(codeType);

            // DB에 있는 코드들을 Map으로 변환하여 빠른 조회 가능하게 함
            var existingCodeMap = existingCodes.stream()
                    .collect(Collectors.toMap(SysCommonCode::getCode, code -> code));

            // 각 기본 코드에 대해 동기화 수행
            for (SysCommonCode defaultCode : defaultCodes) {
                synchronizeCode(defaultCode, existingCodeMap);
            }

            // DB에는 있지만 enum에는 없는 코드 처리 (선택적으로 비활성화)
            deactivateOrphanedCodes(existingCodes, defaultCodes);

            log.info("시스템 코드 타입 [{}] 동기화 완료", codeType);
        } catch (Exception e) {
            log.error("시스템 코드 타입 [{}] 동기화 중 오류 발생: {}", codeType, e.getMessage());
            throw e; // 트랜잭션 롤백을 위해 예외 다시 던지기
        }
    }

    /**
     * 개별 코드를 동기화합니다.
     *
     * @param defaultCode     기본 코드 (enum 기반)
     * @param existingCodeMap 기존 코드 맵
     */
    private void synchronizeCode(SysCommonCode defaultCode, java.util.Map<String, SysCommonCode> existingCodeMap) {
        String codeValue = defaultCode.getCode();

        // 이미 존재하는 코드인지 확인
        if (existingCodeMap.containsKey(codeValue)) {
            // 기존 코드 정보
            SysCommonCode existingCode = existingCodeMap.get(codeValue);

            // 변경 필요 여부 확인 (코드명, 설명, 표시 순서)
            boolean needsUpdate = !defaultCode.getCodeName().equals(existingCode.getCodeName()) ||
                    (defaultCode.getDescription() != null &&
                            !defaultCode.getDescription().equals(existingCode.getDescription())) ||
                    defaultCode.getDisplayOrder() != existingCode.getDisplayOrder();

            // 변경이 필요하면 업데이트
            if (needsUpdate) {
                existingCode.setCodeName(defaultCode.getCodeName());
                if (defaultCode.getDescription() != null) {
                    existingCode.setDescription(defaultCode.getDescription());
                }
                existingCode.setDisplayOrder(defaultCode.getDisplayOrder());

                SysCommonCode updatedCode = sysCommonCodeRepository.save(existingCode);
                log.debug("시스템 코드 업데이트: {}:{} ({})", existingCode.getCodeType(), codeValue, updatedCode.getCodeName());
            }
        } else {
            // 코드가 없으면 새로 생성
            SysCommonCode savedCode = sysCommonCodeRepository.save(defaultCode);
            log.debug("시스템 코드 생성: {}:{} ({})", defaultCode.getCodeType(), codeValue, savedCode.getCodeName());
        }
    }

    /**
     * 사용되지 않는 코드를 비활성화합니다.
     *
     * @param existingCodes 기존 코드 목록
     * @param defaultCodes  기본 코드 목록 (enum 기반)
     */
    private void deactivateOrphanedCodes(List<SysCommonCode> existingCodes, List<SysCommonCode> defaultCodes) {
        existingCodes.stream()
                .filter(existingCode -> defaultCodes.stream()
                        .noneMatch(defaultCode -> defaultCode.getCode().equals(existingCode.getCode())))
                .forEach(orphanCode -> {
                    // 사용되지 않는 코드는 비활성화만 하고 삭제하지 않음
                    if (orphanCode.isActiveFlag()) {
                        orphanCode.setActiveFlag(false);
                        sysCommonCodeRepository.save(orphanCode);
                        log.debug("미사용 시스템 코드 비활성화: {}:{}", orphanCode.getCodeType(), orphanCode.getCode());
                    }
                });
    }

    /**
     * SysCommonCodeEnum 배열을 SysCommonCode 엔티티 목록으로 변환합니다.
     *
     * @param enumValues SysCommonCodeEnum 배열
     * @return SysCommonCode 엔티티 목록
     */
    private List<SysCommonCode> convertEnumsToCodes(SysCommonCodeEnum[] enumValues) {
        List<SysCommonCode> codes = new ArrayList<>();
        for (SysCommonCodeEnum enumValue : enumValues) {
            SysCommonCode code = SysCommonCode.builder()
                    .codeType(enumValue.getCodeType())
                    .code(enumValue.getCode())
                    .codeName(enumValue.getCodeName())
                    .description(enumValue.getDescription())
                    .displayOrder(enumValue.getDisplayOrder())
                    .activeFlag(true)
                    .build();
            codes.add(code);
        }
        return codes;
    }

    /**
     * 코드 타입 문자열로 시스템 코드 목록 조회
     * @param codeTypeStr 코드 타입 문자열 (예: "AUCTION_STATUS")
     * @return 국제화된 코드 DTO 목록
     * @throws BusinessException 유효하지 않은 코드 타입인 경우
     */
    public List<CommonCodeResponseDTO> getSysCommonCodesByCodeType(String codeTypeStr) {
        try {
            // 코드 제공자 조회
            SysCommonCodeEnumProvider provider = sysCommonCodeEnumProviderRegistry.getProvider(codeTypeStr);

            // 제공자로부터 enum 값 추출
            SysCommonCodeEnum[] enumValues = provider.getEnumValues();

            // enum 값을 기반으로 국제화 코드 DTO 생성
            return Arrays.stream(enumValues)
                    .map(this::getSysCommonCode)
                    .sorted(Comparator.comparingInt(CommonCodeResponseDTO::getDisplayOrder))
                    .collect(Collectors.toList());
        } catch (BusinessException e) {
            throw e; // 이미 적절한 예외이므로 그대로 전달
        } catch (Exception e) {
            log.error("시스템 코드 조회 중 오류 발생: {}", codeTypeStr, e);
            throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, codeTypeStr);
        }
    }

    /**
     * StatusCodeEnum 인터페이스에 대한 공통 코드 정보를 반환합니다.
     * (타입 추론 문제 해결용 메서드)
     *
     * @param enumValue StatusCodeEnum 구현체
     * @return 시스템 공통 코드 DTO
     */
    public CommonCodeResponseDTO getSysCommonCode(SysCommonCodeEnum enumValue) {
        String codeName = getCodeName(enumValue.getCodeType(), enumValue.getCode());

        return CommonCodeResponseDTO.builder()
                .codeId(null) // DB ID는 없음
                .codeType(enumValue.getCodeType())
                .code(enumValue.getCode())
                .codeName(codeName)
                .originalCodeName(enumValue.getCodeName())
                .description(enumValue.getDescription())
                .displayOrder(enumValue.getDisplayOrder())
                .activeFlag(true) // enum은 항상 활성 상태로 간주
                .createdAt(null)  // 시간 정보 없음
                .updatedAt(null)  // 시간 정보 없음
                .build();
    }


    /**
     * 코드 타입과 코드 값으로 시스템 코드 조회
     *
     * @param codeType 코드 타입
     * @param code 코드 값
     * @return 시스템 공통 코드 DTO
     */
    public CommonCodeResponseDTO getSysCommonCode(String codeType, String code) {

        // 1. 코드 타입에 해당하는 Provider 찾기
        SysCommonCodeEnumProvider provider = sysCommonCodeEnumProviderRegistry.getProvider(codeType);

        // 2. Provider로부터 모든 enum 값 가져오기
        SysCommonCodeEnum[] enumValues = provider.getEnumValues();

        // 3. 코드 값과 일치하는 enum 찾기
        SysCommonCodeEnum matchingEnum = Arrays.stream(enumValues)
                .filter(enumValue -> enumValue.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND, codeType, code));

        // 4. 국제화된 코드명 가져오기
        String codeName = getCodeName(codeType, code);

        return CommonCodeResponseDTO.builder()
                .codeId(null) // DB ID는 없음
                .codeType(matchingEnum.getCodeType())
                .code(matchingEnum.getCode())
                .codeName(codeName)
                .originalCodeName(matchingEnum.getCodeName())
                .description(matchingEnum.getDescription())
                .displayOrder(matchingEnum.getDisplayOrder())
                .activeFlag(true) // enum은 항상 활성 상태로 간주
                .createdAt(null)  // 시간 정보 없음
                .updatedAt(null)  // 시간 정보 없음
                .build();
    }

    /**
     * enum 상수에 대한 국제화된 코드명을 반환합니다.
     *
     * @param enumValue 코드 enum 값
     * @return 국제화된 코드명
     */
    public <T extends Enum<?> & SysCommonCodeEnum> String getCodeName(T enumValue) {
        return getCodeName(enumValue, LocaleContextHolder.getLocale());
    }

    /**
     * enum 상수에 대한 국제화된 코드명을 지정된 로케일로 반환합니다.
     *
     * @param enumValue 코드 enum 값
     * @param locale 로케일
     * @return 국제화된 코드명
     */
    public <T extends Enum<?> & SysCommonCodeEnum> String getCodeName(T enumValue, Locale locale) {
        String messageKey = getMessageKey(enumValue);
        try {
            // 메시지 리소스에서 번역된 텍스트 가져오기 시도
            return messageSource.getMessage(messageKey, null, locale);
        } catch (NoSuchMessageException e) {
            // 번역이 없으면 enum에서 직접 기본값 가져오기
            String defaultName = enumValue.getCodeName();
            log.debug("국제화된 메시지를 찾을 수 없음: {}, 기본값 사용: {}", messageKey, defaultName);
            return defaultName;
        }
    }

    /**
     * enum 상수에 대한 국제화된 코드 정보를 반환합니다.
     *
     * @param enumValue 코드 enum 값
     * @return 국제화된 코드 DTO
     */
    public <T extends Enum<?> & SysCommonCodeEnum> CommonCodeResponseDTO getSysCommonCode(T enumValue) {
        return getSysCommonCode(enumValue, LocaleContextHolder.getLocale());
    }

    /**
     * enum 상수에 대한 국제화된 코드 정보를 지정된 로케일로 반환합니다.
     *
     * @param enumValue 코드 enum 값
     * @param locale 로케일
     * @return 국제화된 코드 DTO
     */
    public <T extends Enum<?> & SysCommonCodeEnum> CommonCodeResponseDTO getSysCommonCode(T enumValue, Locale locale) {
        String i18nCodeName = getCodeName(enumValue, locale);

        return CommonCodeResponseDTO.builder()
                .codeId(null) // DB ID는 없음
                .codeType(enumValue.getCodeType())
                .code(enumValue.getCode())
                .codeName(i18nCodeName)
                .originalCodeName(enumValue.getCodeName())
                .description(enumValue.getDescription())
                .displayOrder(enumValue.getDisplayOrder())
                .activeFlag(true) // enum은 항상 활성 상태로 간주
                .createdAt(null)  // 시간 정보 없음
                .updatedAt(null)  // 시간 정보 없음
                .build();
    }

    /**
     * 지정된 enum 타입의 모든 값에 대해 국제화된 코드 정보를 반환합니다.
     *
     * @param enumValues enum 값 목록
     * @return 국제화된 코드 DTO 목록
     */
    public <T extends Enum<?> & SysCommonCodeEnum> List<CommonCodeResponseDTO> getSysCommonCodes(T[] enumValues) {
        return getSysCommonCodes(enumValues, LocaleContextHolder.getLocale());
    }

    /**
     * 지정된 enum 타입의 모든 값에 대해 국제화된 코드 정보를 지정된 로케일로 반환합니다.
     *
     * @param enumValues enum 값 목록
     * @param locale 로케일
     * @return 국제화된 코드 DTO 목록
     */
    public <T extends Enum<?> & SysCommonCodeEnum> List<CommonCodeResponseDTO> getSysCommonCodes(T[] enumValues, Locale locale) {
        return Arrays.stream(enumValues)
                .map(value -> getSysCommonCode(value, locale))
                .sorted(Comparator.comparingInt(CommonCodeResponseDTO::getDisplayOrder))
                .collect(Collectors.toList());
    }

    /**
     * enum 값으로부터 메시지 키를 생성합니다.
     */
    private <T extends Enum<?> & SysCommonCodeEnum> String getMessageKey(T enumValue) {
        return String.format(CODE_MESSAGE_KEY_FORMAT, enumValue.getCodeType(), enumValue.getCode());
    }

    /**
     * 하위 호환성을 위한 메서드: 코드 타입과 코드 값을 사용하여 메시지 키를 생성합니다.
     */
    public String getMessageKey(String codeType, String code) {
        return String.format(CODE_MESSAGE_KEY_FORMAT, codeType, code);
    }

    /**
     * 코드 타입과 코드 값으로부터 국제화된 코드명을 반환합니다.
     */
    public String getCodeName(String codeType, String code) {
        return getCodeName(codeType, code, LocaleContextHolder.getLocale());
    }

    /**
     * 코드 타입과 코드 값으로부터 국제화된 코드명을 지정된 로케일로 반환합니다.
     */
    public String getCodeName(String codeType, String code, Locale locale) {
        String messageKey = getMessageKey(codeType, code);
        try {
            return messageSource.getMessage(messageKey, null, locale);
        } catch (NoSuchMessageException e) {
            // 일치하는 enum을 찾아서 기본 코드명 사용
            try {
                SysCommonCodeEnumProvider provider = sysCommonCodeEnumProviderRegistry.getProvider(codeType);
                SysCommonCodeEnum[] enumValues = provider.getEnumValues();

                SysCommonCodeEnum matchingEnum = Arrays.stream(enumValues)
                        .filter(enumValue -> enumValue.getCode().equals(code))
                        .findFirst()
                        .orElse(null);

                String defaultName = matchingEnum != null ? matchingEnum.getCodeName() : code;
                log.debug("국제화된 메시지를 찾을 수 없음: {}, 기본값 사용: {}", messageKey, defaultName);
                return defaultName;
            } catch (Exception ex) {
                log.warn("국제화된 메시지를 찾을 수 없고 enum을 찾을 수 없음: {}", messageKey);
                return code; // enum을 찾을 수 없는 경우 코드 자체 반환
            }
        }
    }
}