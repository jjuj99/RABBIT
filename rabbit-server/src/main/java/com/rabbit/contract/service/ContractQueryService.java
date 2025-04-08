package com.rabbit.contract.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rabbit.contract.domain.dto.request.ContractSearchRequestDTO;
import com.rabbit.contract.domain.dto.response.ContractConfigResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractDetailResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractListResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractResponseDTO;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.UserRepository;
import com.rabbit.user.service.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 계약 조회 관련 서비스
 * 계약 목록 조회, 상세 조회, 검색 등의 읽기 전용 작업 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractQueryService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final SysCommonCodeService sysCommonCodeService;

    // 코드 타입 상수 정의
    private static final String CONTRACT_STATUS = SysCommonCodes.Contract.values()[0].getCodeType();
    private static final String REPAYMENT_TYPE = SysCommonCodes.Repayment.values()[0].getCodeType();

    /**
     * 계약 설정 정보 조회
     * DB 저장하고 cache 처리 예정
     * @return 계약 설정 정보
     */
    public ContractConfigResponseDTO getContractConfig() {
        // 실제로는 DB나 설정 파일에서 가져올 수 있습니다
        return ContractConfigResponseDTO.builder()
                .maxLegalIr(new java.math.BigDecimal("20.0"))
                .defDir(new java.math.BigDecimal("15.0"))
                .defDefCnt(3)
                .defEarlypayFee(new java.math.BigDecimal("2.0"))
                .defGraceDays(7)
                .minLa(new java.math.BigDecimal("10000"))
                .maxLa(new java.math.BigDecimal("10000000"))
                .minLtDays(30)
                .maxLtDays(365)
                .defAddTerms("본 계약은 다음과 같은 조건으로 체결됩니다...[기본 계약 템플릿]")
                .build();
    }

    /**
     * 계약 목록 조회
     * @param userId 조회하는 사용자 ID
     * @param type 계약 유형 (sent: 보낸 계약(채권자), received: 받은 계약(채무자))
     * @param pageable 페이징 정보
     * @return 계약 목록
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ContractListResponseDTO> getContracts(Integer userId, String type, Pageable pageable) {
        log.debug("계약 목록 조회 - 사용자 ID: {}, 타입: {}, 페이징: {}", userId, type, pageable);

        // 사용자 조회
        User user = findUserById(userId);

        // 계약 유형에 따라 다른 조회 메소드 사용
        List<Contract> contracts;
        long totalCount;

        if ("sent".equals(type)) {
            contracts = contractRepository.findByDebtor(user);
        } else if ("received".equals(type)) {
            contracts = contractRepository.findByCreditor(user);
        } else {
            log.warn("유효하지 않은 계약 유형: {}", type);
            // 유효하지 않은 타입인 경우 빈 결과 반환
            return new PageResponseDTO<>(
                    List.of(),
                    0, // pageNumber
                    Integer.MAX_VALUE, // pageSize
                    0 // totalElements
            );
        }

        totalCount = contracts.size();

        // 계약 엔티티를 DTO로 변환
        List<ContractListResponseDTO> content = contracts.stream()
                .map(contract -> {
                    ContractListResponseDTO dto = ContractListResponseDTO.from(contract);

                    // 국제화된 상태명 설정
                    if (dto.getContractStatus() != null) {
                        dto.setContractStatusName(sysCommonCodeService.getCodeName(
                                CONTRACT_STATUS, dto.getContractStatus().getCode()));
                    }
                    if (dto.getRepayType() != null) {
                        dto.setRepayTypeName(sysCommonCodeService.getCodeName(
                                REPAYMENT_TYPE, dto.getRepayType().getCode()));
                    }

                    // 지갑 주소 설정 (채무자 또는 채권자)
                    if ("sent".equals(type)) {
                        // 채무자인 경우 채권자 정보
                        dto.setName(contract.getCreditor().getUserName());
                        dto.setWalletAddress(walletService.getUserPrimaryWalletAddress(contract.getCreditor()));
                    } else {
                        // 채권자인 경우 채무자 정보
                        dto.setName(contract.getDebtor().getUserName());
                        dto.setWalletAddress(walletService.getUserPrimaryWalletAddress(contract.getDebtor()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                content,
                0, // pageNumber
                Integer.MAX_VALUE, // pageSize
                totalCount
        );
    }

    /**
     * 계약 목록 검색
     * @param searchRequest 검색 조건
     * @return 검색 결과
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ContractResponseDTO> searchContracts(ContractSearchRequestDTO searchRequest) {
        Pageable pageable = searchRequest.toPageable();
        Page<Contract> contractPage = contractRepository.findBySearchCondition(searchRequest, pageable);

        List<ContractResponseDTO> content = contractPage.getContent().stream()
                .map(contract -> {
                    ContractResponseDTO dto = ContractResponseDTO.from(contract);
                    // 국제화된 상태명 설정
                    if (dto.getContractStatus() != null) {
                        dto.setContractStatusName(sysCommonCodeService.getCodeName(
                                CONTRACT_STATUS, dto.getContractStatus().getCode()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                content,
                contractPage.getNumber(),
                contractPage.getSize(),
                contractPage.getTotalElements()
        );
    }

    /**
     * 계약 상세 정보 조회
     * @param userId 요청한 사용자 ID
     * @param contractId 계약 ID
     * @return 계약 상세 정보
     */
    @Transactional(readOnly = true)
    public ContractDetailResponseDTO getContractDetail(Integer userId, Integer contractId) {
        log.debug("[계약 상세 정보 조회] 사용자 ID: {}, 계약 ID: {}", userId, contractId);

        // 계약 기본 정보 조회
        Contract contract = findContractById(contractId);

        // 권한 검증 - 계약의 채권자나 채무자만 접근 가능
        validateContractAccess(userId, contract, "해당 계약에 접근할 권한이 없습니다");

        // 기본 DTO 생성
        ContractDetailResponseDTO dto = ContractDetailResponseDTO.from(contract);

        // 지갑 정보 설정
        dto.setCrWallet(walletService.getUserPrimaryWalletAddress(contract.getCreditor()));
        dto.setDrWallet(walletService.getUserPrimaryWalletAddress(contract.getDebtor()));

        return dto;
    }

    /**
     * 계약 ID로 계약 조회
     * @param contractId 계약 ID
     * @return 계약 엔티티
     * @throws BusinessException 계약이 존재하지 않는 경우 발생
     */
    public Contract findContractById(Integer contractId) {
        return contractRepository.findByContractIdAndDeletedFlagFalse(contractId)
                .orElseThrow(() -> {
                    log.warn("[계약 조회 실패] 존재하지 않는 계약: {}", contractId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 계약입니다");
                });
    }

    /**
     * 사용자 ID로 사용자 조회
     * @param userId 사용자 ID
     * @return 사용자 엔티티
     * @throws BusinessException 사용자가 존재하지 않는 경우 발생
     */
    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[사용자 조회 실패] 존재하지 않는 사용자: {}", userId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 사용자입니다");
                });
    }

    /**
     * 계약 접근 권한 검증
     * @param userId 요청한 사용자 ID
     * @param contract 접근하려는 계약
     * @param errorMessage 권한 없을 때 표시할 오류 메시지
     * @throws BusinessException 권한이 없는 경우 발생
     */
    private void validateContractAccess(Integer userId, Contract contract, String errorMessage) {
        boolean isCreditor = Objects.equals(userId, contract.getCreditor().getUserId());
        boolean isDebtor = Objects.equals(userId, contract.getDebtor().getUserId());

        // boolean isAdmin = 향후 관리자 권한 체크 로직 (예: user.hasRole(Role.ADMIN))
        if (!isCreditor && !isDebtor /* && !isAdmin */) {
            log.warn("[접근 거부] 사용자({})는 계약({})에 접근할 권한이 없습니다",
                    userId, contract.getContractId());
            throw new BusinessException(ErrorCode.ACCESS_DENIED, errorMessage);
        }
    }

    /**
     * 토큰 ID로 채무자 조회
     * @param tokenId 토큰 ID
     * @return 채무자
     */
    public User getDebtorByTokenId(java.math.BigInteger tokenId) {
        Contract contract = contractRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 tokenId의 계약이 없습니다."));

        return contract.getDebtor();
    }
}