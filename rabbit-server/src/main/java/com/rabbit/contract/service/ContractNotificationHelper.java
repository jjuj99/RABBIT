package com.rabbit.contract.service;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.mail.service.ExtendedMailService;
import com.rabbit.notification.domain.dto.request.NotificationRequestDTO;
import com.rabbit.notification.service.NotificationService;
import com.rabbit.user.domain.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 계약 관련 알림 및 이메일 발송 기능을 담당하는 헬퍼 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContractNotificationHelper {

    private final NotificationService notificationService;
    private final ExtendedMailService extendedMailService;
    private final ContractPdfService contractPdfService;
    private final WhiteThemePdfService whiteThemePdfService;

    /**
     * 알림 발송 헬퍼 메서드
     * @param userId 사용자 ID
     * @param type 알림 유형
     * @param relatedType 관련 엔티티 유형
     * @param relatedId 관련 엔티티 ID
     */
    public void sendNotification(Integer userId, SysCommonCodes.NotificationType type,
                                 SysCommonCodes.NotificationRelatedType relatedType, Integer relatedId) {
        NotificationRequestDTO notificationRequest = NotificationRequestDTO.builder()
                .userId(userId)
                .type(type)
                .relatedType(relatedType)
                .relatedId(relatedId)
                .build();

        notificationService.createNotification(notificationRequest);
        log.debug("[알림 발송] 사용자: {}, 유형: {}, 관련 ID: {}", userId, type, relatedId);
    }

    /**
     * 계약 완료 시 알림, 이메일 발송
     * @param contract 계약 엔티티
     */
    public void sendContractCompletionNotifications(Contract contract) {
        try {
            // PDF 생성
//            byte[] pdfBytes = contractPdfService.generateContractPdf(contract);
            byte[] pdfBytes = whiteThemePdfService.generateWhiteThemeContractPdf(contract);
            // 채무자에게 이메일 및 알림 발송
            User debtor = contract.getDebtor();
            if (debtor.getEmail() != null) {
                extendedMailService.sendContractCompletedEmail(
                        debtor.getEmail(),
                        contract.getContractId(),
                        contract.getCreditor().getNickname(),
                        pdfBytes
                );
            }

            // 채무자에게 알림 발송
            sendNotification(
                    debtor.getUserId(),
                    SysCommonCodes.NotificationType.CONTRACT_COMPLETED,
                    SysCommonCodes.NotificationRelatedType.CONTRACT,
                    contract.getContractId()
            );

            // 채권자에게 이메일 및 알림 발송
            User creditor = contract.getCreditor();
            if (creditor.getEmail() != null) {
                extendedMailService.sendContractCompletedEmail(
                        creditor.getEmail(),
                        contract.getContractId(),
                        contract.getDebtor().getNickname(),
                        pdfBytes
                );
            }

            // 채권자에게 알림 발송
            sendNotification(
                    creditor.getUserId(),
                    SysCommonCodes.NotificationType.CONTRACT_COMPLETED,
                    SysCommonCodes.NotificationRelatedType.CONTRACT,
                    contract.getContractId()
            );

            log.info("[계약 완료 알림 발송 완료] 계약 ID: {}", contract.getContractId());
        } catch (Exception e) {
            log.error("[계약 완료 알림 발송 실패] 계약 ID: {}, 오류: {}",
                    contract.getContractId(), e.getMessage(), e);
            // 알림 실패는 비즈니스 로직에 영향을 주지 않도록 예외를 전파하지 않음
        }
    }

    /**
     * 계약 취소 알림 발송
     * @param contract 계약 엔티티
     * @param userId 취소를 요청한 사용자 ID
     */
    public void sendContractCancelNotifications(Contract contract, Integer userId) {
        try {
            // 채권자가 취소한 경우 채무자에게 알림
            if (Objects.equals(userId, contract.getCreditor().getUserId())) {
                User debtor = contract.getDebtor();

                // 알림 발송
                sendNotification(
                        debtor.getUserId(),
                        SysCommonCodes.NotificationType.CONTRACT_CANCELED,
                        SysCommonCodes.NotificationRelatedType.CONTRACT,
                        contract.getContractId()
                );

                // 이메일 발송
                if (debtor.getEmail() != null) {
                    extendedMailService.sendContractCanceledEmail(
                            debtor.getEmail(),
                            contract.getContractId(),
                            contract.getCreditor().getNickname()
                    );
                }
            }
            // 채무자가 취소한 경우 채권자에게 알림
            else if (Objects.equals(userId, contract.getDebtor().getUserId())) {
                User creditor = contract.getCreditor();

                // 알림 발송
                sendNotification(
                        creditor.getUserId(),
                        SysCommonCodes.NotificationType.CONTRACT_CANCELED,
                        SysCommonCodes.NotificationRelatedType.CONTRACT,
                        contract.getContractId()
                );

                // 이메일 발송
                if (creditor.getEmail() != null) {
                    extendedMailService.sendContractCanceledEmail(
                            creditor.getEmail(),
                            contract.getContractId(),
                            contract.getDebtor().getNickname()
                    );
                }
            }

            log.info("[계약 취소 알림 발송 완료] 계약 ID: {}, 요청자: {}", contract.getContractId(), userId);
        } catch (Exception e) {
            log.error("[계약 취소 알림 발송 실패] 계약 ID: {}, 요청자: {}, 오류: {}",
                    contract.getContractId(), userId, e.getMessage(), e);
            // 알림 실패는 비즈니스 로직에 영향을 주지 않도록 예외를 전파하지 않음
        }
    }

    /**
     * 계약 반려 시 알림, 이메일 발송
     * @param contract 계약 엔티티
     * @param rejectMessage 반려 사유
     */
    public void sendRejectNotifications(Contract contract, String rejectMessage) {
        try {
            // 알림 유형 결정 (취소 또는 수정 요청)
            SysCommonCodes.NotificationType notificationType = contract.getContractStatus() == SysCommonCodes.Contract.CANCELED ?
                    SysCommonCodes.NotificationType.CONTRACT_CANCELED :
                    SysCommonCodes.NotificationType.CONTRACT_MODIFICATION_REQUESTED;

            // 채무자에게 알림 및 이메일 발송
            User debtor = contract.getDebtor();

            // 채무자에게 알림 발송
            sendNotification(
                    debtor.getUserId(),
                    notificationType,
                    SysCommonCodes.NotificationRelatedType.CONTRACT,
                    contract.getContractId()
            );

            // 취소인 경우 이메일 발송
            if (contract.getContractStatus() == SysCommonCodes.Contract.CANCELED && debtor.getEmail() != null) {
                extendedMailService.sendContractCanceledEmail(
                        debtor.getEmail(),
                        contract.getContractId(),
                        contract.getCreditor().getNickname()
                );
            }
            // 수정 요청인 경우 PDF 첨부 이메일 발송
            else if (contract.getContractStatus() == SysCommonCodes.Contract.MODIFICATION_REQUESTED && debtor.getEmail() != null) {
                byte[] pdfBytes = contractPdfService.generateContractPdf(contract);

                extendedMailService.sendContractModificationRequestEmail(
                        debtor.getEmail(),
                        contract.getContractId(),
                        contract.getCreditor().getNickname(),
                        rejectMessage,
                        pdfBytes
                );
            }

            log.info("[계약 반려 알림 발송 완료] 계약 ID: {}, 상태: {}",
                    contract.getContractId(), contract.getContractStatus());
        } catch (Exception e) {
            log.error("[계약 반려 알림 발송 실패] 계약 ID: {}, 오류: {}",
                    contract.getContractId(), e.getMessage(), e);
            // 알림 실패는 비즈니스 로직에 영향을 주지 않도록 예외를 전파하지 않음
        }
    }
}