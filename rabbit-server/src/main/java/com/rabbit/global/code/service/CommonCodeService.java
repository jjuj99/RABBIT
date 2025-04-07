package com.rabbit.global.code.service;

import com.rabbit.global.code.domain.dto.request.CommonCodeUpdateRequestDTO;
import com.rabbit.global.code.domain.dto.response.CommonCodeResponseDTO;
import com.rabbit.global.code.domain.entity.CommonCode;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.code.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "codes")
public class CommonCodeService {
    private final CommonCodeRepository commonCodeRepository;
    // 동기화가 아닌 초기세팅 CommonCodeEnumProviderRegistry 필요
    private final MessageSource messageSource;

    // 코드 메시지 키 형식 (SysCommonCodeService와 동일한 형식 사용)
    private static final String CODE_MESSAGE_KEY_FORMAT = "code.%s.%s";

    // 캐시 키 상수 정의
    private static final String CACHE_KEY_TYPE = "#codeType";
    private static final String CACHE_KEY_ENTITY_TYPE = "#commonCode.codeType";
    private static final String CACHE_KEY_ACTIVE_TYPE = "'activeFlag.' + #codeType";
    private static final String CACHE_KEY_TYPE_CODE = "#codeType + '.' + #code";

    /**
     * 코드를 저장합니다. 이미 존재하는 코드인 경우 예외가 발생합니다.
     *
     * @param commonCode 저장할 코드 객체
     * @return 저장된 코드 객체
     * @throws BusinessException 이미 존재하는 코드인 경우
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#commonCode.codeType"),
            @CacheEvict(key = "'activeFlag.' + #commonCode.codeType")
    })
    public CommonCode saveCode(CommonCode commonCode) {
        validateCode(commonCode);

        // 중복 코드 체크
        if (commonCodeRepository.existsByCodeTypeAndCode(commonCode.getCodeType(), commonCode.getCode())) {
            throw new BusinessException(ErrorCode.CODE_ALREADY_EXISTS, commonCode.getCodeType(), commonCode.getCode());
        }

        CommonCode savedCode = commonCodeRepository.save(commonCode);
        log.debug("공통 코드 저장 완료: {}", savedCode);
        return savedCode;
    }

    /**
     * 코드를 벌크로 저장합니다. 중복 코드는 건너뜁니다.
     * Spring Data JPA의 saveAll 메서드 활용
     *
     * @param codes 저장할 코드 목록
     * @return 저장된 코드 목록
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public List<CommonCode> saveCodes(List<CommonCode> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }

        // 유효하지 않은 코드 필터링
        List<CommonCode> validCodes = codes.stream()
                .filter(this::isValidCode)
                .collect(Collectors.toList());

        if (validCodes.isEmpty()) {
            return List.of();
        }

        // 코드 타입과 코드값 추출
        Set<String> codeTypes = validCodes.stream()
                .map(CommonCode::getCodeType)
                .collect(Collectors.toSet());

        Set<String> codeValues = validCodes.stream()
                .map(CommonCode::getCode)
                .collect(Collectors.toSet());

        // 이미 존재하는 코드 타입과 코드 쌍 찾기
        List<CommonCodeRepository.CodeTypePair> existingPairs = commonCodeRepository.findExistingCodeTypeAndCodePairs(
                codeTypes, codeValues
        );

        // 중복 코드 제외 처리
        Set<String> existingTypeAndValuePairs = existingPairs.stream()
                .map(pair -> pair.getCodeType() + ":" + pair.getCode())
                .collect(Collectors.toSet());

        List<CommonCode> newCodesToSave = validCodes.stream()
                .filter(code -> !existingTypeAndValuePairs.contains(code.getCodeType() + ":" + code.getCode()))
                .collect(Collectors.toList());

        if (newCodesToSave.isEmpty()) {
            return List.of();
        }

        List<CommonCode> savedCodes = commonCodeRepository.saveAll(newCodesToSave);
        log.debug("일괄 처리로 {} 개의 공통 코드 저장 완료", savedCodes.size());
        return savedCodes;
    }


    /**
     * 코드를 업데이트합니다.
     *
     * @param codeType 코드 타입
     * @param code 코드 값
     * @param updateRequest 업데이트할 코드 정보
     * @return 업데이트된 코드 DTO
     * @throws BusinessException 코드가 존재하지 않는 경우
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#codeType"),
            @CacheEvict(key = "'activeFlag.' + #codeType"),
            @CacheEvict(key = "#codeType + '.' + #code")
    })
    public CommonCodeResponseDTO updateCode(String codeType, String code, CommonCodeUpdateRequestDTO updateRequest) {

        // 기존 코드 존재 여부 확인
        CommonCode existingCode = commonCodeRepository.findByCodeTypeAndCode(codeType, code)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.CODE_NOT_FOUND, codeType, code
                ));

        // 필드 업데이트 (null이 아닌 필드만)
        if (updateRequest.getCodeName() != null) {
            existingCode.setCodeName(updateRequest.getCodeName());
        }

        if (updateRequest.getDescription() != null) {
            existingCode.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getDisplayOrder() != null) {
            existingCode.setDisplayOrder(updateRequest.getDisplayOrder());
        }

        if (updateRequest.getActiveFlag() != null) {
            existingCode.setActiveFlag(updateRequest.getActiveFlag());
        }

        CommonCode updatedCode = commonCodeRepository.save(existingCode);
        log.debug("공통 코드 업데이트 완료: {}", updatedCode);
        return createI18nResponseDTO(updatedCode);
    }

    /**
     * 코드를 삭제합니다.
     *
     * @param codeType 코드 타입
     * @param code     코드 값
     * @throws BusinessException 코드가 존재하지 않는 경우
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#codeType"),
            @CacheEvict(key = "'activeFlag.' + #codeType"),
            @CacheEvict(key = "#codeType + '.' + #code")
    })
    public void deleteCode(String codeType, String code) {
        log.warn("코드 삭제 요청: 코드 삭제보다 StatusCode enum에서 제거하는 것이 바람직합니다. {}:{}", codeType, code);

        CommonCode existingCode = commonCodeRepository.findByCodeTypeAndCode(codeType, code)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND, codeType, code));

        commonCodeRepository.delete(existingCode);
        log.debug("공통 코드 삭제 완료: 타입={}, 코드={}", codeType, code);
    }

    /**
     * 특정 코드 타입에 해당하는 모든 코드를 조회하고 국제화합니다.
     *
     * @param codeType 코드 타입
     * @return 국제화된 코드 목록
     */
    @Cacheable(key = CACHE_KEY_TYPE)
    public List<CommonCodeResponseDTO> getCommonCodesByType(String codeType) {
        log.debug("코드 타입 [{}]에 대한 코드 목록 조회 중", codeType);
        List<CommonCode> codes = commonCodeRepository.findByCodeTypeOrderByDisplayOrder(codeType);

        // 각 코드를 국제화된 DTO로 변환
        List<CommonCodeResponseDTO> responseDTOs = codes.stream()
                .map(this::createI18nResponseDTO)
                .collect(Collectors.toList());

        return responseDTOs;
    }

    /**
     * 특정 코드 타입의 활성화된 코드만 조회하고 국제화합니다.
     *
     * @param codeType 코드 타입
     * @return 국제화된 활성화 코드 목록
     */
    @Cacheable(key = CACHE_KEY_ACTIVE_TYPE)
    public List<CommonCodeResponseDTO> getActiveFlagCommonCodesByType(String codeType) {
        log.debug("코드 타입 [{}]에 대한 활성화된 코드 목록 조회 중", codeType);
        List<CommonCode> codes = commonCodeRepository.findByCodeTypeAndActiveFlagOrderByDisplayOrder(codeType, true);

        // 각 코드를 국제화된 DTO로 변환
        List<CommonCodeResponseDTO> responseDTOs = codes.stream()
                .map(this::createI18nResponseDTO)
                .collect(Collectors.toList());

        return responseDTOs;
    }

    /**
     * 특정 코드를 조회합니다.
     *
     * @param codeType 코드 타입
     * @param code     코드 값
     * @return 코드 객체 (Optional)
     */
    @Cacheable(key = CACHE_KEY_TYPE_CODE)
    public Optional<CommonCode> getCommonCode(String codeType, String code) {
        log.debug("단일 코드 조회 중: 타입={}, 코드={}", codeType, code);
        return commonCodeRepository.findByCodeTypeAndCode(codeType, code);
    }

    /**
     * 특정 코드를 조회하고 국제화된 ResponseDTO로 변환합니다. 코드가 존재하지 않으면 예외가 발생합니다.
     *
     * @param codeType 코드 타입
     * @param code     코드 값
     * @return 국제화된 코드 응답 DTO
     * @throws BusinessException 코드가 존재하지 않는 경우
     */
    public CommonCodeResponseDTO getCommonCodeOrThrow(String codeType, String code) {
        CommonCode commonCode = getCommonCode(codeType, code)
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_NOT_FOUND, codeType, code));

        // 국제화된 DTO로 변환
        return createI18nResponseDTO(commonCode);
    }

    /**
     * 일반 공통 코드의 모든 코드 타입 조회
     * @return 코드 타입 집합
     */
    @Cacheable(key = "'allCodeTypes'")
    public Set<String> getAllCodeTypes() {
        return commonCodeRepository.findAllCodeTypes();
    }

    /**
     * 특정 코드 타입의 모든 캐시를 갱신합니다.
     *
     * @param codeType 코드 타입
     */
    @Caching(evict = {
            @CacheEvict(key = "#codeType"),
            @CacheEvict(key = "'activeFlag.' + #codeType"),
            @CacheEvict(key = "'allCodeTypes'")
    })
    public void refreshCache(String codeType) {
        log.info("코드 타입 [{}]에 대한 캐시 새로고침 완료", codeType);
    }

    /**
     * 모든 코드 캐시를 갱신합니다.
     */
    @CacheEvict(allEntries = true)
    public void refreshAllCaches() {
        log.info("모든 코드 캐시 새로고침 완료");
    }

    /**
     * 코드의 유효성을 검증합니다.
     *
     * @param commonCode 검증할 코드 객체
     * @throws BusinessException 코드가 유효하지 않은 경우
     */
    private void validateCode(CommonCode commonCode) {
        // CommonCode 객체 NULL 체크
        if (commonCode == null) {
            throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, "공통 코드 객체가 null일 수 없습니다");
        }

        // 코드 타입 유효성 검사
        if (!StringUtils.hasText(commonCode.getCodeType())) {
            throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, "코드 타입은 null이거나 빈 값일 수 없습니다");
        }

        // 코드 값 유효성 검사
        if (!StringUtils.hasText(commonCode.getCode())) {
            throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, "코드 값은 null이거나 빈 값일 수 없습니다");
        }

        // 코드 이름 유효성 검사
        if (!StringUtils.hasText(commonCode.getCodeName())) {
            throw new BusinessException(ErrorCode.CODE_TYPE_INVALID, "코드 이름은 null이거나 빈 값일 수 없습니다");
        }

        // 코드 타입 유효성 검사 (등록된 타입인지)
    }

    /**
     * 코드의 유효성을 검사합니다. 예외를 던지지 않고 결과만 반환합니다.
     *
     * @param commonCode 검증할 코드 객체
     * @return 유효성 여부
     */
    private boolean isValidCode(CommonCode commonCode) {
        try {
            validateCode(commonCode);
            return true;
        } catch (BusinessException e) {
            log.warn("유효하지 않은 코드 감지됨: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 메시지 키를 생성합니다.
     *
     * @param codeType 코드 타입
     * @param code 코드 값
     * @return 메시지 키
     */
    private String getMessageKey(String codeType, String code) {
        return String.format(CODE_MESSAGE_KEY_FORMAT, codeType, code);
    }

    /**
     * 코드 엔티티를 국제화된 ResponseDTO로 변환합니다.
     *
     * @param commonCode 코드 엔티티
     * @return 국제화된 코드 응답 DTO
     */
    private CommonCodeResponseDTO createI18nResponseDTO(CommonCode commonCode) {
        // 현재 로케일로 국제화된 코드명 조회
        String i18nCodeName = getCodeName(commonCode.getCodeType(), commonCode.getCode());

        return CommonCodeResponseDTO.builder()
                .codeId(commonCode.getCodeId())
                .codeType(commonCode.getCodeType())
                .code(commonCode.getCode())
                .codeName(i18nCodeName)
                .originalCodeName(commonCode.getCodeName()) // 원본 코드명도 함께 저장
                .description(commonCode.getDescription())
                .displayOrder(commonCode.getDisplayOrder())
                .activeFlag(commonCode.isActiveFlag())
                .createdAt(commonCode.getCreatedAt())
                .updatedAt(commonCode.getUpdatedAt())
                .build();
    }

    /**
     * 코드 타입과 코드 값에 대한 국제화된 코드명을 반환합니다.
     *
     * @param codeType 코드 타입
     * @param code 코드 값
     * @return 국제화된 코드명
     */
    public String getCodeName(String codeType, String code) {
        return getCodeName(codeType, code, LocaleContextHolder.getLocale());
    }

    /**
     * 코드 타입과 코드 값에 대한 국제화된 코드명을 지정된 로케일로 반환합니다.
     *
     * @param codeType 코드 타입
     * @param code 코드 값
     * @param locale 로케일
     * @return 국제화된 코드명
     */
    public String getCodeName(String codeType, String code, Locale locale) {
        String messageKey = getMessageKey(codeType, code);
        try {
            // 메시지 리소스에서 번역된 텍스트 가져오기 시도
            return messageSource.getMessage(messageKey, null, locale);
        } catch (NoSuchMessageException e) {
            // 국제화된 메시지가 없는 경우 DB에 저장된 기본 코드명 사용
            CommonCode commonCode = getCommonCode(codeType, code).orElse(null);
            String defaultName = commonCode != null ? commonCode.getCodeName() : code;

            log.debug("국제화된 메시지를 찾을 수 없음: {}, 기본값 사용: {}", messageKey, defaultName);
            return defaultName;
        }
    }
}