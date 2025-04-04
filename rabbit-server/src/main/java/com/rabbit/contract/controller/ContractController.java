package com.rabbit.contract.controller;

import com.rabbit.contract.domain.dto.request.*;
import com.rabbit.contract.domain.dto.response.ContractListResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rabbit.contract.controller.swagger.ContractControllerSwagger;
import com.rabbit.contract.domain.dto.response.ContractConfigResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractDetailResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractResponseDTO;
import com.rabbit.contract.service.ContractService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.user.domain.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
public class ContractController implements ContractControllerSwagger {

    private final ContractService contractService;

    /**
     * 계약 설정 정보 조회 API
     *
     * @return 계약 설정 정보
     */
    @GetContractConfigApi
    @GetMapping("/config")
    public ResponseEntity<CustomApiResponse<ContractConfigResponseDTO>> getContractConfig() {
        log.debug("[계약 설정 정보 조회 요청]");
        ContractConfigResponseDTO config = contractService.getContractConfig();
        return ResponseEntity.ok(CustomApiResponse.success(config));
    }

    /**
     * 계약 목록 조회 API
     *
     * @param authentication 인증 정보
     * @param searchRequest  검색 및 페이지 요청 정보
     * @return 계약 목록
     */
    @GetContractsApi
    @GetMapping
    public ResponseEntity<CustomApiResponse<PageResponseDTO<ContractListResponseDTO>>> getContracts(
            Authentication authentication,
            @Parameter(hidden = true) @Valid ContractSearchRequestDTO searchRequest) {

        log.debug("[계약 목록 조회 요청] 유형: {}, 페이지: {}, 크기: {}, 정렬: {}",
                searchRequest.getSearchCondition().getType(),
                searchRequest.getPageNumber(),
                searchRequest.getPageSize(),
                searchRequest.getSortBy());

        Integer userId = extractUserIdFromAuthentication(authentication);

        Pageable pageable = searchRequest.toPageable("createdAt", Sort.Direction.DESC);
        PageResponseDTO<ContractListResponseDTO> result = contractService.getContracts(
                userId,
                searchRequest.getSearchCondition().getType(),
                pageable);

        return ResponseEntity.ok(CustomApiResponse.success(result));
    }

    /**
     * 계약 상세 정보 조회 API
     *
     * @param authentication 인증 정보
     * @param contractId     계약 ID
     * @return 계약 상세 정보
     */
    @GetContractDetailApi
    @GetMapping("/{contractId}")
    public ResponseEntity<CustomApiResponse<ContractDetailResponseDTO>> getContractDetail(
            Authentication authentication,
            @ContractIdParam @PathVariable("contractId") Integer  contractId) {

        Integer userId = extractUserIdFromAuthentication(authentication);
        log.debug("[계약 상세 정보 조회 요청] 사용자 ID: {}, 계약 ID: {}", userId, contractId);

        // 사용자 정보를 서비스에 전달하여 권한 검증
        ContractDetailResponseDTO detail = contractService.getContractDetail(userId, contractId);
        return ResponseEntity.ok(CustomApiResponse.success(detail));
    }

    /**
     * 계약 생성 API
     *
     * @param authentication 인증 정보
     * @param request        계약 요청 정보
     * @return 생성된 계약 정보
     */
    @CreateContractApi
    @PostMapping
    public ResponseEntity<CustomApiResponse<ContractResponseDTO>> createContract(
            Authentication authentication,
            @Valid @RequestBody ContractRequestDTO request) {

        Integer userId = extractUserIdFromAuthentication(authentication);
        log.debug("[계약 생성 요청] 채무자: {}, 금액: {}", request.getDrName(), request.getLa());

        ContractResponseDTO response = contractService.createContract(userId, request);

        // 생성된 리소스의 URI 생성
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{contractId}")
                .buildAndExpand(response.getContractId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(CustomApiResponse.success(response));
    }

    /**
     * 계약 취소 API (요청됨 상태에서 채무자가 취소할 경우)
     *
     * @param authentication 인증 정보
     * @param contractId     계약 ID
     * @return 업데이트된 계약 정보
     */
    @CancelContractApi
    @PatchMapping("/{contractId}/cancel")
    public ResponseEntity<CustomApiResponse<ContractResponseDTO>> cancelContract(
            Authentication authentication,
            @ContractIdParam @PathVariable("contractId") Integer  contractId) {

        Integer userId = extractUserIdFromAuthentication(authentication);
        log.debug("[계약 취소 요청] ID: {}, 요청자: {}", contractId, userId);

        ContractResponseDTO response = contractService.cancelContract(contractId, userId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    /**
     * 계약 체결 API
     *
     * @param authentication 인증 정보
     * @param contractId     계약 ID
     * @return 체결된 계약 정보
     */
    @CompleteContractApi
    @PostMapping("/{contractId}/complete")
    public ResponseEntity<CustomApiResponse<ContractResponseDTO>> completeContract(
            Authentication authentication,
            @ContractIdParam @PathVariable("contractId") Integer  contractId,
            @Valid @RequestBody ContractCompleteRequestDTO request) {

        Integer userId = extractUserIdFromAuthentication(authentication);
        log.debug("[계약 체결 요청] ID: {}", contractId);

        ContractResponseDTO response = contractService.completeContract(contractId, userId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    /**
     * 계약 반려(수정요청) API
     *
     * @param authentication 인증 정보
     * @param contractId     계약 ID
     * @param request        반려 요청 정보
     * @return 반려된 계약 정보
     */
    @RejectContractApi
    @PostMapping("/{contractId}/reject")
    public ResponseEntity<CustomApiResponse<ContractResponseDTO>> rejectContract(
            Authentication authentication,
            @ContractIdParam @PathVariable("contractId") Integer  contractId,
            @Valid @RequestBody ContractRejectRequestDTO request) {

        Integer userId = extractUserIdFromAuthentication(authentication);
        log.debug("[계약 반려 요청] ID: {}, 메시지: {}, 취소 여부: {}, 인증 토큰: {}, TxID: {}, 결과 코드: {}",
                contractId,
                request.getRejectMessage(),
                request.isCanceled(),
                request.getPassAuthToken(),
                request.getTxId(),
                request.getAuthResultCode());

        ContractResponseDTO response = contractService.rejectContract(
                contractId,
                userId,
                request
        );
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    /**
     * 계약 삭제 API (관리자)
     *
     * @param authentication 인증 정보
     * @param contractId     계약 ID
     * @return 삭제 결과 메시지
     */
    @DeleteContractApi
    @DeleteMapping("/{contractId}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> deleteContract(
            Authentication authentication,
            @ContractIdParam @PathVariable("contractId") Integer  contractId) {

        Integer userId = extractUserIdFromAuthentication(authentication);
        log.debug("[계약 삭제 요청] ID: {}", contractId);

        contractService.deleteContract(contractId, userId);
        return ResponseEntity.ok(CustomApiResponse.success(
                MessageResponse.of("계약이 성공적으로 삭제되었습니다")
        ));
    }

    /**
     * Authentication 객체에서 사용자 ID를 추출하는 유틸리티 메서드
     *
     * @param authentication Spring Security의 Authentication 객체
     * @return 추출된 사용자 ID (Integer)
     * @throws BusinessException 인증 정보가 없거나 유효하지 않은 경우
     */
    private Integer extractUserIdFromAuthentication(Authentication authentication) {
        // 인증 정보가 없는 경우 검사
        if (authentication == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        // JWT 토큰에서 추출한 사용자 ID 문자열 가져오기
        String userIdStr;
        try {
            userIdStr = (String) authentication.getPrincipal();
        } catch (ClassCastException e) {
            throw new BusinessException(ErrorCode.JWT_INVALID, "인증 정보 형식이 올바르지 않습니다.");
        }

        // userIdStr이 null이거나 비어있는 경우 검사
        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new BusinessException(ErrorCode.JWT_INVALID, "유효하지 않은 사용자 ID입니다.");
        }

        // 문자열을 Integer로 변환
        try {
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "사용자 ID는 숫자 형식이어야 합니다.");
        }
    }
}