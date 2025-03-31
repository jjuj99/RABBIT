package com.rabbit.global.code.controller;

import com.rabbit.global.code.controller.swagger.CommonCodeControllerSwagger;
import com.rabbit.global.code.domain.dto.request.CommonCodeRequestDTO;
import com.rabbit.global.code.domain.dto.request.CommonCodeUpdateRequestDTO;
import com.rabbit.global.code.domain.dto.response.CommonCodeResponseDTO;
import com.rabbit.global.code.domain.entity.CommonCode;
import com.rabbit.global.code.domain.entity.SysCommonCode;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.CommonCodeService;
import com.rabbit.global.code.service.SysCommonCodeService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 통합 공통 코드 컨트롤러
 * 시스템 공통 코드와 일반 공통 코드를 모두 처리합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/codes")
public class CommonCodeController implements CommonCodeControllerSwagger {

    private final CommonCodeService commonCodeService;
    private final SysCommonCodeService sysCommonCodeService;

    // 시스템 공통 코드 타입 목록 - StatusCode에서 동적으로 가져옴
    private final Set<String> systemCodeTypes = SysCommonCodes.getAllCodeTypes();

    /**
     * 코드 타입별 코드 목록 조회
     * 코드 타입에 따라 시스템 공통 코드 또는 일반 공통 코드를 반환합니다.
     */
    @GetCodesByTypeApi
    @GetMapping("/{codeType}")
    public ResponseEntity<CustomApiResponse<?>> getCodesByType(
            @CodeTypeParam @PathVariable String codeType,
            @RequestParam(required = false) String lang) {

        log.debug("[공통 코드 목록 조회 요청] codeType: {}, lang: {}", codeType, lang);

        if (isSystemCodeType(codeType)) {
            // 시스템 공통 코드 조회
            List<CommonCodeResponseDTO> codes = sysCommonCodeService.getSysCommonCodesByCodeType(codeType);
            return ResponseEntity.ok(CustomApiResponse.success(codes));
        } else {
            // 일반 공통 코드 조회
            List<CommonCodeResponseDTO> responseDTOs = commonCodeService.getCommonCodesByType(codeType);
            return ResponseEntity.ok(CustomApiResponse.success(responseDTOs));
        }
    }

    /**
     * 활성화된 공통 코드 목록 조회
     * 시스템 공통 코드는 항상 활성화 상태로 간주됩니다.
     */
    @GetActiveFlagCodesByTypeApi
    @GetMapping("/{codeType}/active")
    public ResponseEntity<CustomApiResponse<?>> getActiveFlagCodesByType(
            @CodeTypeParam @PathVariable String codeType) {

        log.debug("[활성화된 공통 코드 목록 조회 요청] codeType: {}", codeType);

        if (isSystemCodeType(codeType)) {
            // 시스템 공통 코드는 모두 활성화 상태로 간주
            List<CommonCodeResponseDTO> codes = sysCommonCodeService.getSysCommonCodesByCodeType(codeType);
            return ResponseEntity.ok(CustomApiResponse.success(codes));
        } else {
            // 일반 공통 코드 중 활성화된 것만 조회
            List<CommonCodeResponseDTO> responseDTOs = commonCodeService.getActiveFlagCommonCodesByType(codeType);
            return ResponseEntity.ok(CustomApiResponse.success(responseDTOs));
        }
    }

    /**
     * 단일 코드 조회
     * 코드 타입에 따라 시스템 공통 코드 또는 일반 공통 코드를 반환합니다.
     */
    @GetCodeApi
    @GetMapping("/{codeType}/{code}")
    public ResponseEntity<CustomApiResponse<CommonCodeResponseDTO>> getCode(
            @CodeTypeParam @PathVariable String codeType,
            @CodeParam @PathVariable String code) {

        log.debug("[단일 공통 코드 조회 요청] codeType: {}, code: {}", codeType, code);

        if (isSystemCodeType(codeType)) {
            // 시스템 공통 코드 조회
            CommonCodeResponseDTO responseDTOs = sysCommonCodeService.getSysCommonCode(codeType, code);
            return ResponseEntity.ok(CustomApiResponse.success(responseDTOs));
        } else {
            // 일반 공통 코드 조회
            CommonCodeResponseDTO responseDTOs = commonCodeService.getCommonCodeOrThrow(codeType, code);
            return ResponseEntity.ok(CustomApiResponse.success(responseDTOs));
        }
    }

    /**
     * 모든 코드 타입 조회
     */
    @GetCodeTypesApi
    @GetMapping("/types")
    public ResponseEntity<CustomApiResponse<List<String>>> getCodeTypes() {
        log.debug("[모든 코드 타입 조회 요청]");

        // 시스템 코드 타입과 일반 코드 타입을 모두 포함
        Set<String> allCodeTypes = new HashSet<>(systemCodeTypes);
        allCodeTypes.addAll(commonCodeService.getAllCodeTypes());

        return ResponseEntity.ok(CustomApiResponse.success(
                allCodeTypes.stream().sorted().collect(Collectors.toList())
        ));
    }

    /**
     * 일반 공통 코드 생성
     * 시스템 공통 코드는 생성할 수 없습니다.
     * @deprecated StatusCode enum에서 코드를 관리해야 합니다. 이 API는 앱 시작 시 자동 동기화를 위해서만 사용됩니다.
     */
    @Deprecated
    @CreateCodeApi
    @PostMapping
    public ResponseEntity<CustomApiResponse<CommonCodeResponseDTO>> createCode(
            @Valid @RequestBody CommonCodeRequestDTO requestDTO) {

        log.warn("[공통 코드 생성 요청] 주의: 코드 생성보다 StatusCode enum에 정의하는 것이 바람직합니다. request: {}", requestDTO);

        // 시스템 코드 타입을 통해 생성 방지
        if (isSystemCodeType(requestDTO.getCodeType())) {
            throw new BusinessException(
                    ErrorCode.CODE_TYPE_INVALID,
                    "시스템 공통 코드는 API를 통해 생성할 수 없습니다. StatusCode enum에 정의해주세요."
            );
        }

        CommonCode commonCode = convertToCommonCode(requestDTO);
        CommonCode savedCode = commonCodeService.saveCode(commonCode);

        // 생성된 리소스의 URI 생성
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{codeType}/{code}")
                .buildAndExpand(savedCode.getCodeType(), savedCode.getCode())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CustomApiResponse.success(CommonCodeResponseDTO.from(savedCode)));
    }

    /**
     * 일반 공통 코드 업데이트
     * 시스템 공통 코드는 업데이트할 수 없습니다.
     * @deprecated StatusCode enum에서 코드를 관리해야 합니다. 이 API는 앱 시작 시 자동 동기화를 위해서만 사용됩니다.
     */
    @Deprecated
    @UpdateCodeApi
    @PutMapping("/{codeType}/{code}")
    public ResponseEntity<CustomApiResponse<CommonCodeResponseDTO>> updateCode(
            @CodeTypeParam @PathVariable String codeType,
            @CodeParam @PathVariable String code,
            @Valid @RequestBody CommonCodeUpdateRequestDTO requestDTO) {

        log.debug("[공통 코드 수정 요청] codeType: {}, code: {}, request: {}", codeType, code, requestDTO);

        // 시스템 코드 타입을 통해 업데이트 방지
        if (isSystemCodeType(codeType)) {
            throw new BusinessException(
                    ErrorCode.CODE_TYPE_INVALID,
                    "시스템 공통 코드는 API를 통해 업데이트할 수 없습니다. StatusCode enum에 정의해주세요."
            );
        }

        CommonCodeResponseDTO responseDTO = commonCodeService.updateCode(codeType, code, requestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));

    }

    /**
     * 일반 공통 코드 삭제
     * 시스템 공통 코드는 삭제할 수 없습니다.
     * @deprecated StatusCode enum에서 코드를 관리해야 합니다. 이 API는 앱 시작 시 자동 동기화를 위해서만 사용됩니다.
     */
    @Deprecated
    @DeleteCodeApi
    @DeleteMapping("/{codeType}/{code}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> deleteCode(
            @CodeTypeParam @PathVariable String codeType,
            @CodeParam @PathVariable String code) {

        log.debug("[공통 코드 삭제 요청] codeType: {}, code: {}", codeType, code);

        // 시스템 코드 타입을 통해 삭제 방지
        if (isSystemCodeType(codeType)) {
            throw new BusinessException(
                    ErrorCode.CODE_TYPE_INVALID,
                    "시스템 공통 코드는 API를 통해 삭제할 수 없습니다. StatusCode enum에서 관리됩니다."
            );
        }

        commonCodeService.deleteCode(codeType, code);

        return ResponseEntity.ok(CustomApiResponse.success(
                MessageResponse.of(String.format("코드가 삭제되었습니다: 타입=%s, 코드=%s", codeType, code))
        ));
    }

    /**
     * 코드 캐시 새로고침
     * @deprecated 이 API는 앱 시작 시 자동 동기화를 위해서만 사용됩니다.
     */
    @Deprecated
    @RefreshCacheApi
    @PostMapping("/{codeType}/refresh-cache")
    public ResponseEntity<CustomApiResponse<MessageResponse>> refreshCache(
            @CodeTypeParam @PathVariable String codeType) {

        log.debug("[공통 코드 캐시 새로고침 요청] codeType: {}", codeType);

        // 일반 코드 타입 모두 캐시 갱신
        commonCodeService.refreshCache(codeType);

        return ResponseEntity.ok(CustomApiResponse.success(
                MessageResponse.of(String.format("코드 타입 [%s]의 캐시가 새로고침되었습니다", codeType))
        ));
    }

    /**
     * 모든 코드 캐시 새로고침
     * @deprecated StatusCode enum에서 코드를 관리해야 합니다. 이 API는 앱 시작 시 자동 동기화를 위해서만 사용됩니다.
     */
    @Deprecated
    @RefreshAllCachesApi
    @PostMapping("/refresh-all-caches")
    public ResponseEntity<CustomApiResponse<MessageResponse>> refreshAllCaches() {

        log.debug("[모든 공통 코드 캐시 새로고침 요청]");

        commonCodeService.refreshAllCaches();

        return ResponseEntity.ok(CustomApiResponse.success(
                MessageResponse.of("모든 공통 코드 캐시가 새로고침되었습니다")
        ));
    }

    /**
     * DTO를 엔티티로 변환
     * @param dto 요청 DTO
     * @return 변환된 엔티티
     */
    private SysCommonCode convertToSysCommonCode(CommonCodeRequestDTO dto) {
        return SysCommonCode.builder()
                .codeType(dto.getCodeType())
                .code(dto.getCode())
                .codeName(dto.getCodeName())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder())
                .activeFlag(dto.getActiveFlag())
                .build();
    }
    private CommonCode convertToCommonCode(CommonCodeRequestDTO dto) {
        return CommonCode.builder()
                .codeType(dto.getCodeType())
                .code(dto.getCode())
                .codeName(dto.getCodeName())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder())
                .activeFlag(dto.getActiveFlag())
                .build();
    }

    /**
     * 시스템 공통 코드 타입인지 확인
     * @param codeType 코드 타입
     * @return 시스템 공통 코드 타입 여부
     */
    private boolean isSystemCodeType(String codeType) {
        return systemCodeTypes.contains(codeType);
    }
}