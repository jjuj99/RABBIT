package com.rabbit.contract.service;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.rabbit.user.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.rabbit.contract.domain.dto.request.ContractRejectRequestDTO;
import com.rabbit.contract.domain.dto.request.ContractRequestDTO;
import com.rabbit.contract.domain.dto.response.ContractResponseDTO;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.mail.service.MailService;
import com.rabbit.user.domain.entity.User;
import com.rabbit.user.repository.UserRepository;
import com.rabbit.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 계약 생성, 수정, 상태 변경 등의 명령 작업을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractCommandService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final SysCommonCodeService sysCommonCodeService;
    private final UserService userService;
    private final ContractQueryService contractQueryService;
    private final WalletService walletService;
    private final ContractProcessingService contractProcessingService;
    private final ContractNotificationHelper notificationHelper;
    private final MailService mailService;

    // 코드 타입 상수 정의
    private static final String CONTRACT_STATUS = SysCommonCodes.Contract.values()[0].getCodeType();

    /**
     * 계약 생성 또는 수정
     *
     * @param userId     채무자 ID
     * @param requestDTO 계약 요청 정보
     * @return 생성된 계약 정보
     */
    @Transactional
    public ContractResponseDTO createContract(Integer userId, ContractRequestDTO requestDTO) {
        // 채무자 조회
        User debtor = contractQueryService.findUserById(userId);

        // 채권자 조회 (이메일로)
        User creditor = findCreditorByEmail(requestDTO.getCrEmail());

        // 자기 자신과의 계약 생성 방지
        validateSelfContract(userId, creditor.getUserId());

        // 수정 요청인 경우 (계약 ID가 있는 경우)
        Integer contractIdParam = requestDTO.getContractId();
        if (contractIdParam != null) {
            // 이전 계약 조회
            Contract originalContract = contractQueryService.findContractById(contractIdParam);

            // 권한 검증 - 채무자만 수정 가능
            if (!Objects.equals(originalContract.getDebtor().getUserId(), userId)) {
                log.warn("[계약 수정 권한 없음] 사용자 ID: {}, 원본 계약 ID: {}", userId, contractIdParam);
                throw new BusinessException(ErrorCode.ACCESS_DENIED, "원본 계약의 채무자만 계약을 수정할 수 있습니다");
            }

            // 원본 계약 상태 검증 - MODIFICATION_REQUESTED 상태인지 확인
            if (originalContract.getContractStatus() != SysCommonCodes.Contract.MODIFICATION_REQUESTED) {
                log.warn("[잘못된 계약 상태] 원본 계약 ID: {}, 상태: {}",
                        contractIdParam, originalContract.getContractStatus());
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR,
                        "수정 요청된 계약만 수정할 수 있습니다");
            }

            // 원본 계약 취소 처리
            originalContract.updateStatus(SysCommonCodes.Contract.CANCELED);
            originalContract.setUpdatedAt(ZonedDateTime.now());
            contractRepository.save(originalContract);

            log.info("[원본 계약 취소 처리] 원본 계약 ID: {}, 새 상태: {}",
                    contractIdParam, SysCommonCodes.Contract.CANCELED);
        }

        // 계약 엔티티 생성
        Contract contract = Contract.from(requestDTO, creditor, debtor);

        // 계약 저장
        Contract savedContract = contractRepository.save(contract);
        log.info("[계약 생성 완료] 계약 ID: {}, 채권자: {}, 채무자: {}, 원본 계약 ID: {}",
                savedContract.getContractId(), creditor.getUserId(), debtor.getUserId(), contractIdParam);

        // 채권자 지갑 주소 확인
        String creditorWalletAddress = walletService.getUserPrimaryWalletAddressById(contract.getCreditor().getUserId());
        if (creditorWalletAddress == null || creditorWalletAddress.isBlank()) {
            creditorWalletAddress = "";
        }

        // 채무자 지갑 주소 확인
        String debtorWalletAddress = walletService.getUserPrimaryWalletAddressById(contract.getDebtor().getUserId());
        if (debtorWalletAddress == null || debtorWalletAddress.isBlank()) {
            debtorWalletAddress = "";
        }

        // 채권자에게 알림 발송
        notificationHelper.sendNotification(
                creditor.getUserId(),
                SysCommonCodes.NotificationType.CONTRACT_REQUESTED,
                SysCommonCodes.NotificationRelatedType.CONTRACT,
                savedContract.getContractId()
        );

        // 채권자에게 이메일 발송
        if (creditor.getEmail() != null) {
            try {
                mailService.sendMail(
                        creditor.getEmail(),
                        SysCommonCodes.MailTemplateType.CONTRACT_REQUESTED,
                        String.valueOf(savedContract.getContractId()),
                        debtor.getNickname(),
                        savedContract.getLoanAmount().toString()
                );
                log.info("[계약 요청 이메일 발송 완료] 계약 ID: {}, 수신자: {}",
                        savedContract.getContractId(), creditor.getEmail());
            } catch (Exception e) {
                log.error("[계약 요청 이메일 발송 실패] 계약 ID: {}, 수신자: {}, 오류: {}",
                        savedContract.getContractId(), creditor.getEmail(), e.getMessage());
                // 이메일 발송 실패는 비즈니스 로직을 방해하지 않도록 예외 전파하지 않음
            }
        }

        // 응답 생성
        ContractResponseDTO responseDTO = ContractResponseDTO.createFrom(savedContract, creditorWalletAddress, debtorWalletAddress);
        responseDTO.setContractStatusName(sysCommonCodeService.getCodeName(
                CONTRACT_STATUS, responseDTO.getContractStatus().getCode()));

        return responseDTO;
    }

    /**
     * 계약 취소
     *
     * @param contractId 계약 ID
     * @param userId     요청자 ID
     * @return 업데이트된 계약 정보
     */
    @Transactional
    public ContractResponseDTO cancelContract(Integer contractId, Integer userId) {
        Contract contract = contractQueryService.findContractById(contractId);

        // 계약 접근 권한 검증
        validateContractAccess(userId, contract, "해당 계약의 상태를 변경할 권한이 없습니다");

        // 상태 전이 검증
        validateStatusTransition(contract.getContractStatus(), SysCommonCodes.Contract.CANCELED, userId, contract);

        // 계약 취소
        contract.updateStatus(SysCommonCodes.Contract.CANCELED);

        Contract updatedContract = contractRepository.save(contract);
        log.info("[계약 취소 업데이트] 계약 ID: {}, 이전 상태: {}, 새 상태: {}, 요청자: {}",
                contractId, contract.getContractStatus(), SysCommonCodes.Contract.CANCELED, userId);

        // 계약 취소 알림 및 이메일 발송
        try {
            notificationHelper.sendContractCancelNotifications(updatedContract, userId);
            log.info("[계약 취소 알림 발송 완료] 계약 ID: {}", contract.getContractId());
        } catch (Exception e) {
            log.error("[계약 취소 알림 발송 실패] 계약 ID: {}, 오류: {}",
                    contract.getContractId(), e.getMessage());
            // 알림 실패는 비즈니스 로직을 방해하지 않도록 예외 전파하지 않음
        }

        return ContractResponseDTO.from(updatedContract);
    }

    /**
     * 계약 완료 처리 (NFT 생성, 자금 전송 등)
     *
     * @param contractId 계약 ID
     * @param userId     요청자 ID
     * @return 완료된 계약 정보
     */
    @Transactional
    public ContractResponseDTO completeContract(Integer contractId, Integer userId) {
        // 시작 시간 측정
        long startTime = System.currentTimeMillis();
        Contract contract = contractQueryService.findContractById(contractId);

        // 상태 전이 검증
        validateStatusTransition(
                contract.getContractStatus(),
                SysCommonCodes.Contract.CONTRACTED,
                userId,
                contract
        );

        // 계약 완료 처리 위임
        contractProcessingService.completeContractProcessing(contract);

        Contract updatedContract = contractRepository.save(contract);

        // 2. 채무자 지갑 주소 확인
        String debtorWalletAddress = walletService.getUserPrimaryWalletAddressById(contract.getDebtor().getUserId());
        if (debtorWalletAddress == null || debtorWalletAddress.isBlank()) {
            debtorWalletAddress = "";
        }

        log.info("[계약 완료 처리] 계약 ID: {}, 채권자: {}, 채무자: {}",
                contractId, contract.getCreditor().getUserId(), contract.getDebtor().getUserId());

        // PDF 생성, 이메일 및 알림 발송
        notificationHelper.sendContractCompletionNotifications(updatedContract);

        // 종료 시간 측정 및 소요 시간 로깅
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        log.info("[성능 측정] 계약 ID: {}, NFT 생성 총 소요 시간: {}ms", contract.getContractId(), elapsedTime);

        return ContractResponseDTO.successFrom(updatedContract, debtorWalletAddress);
    }

    /**
     * 계약 반려 (수정 요청, 취소됨)
     *
     * @param contractId 계약 ID
     * @param userId     요청자 ID
     * @param requestDTO 반려 사유
     * @return 반려된 계약 정보
     */
    @Transactional
    public ContractResponseDTO rejectContract(Integer contractId, Integer userId, ContractRejectRequestDTO requestDTO) {
        Contract contract = contractQueryService.findContractById(contractId);

        // 상태 전이 규칙 검증과 채무자 권한 검증
        validateRejectContractAccess(contract, userId);

        // 계약 상태 결정 (취소 또는 수정 요청)
        SysCommonCodes.Contract newStatus = requestDTO.isCanceled() ?
                SysCommonCodes.Contract.CANCELED :
                SysCommonCodes.Contract.REJECTED;

        // 상태 업데이트
        contract.updateStatus(newStatus);

        // 반려 메시지 저장
        // 반려 사유 설정 (비어있으면 기본 메시지 사용)
        String rejectMessage = StringUtils.hasText(requestDTO.getRejectMessage()) ?
                requestDTO.getRejectMessage() : "계약 반려 요청";

        contract.setRejectMessage(rejectMessage);
        contract.setRejectedAt(ZonedDateTime.now());

        Contract updatedContract = contractRepository.save(contract);
        log.debug("[반려 메시지 저장] 계약 ID: {}, 메시지: {}, 변경 상태: {}",
                contractId, rejectMessage, contract.getContractStatus());

        // 알림 및 이메일 발송
        notificationHelper.sendRejectNotifications(updatedContract, rejectMessage);

        return ContractResponseDTO.from(updatedContract);
    }

    /**
     * 계약 삭제 (논리적 삭제, 관리자)
     *
     * @param contractId 계약 ID
     * @param userId     요청자 ID
     */
    @Transactional
    public void deleteContract(Integer contractId, Integer userId) {
        Contract contract = contractQueryService.findContractById(contractId);

        // 권한 검증 - 관리자만 접근 가능
        validateAdminAccess(userId, contract, "해당 계약을 삭제할 권한이 없습니다");

        // 계약 상태 검증
        validateContractDeletable(contract);

        // 논리적 삭제 처리
        markContractAsDeleted(contract);

        log.info("[계약 삭제 완료] 계약 ID: {}, 요청자: {}", contractId, userId);
    }

    /**
     * 채권자 변경 (NFT 전송 후 호출)
     *
     * @param tokenId       NFT 토큰 ID
     * @param newCreditorId 새 채권자 ID
     */
    @Transactional
    public void changeCreditorByTokenId(BigInteger tokenId, Integer newCreditorId) {
        Contract contract = contractRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 tokenId의 계약이 없습니다."));

        User newCreditor = userService.findById(newCreditorId);

        contract.changeCreditor(newCreditor);
        contractRepository.save(contract);

        log.info("[채권자 변경] 계약 ID: {}, 토큰 ID: {}, 새 채권자 ID: {}",
                contract.getContractId(), tokenId, newCreditorId);
    }

    /**
     * 이메일로 채권자 조회
     *
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
     * 자기 자신과의 계약 생성 방지 검증
     *
     * @param debtorId   채무자 ID
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
     * 상태 전이 검증
     *
     * @param currentStatus 현재 상태
     * @param newStatus     새 상태
     * @param userId        요청자 ID
     * @param contract      계약 엔티티
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
     * 계약 접근 권한 검증
     *
     * @param userId       요청한 사용자 ID
     * @param contract     접근하려는 계약
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
     *
     * @param userId       요청한 사용자 ID
     * @param contract     접근하려는 계약
     * @param errorMessage 권한 없을 때 표시할 오류 메시지
     * @throws BusinessException 관리자가 아닌 경우 발생
     */
    private void validateAdminAccess(Integer userId, Contract contract, String errorMessage) {
        // 추후 관리자 권한 체크 로직 구현
        // boolean isAdmin = userService.isAdmin(userId);
        // if (!isAdmin) {
        log.warn("[접근 거부] 사용자({})는 관리자 권한이 없어 계약({})에 접근할 수 없습니다",
                userId, contract.getContractId());
        throw new BusinessException(ErrorCode.ACCESS_DENIED, errorMessage);
        // }
    }

    /**
     * 계약 반려 권한 및 상태 검증
     *
     * @param contract 계약 엔티티
     * @param userId   요청자 ID
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
     *
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

    /**
     * 계약을 삭제됨으로 표시 (논리적 삭제)
     *
     * @param contract 삭제할 계약
     */
    private void markContractAsDeleted(Contract contract) {
        contract.setDeletedFlag(true);
        contract.setUpdatedAt(ZonedDateTime.now());
        contractRepository.save(contract);
        log.debug("[계약 논리적 삭제] 계약 ID: {}, 상태: {}",
                contract.getContractId(), contract.getContractStatus());
    }
}