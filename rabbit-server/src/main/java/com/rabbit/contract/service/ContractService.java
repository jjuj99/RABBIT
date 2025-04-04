package com.rabbit.contract.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.rabbit.contract.domain.dto.request.ContractRejectRequestDTO;
import com.rabbit.contract.domain.dto.response.ContractListResponseDTO;
import com.rabbit.user.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.contract.domain.dto.request.ContractRequestDTO;
import com.rabbit.contract.domain.dto.request.ContractSearchRequestDTO;
import com.rabbit.contract.domain.dto.response.ContractConfigResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractDetailResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractResponseDTO;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.global.code.service.SysCommonCodeService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final SysCommonCodeService sysCommonCodeService;
    // NFT 관련 서비스 주입 필요
    // private final NftService nftService;

    // 코드 타입 상수 정의
    private static final String CONTRACT_STATUS = SysCommonCodes.Contract.values()[0].getCodeType();

    /**
     * 계약 설정 정보 조회
     * DB 저장하고 cache 처리 예정
     * @return 계약 설정 정보
     */
    public ContractConfigResponseDTO getContractConfig() {
        // 실제로는 DB나 설정 파일에서 가져올 수 있습니다
        return ContractConfigResponseDTO.builder()
                .maxLegalIr(new BigDecimal("20.0"))
                .defDir(new BigDecimal("15.0"))
                .defDefCnt(3)
                .defEarlypayFee(new BigDecimal("2.0"))
                .defGraceDays(7)
                .minLa(new BigDecimal("10000"))
                .maxLa(new BigDecimal("10000000"))
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
            // 사용자가 채권자(대출해주는 사람)인 계약 조회
            contracts = contractRepository.findByCreditor(user);
        } else if ("received".equals(type)) {
            // 사용자가 채무자(대출받는 사람)인 계약 조회
            contracts = contractRepository.findByDebtor(user);
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

    @Transactional
    public ContractResponseDTO createContract(Integer userId, ContractRequestDTO requestDTO) {
        // 채무자 조회
        User debtor = findUserById(userId);

        // 채권자 조회
        User creditor = findCreditorByEmail(requestDTO.getCrEmail());

        // 자기 자신과의 계약 생성 방지
        validateSelfContract(userId, creditor.getUserId());

        // 계약 엔티티 생성
        Contract contract = Contract.from(requestDTO, creditor, debtor);

        // 계약 저장
        Contract savedContract = contractRepository.save(contract);
        log.info("[계약 생성 완료] 계약 ID: {}, 채권자: {}, 채무자: {}",
                savedContract.getContractId(), creditor.getUserId(), debtor.getUserId());

        // 응답 생성
        ContractResponseDTO responseDTO = ContractResponseDTO.from(savedContract);
        responseDTO.setContractStatusName(sysCommonCodeService.getCodeName(
                CONTRACT_STATUS, responseDTO.getContractStatus().getCode()));

        return responseDTO;
    }

    /**
     * 이메일로 채권자 조회
     * @param email 채권자 이메일
     * @return 채권자 엔티티
     * @throws BusinessException 채권자가 존재하지 않는 경우 발생
     */
    private User findCreditorByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[채권자 조회 실패] 존재하지 않는 이메일: {}", email);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 채권자 이메일입니다");
                });
    }

    /**
     * 계약 취소
     * @param contractId 계약 ID
     * @param userId 요청자 ID
     * @return 업데이트된 계약 정보
     */
    @Transactional
    public ContractResponseDTO cancelContract(Integer contractId, Integer userId) {
        Contract contract = findContractById(contractId);

        // 사용자 조회
        User user = findUserById(userId);

        // 계약 접근 권한 검증
        validateContractAccess(userId, contract, "해당 계약의 상태를 변경할 권한이 없습니다");

        // 상태 전이 검증
        validateStatusTransition(contract.getContractStatus(), SysCommonCodes.Contract.CANCELED, userId, contract);

        // 계약 취소
        contract.updateStatus(SysCommonCodes.Contract.CANCELED);

        Contract updatedContract = contractRepository.save(contract);
        log.info("[계약 취소 업데이트] 계약 ID: {}, 이전 상태: {}, 새 상태: {}, 요청자: {}",
                contractId, contract.getContractStatus(), SysCommonCodes.Contract.CANCELED, userId);

        return ContractResponseDTO.from(updatedContract);
    }

    /**
     * 계약 완료 처리 (NFT 생성, 자금 전송 등)
     * @param contractId 계약 ID
     * @param userId 요청자 ID
     * @return 완료된 계약 정보
     */
    @Transactional
    public ContractResponseDTO completeContract(Integer contractId, Integer userId) {
        Contract contract = findContractById(contractId);

        // 이미 구현된 상태 전이 검증 메서드 활용
        validateStatusTransition(
                contract.getContractStatus(),
                SysCommonCodes.Contract.CONTRACTED,
                userId,
                contract
        );

        // 계약 완료 처리
        completeContract(contract);

        Contract updatedContract = contractRepository.save(contract);
        log.info("[계약 완료 처리] 계약 ID: {}, 채권자: {}, 채무자: {}",
                contractId, contract.getCreditor().getUserId(), contract.getDebtor().getUserId());

        return ContractResponseDTO.from(updatedContract);
    }

    /**
     * 계약 반려 (수정 요청, 취소됨)
     * @param contractId 계약 ID
     * @param userId 요청자 ID
     * @param requestDTO 반려 사유
     * @return 반려된 계약 정보
     */
    @Transactional
    public ContractResponseDTO rejectContract(Integer contractId, Integer userId, ContractRejectRequestDTO requestDTO) {
        Contract contract = findContractById(contractId);

        // 상태 전이 규칙 검증과 채무자 권한 검증
        validateRejectContractAccess(contract, userId);

        contract.setContractStatus(requestDTO.isCanceled()? SysCommonCodes.Contract.CANCELED : SysCommonCodes.Contract.MODIFICATION_REQUESTED);

        // 상태 업데이트
        contract.updateStatus(contract.getContractStatus());

        // 반려 메시지 저장
        // 반려 사유 설정 (비어있으면 기본 메시지 사용)
        String rejectMessage = StringUtils.hasText(requestDTO.getRejectMessage()) ? requestDTO.getRejectMessage() : "계약 반려 요청`";

        contract.setRejectMessage(rejectMessage);
        contract.setCreatedAt(ZonedDateTime.now());

        contractRepository.save(contract);

        log.debug("[반려 메시지 저장] 계약 ID: {}, 메시지: {}, 변경 상태: {}", contractId, rejectMessage, contract.getContractStatus());
        Contract updatedContract = contractRepository.save(contract);

        return ContractResponseDTO.from(updatedContract);
    }

    /**
     * 계약 삭제 (논리적 삭제, 관리자)
     * @param contractId 계약 ID
     * @param userId 요청자 ID
     */
    @Transactional
    public void deleteContract(Integer contractId, Integer userId) {
        Contract contract = findContractById(contractId);

        // 권한 검증 - 관리자만 접근 가능
        validateAdminAccess(userId, contract, "해당 계약을 삭제할 권한이 없습니다");

        // 계약 상태 검증
        validateContractDeletable(contract);

        // 논리적 삭제 처리
        markContractAsDeleted(contract);

        log.info("[계약 삭제 완료] 계약 ID: {}, 요청자: {}", contractId, userId);
    }

    /**
     * 계약을 삭제됨으로 표시 (논리적 삭제)
     * @param contract 삭제할 계약
     */
    private void markContractAsDeleted(Contract contract) {
        contract.setDeletedFlag(true);
        contract.setUpdatedAt(ZonedDateTime.now());
        contractRepository.save(contract);
        log.debug("[계약 논리적 삭제] 계약 ID: {}, 상태: {}",
                contract.getContractId(), contract.getContractStatus());
    }

    /**
     * 계약 ID로 계약 조회
     * @param contractId 계약 ID
     * @return 계약 엔티티
     * @throws BusinessException 계약이 존재하지 않는 경우 발생
     */
    private Contract findContractById(Integer contractId) {
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
    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[사용자 조회 실패] 존재하지 않는 사용자: {}", userId);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 사용자입니다");
                });
    }

    /**
     * 상태 전이 검증
     * @param currentStatus 현재 상태
     * @param newStatus 새 상태
     * @param userId 요청자 ID
     * @param contract 계약 엔티티
     */
    private void validateStatusTransition(SysCommonCodes.Contract currentStatus, SysCommonCodes.Contract newStatus, Integer userId, Contract contract) {
        boolean isCreditor = contract.getCreditor().getUserId().equals(userId);
        boolean isDebtor = contract.getDebtor().getUserId().equals(userId);

        // 각 상태 전이 규칙 정의
        switch (currentStatus) {
            case REQUESTED:
                if (newStatus != SysCommonCodes.Contract.MODIFICATION_REQUESTED &&
                        newStatus != SysCommonCodes.Contract.CONTRACTED &&
                        newStatus != SysCommonCodes.Contract.REJECTED &&
                        newStatus != SysCommonCodes.Contract.CANCELED) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청됨 상태에서는 수정 요청, 거절, 체결 또는 취소 상태로만 변경 가능합니다");
                }
                if (newStatus == SysCommonCodes.Contract.MODIFICATION_REQUESTED && !isCreditor) {
                    throw new BusinessException(ErrorCode.ACCESS_DENIED, "채권자만 계약 수정을 요청할 수 있습니다");
                }
                if (newStatus == SysCommonCodes.Contract.REJECTED && !isCreditor) {
                    throw new BusinessException(ErrorCode.ACCESS_DENIED, "채권자만 계약을 거절할 수 있습니다");
                }
                if (newStatus == SysCommonCodes.Contract.CONTRACTED && !isCreditor) {
                    throw new BusinessException(ErrorCode.ACCESS_DENIED, "채권자만 계약을 체결할 수 있습니다");
                }
                if (newStatus == SysCommonCodes.Contract.CANCELED && !isDebtor) {
                    throw new BusinessException(ErrorCode.ACCESS_DENIED, "채무자만 계약을 취소할 수 있습니다");
                }
                break;

            case MODIFICATION_REQUESTED:
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "수정 요청된 계약의 상태는 변경할 수 없습니다");

            case REJECTED:
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "거절된 계약의 상태는 변경할 수 없습니다");

            case CONTRACTED:
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "체결된 계약의 상태는 변경할 수 없습니다");

            case CANCELED:
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "취소된 계약의 상태는 변경할 수 없습니다");

            default:
                throw new BusinessException(ErrorCode.INVALID_TYPE_VALUE, "알 수 없는 계약 상태입니다");
        }
    }

    /**
     * 계약 완료 처리 (NFT 생성, 자금 전송 등)
     * @param contract 계약 엔티티
     */
    private void completeContract(Contract contract) {
        // 1. NFT 생성
        BigInteger tokenId = generateNFT(contract);

        // 2. 자금 전송
        transferFunds(contract);

        // 3. 상태 업데이트
        contract.updateStatus(SysCommonCodes.Contract.CONTRACTED);
    }

    /**
     * NFT 생성
     * @param contract 계약 엔티티
     * @return 생성된 NFT 토큰 ID
     */
    private BigInteger generateNFT(Contract contract) {
        // 실제 NFT 생성 로직 구현 필요
        // 예시 코드
        BigInteger tokenId = new BigInteger("1");
        String imageUrl = "https://example.com/nft/" + tokenId + ".png";

        // 생성된 NFT 정보 설정
        contract.setNftInfo(tokenId, imageUrl);

        return tokenId;
    }

    /**
     * 자금 전송
     * @param contract 계약 엔티티
     */
    private void transferFunds(Contract contract) {
        // 실제 자금 전송 로직 구현 필요
        // 예: 블록체인 트랜잭션, 내부 계좌 이체 등
        log.info("자금 전송: {} -> {}, 금액: {}",
                contract.getCreditor().getNickname(),
                contract.getDebtor().getNickname(),
                contract.getLoanAmount());
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
     * 관리자 전용 계약 접근 권한 검증
     * @param userId 요청한 사용자 ID
     * @param contract 접근하려는 계약
     * @param errorMessage 권한 없을 때 표시할 오류 메시지
     * @throws BusinessException 관리자가 아닌 경우 발생
     */
    private void validateAdminAccess(Integer userId, Contract contract, String errorMessage) {
//        boolean isAdmin = 향후 관리자 권한 체크 로직 (예: user.hasRole(Role.ADMIN))

//        if (!isAdmin) {
//            log.warn("[접근 거부] 사용자({})는 관리자 권한이 없어 계약({})에 접근할 수 없습니다",
//                    userId, contract.getContractId());
        throw new BusinessException(ErrorCode.ACCESS_DENIED, errorMessage);
//        }
//
//        log.debug("[관리자 접근] 관리자({})가 계약({})에 접근합니다",
//                userId, contract.getContractId());
    }

    /**
     * 자기 자신과의 계약 생성 방지 검증
     * @param debtorId 채무자 ID
     * @param creditorId 채권자 ID
     * @throws BusinessException 자기 자신과 계약을 생성하려는 경우 발생
     */
    private void validateSelfContract(Integer debtorId, Integer creditorId) {
        if (Objects.equals(debtorId, creditorId)) {
            log.warn("[계약 생성 실패] 자기 자신과 계약 생성 시도: 사용자 ID = {}", debtorId);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "자기 자신과 계약을 생성할 수 없습니다");
        }
    }

    /**
     * 계약 반려 권한 및 상태 검증
     * @param contract 계약 엔티티
     * @param userId 요청자 ID
     * @throws BusinessException 권한이 없거나 상태가 맞지 않는 경우 발생
     */
    private void validateRejectContractAccess(Contract contract, Integer userId) {
        // 채권자 권한 검증
        boolean isCreditor = Objects.equals(userId, contract.getCreditor().getUserId());
        if (!isCreditor) {
            log.warn("[접근 거부] 사용자({})는 계약({})의 채권자가 아닙니다",
                    userId, contract.getContractId());
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "채권자만 계약을 반려할 수 있습니다");
        }

        // 상태 검증
        if (contract.getContractStatus() != SysCommonCodes.Contract.REQUESTED) {
            log.warn("[상태 불일치] 계약 ID: {}, 현재 상태: {}, 기대 상태: {}",
                    contract.getContractId(), contract.getContractStatus(), SysCommonCodes.Contract.REQUESTED);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "요청됨 상태의 계약만 반려할 수 있습니다");
        }
    }

    /**
     * 계약 삭제 가능 여부 검증
     * @param contract 삭제할 계약
     * @throws BusinessException 삭제할 수 없는 상태인 경우 발생
     */
    private void validateContractDeletable(Contract contract) {
        // 체결된 계약은 삭제 불가
        if (contract.getContractStatus() == SysCommonCodes.Contract.CONTRACTED) {
            log.warn("[삭제 불가] 체결된 계약 삭제 시도: 계약 ID = {}, 상태 = {}",
                    contract.getContractId(), contract.getContractStatus());
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "체결된 계약은 삭제할 수 없습니다");
        }
    }
}