package com.rabbit.contract.domain.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;

import com.rabbit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.contract.domain.dto.request.ContractRequestDTO;

/**
 * 차용증 계약 엔티티
 */
@Entity
@Table(name = "contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    /**
     * 계약 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Integer contractId;

    /**
     * 채권자 (대출해주는 사람)
     */
    @ManyToOne
    @JoinColumn(name = "creditor_id", nullable = false)
    private User creditor;

    /**
     * 채무자 (대출받는 사람)
     */
    @ManyToOne
    @JoinColumn(name = "debtor_id", nullable = false)
    private User debtor;

    /**
     * 대출 금액
     */
    @Column(nullable = false)
    private BigDecimal loanAmount;

    /**
     * 연 이자율 (%)
     */
    @Column(nullable = false)
    private BigDecimal interestRate;

    /**
     * 대출 시작일
     */
    @Column(nullable = false)
    private ZonedDateTime contractDate;

    /**
     * 대출 만기일
     */
    @Column(nullable = false)
    private ZonedDateTime maturityDate;

    /**
     * 중도상환 이자율 (%)
     */
    @Column(nullable = true)
    private BigDecimal prepaymentInterestRate;

    /**
     * 납부 유예 일수
     */
    @Column(nullable = false)
    private Integer graceLineDays;

    /**
     * NFT 토큰 ID
     */
    @Column(nullable = true, columnDefinition = "BIGINT")
    private BigInteger tokenId;

    /**
     * NFT 이미지 URL
     */
    @Column(nullable = true)
    private String nftImageUrl;

    /**
     * 계약 조항 (추가 조항)
     */
    @Column(nullable = true, length = 1000)
    private String contractTerms;

    /**
     * 상환 방식 (EPIP: 원리금균등상환, EPP: 원금균등상환, BP: 만기일시상환)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "repayment_type", nullable = false, length = 50)
    private SysCommonCodes.Repayment repaymentType;

    /**
     * 월 납입일 (매월 n일)
     */
    @Column(nullable = true)
    private Integer monthlyPaymentDate;

    /**
     * 연체 이자율 (%)
     */
    @Column(nullable = true)
    private BigDecimal defaultInterestRate;

    /**
     * 기한이익상실 연체 횟수
     */
    @Column(nullable = true)
    private Integer defaultCount;

    /**
     * 중도 상환 가능 여부
     */
    @Column(nullable = true)
    private Boolean earlyPayment;

    /**
     * 차용증 양도 가능 여부
     */
    @Column(nullable = true)
    private Boolean promissoryNoteTransferabilityFlag;

    /**
     * 대출 기간 (개월 수)
     */
    @Column(nullable = true)
    private Integer loanTerm;

    /**
     * 계약 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "contract_status", nullable = false, length = 50)
    private SysCommonCodes.Contract contractStatus;

    /**
     * 생성일
     */
    @Column(nullable = false)
    private ZonedDateTime createdAt;

    /**
     * 수정일
     */
    @Column(nullable = true)
    private ZonedDateTime updatedAt;

    /**
     * 삭제 여부
     */
    @Column(nullable = false)
    private boolean deletedFlag;

    /**
     * 메시지 (채무자)
     */
    @Column(nullable = true, length = 500)
    private String message;

    /**
     * 반려 메시지
     */
    @Column(nullable = true, length = 500)
    private String rejectMessage;

    /**
     * 반려 일시
     */
    @Column(nullable = true)
    private ZonedDateTime rejectedAt;

    // 새로 추가된 필드: 암호화 키 (마스터 키로 암호화된 상태로 저장)
    @Column(name = "encrypted_contract_key", nullable = true, length = 1024)
    private String encryptedContractKey;

    // 생성된 키 버전 (키 순환 대비)
    @Column(name = "key_version", nullable = true)
    private Integer keyVersion;

    // 확장 개념
//    /**
//     * 채권자에게 숨김 여부
//     */
//    @Column(nullable = false, columnDefinition = "boolean default false")
//    private boolean hidden_from_creditor_flag;
//
//    /**
//     * 채무자에게 숨김 여부
//     */
//    @Column(nullable = false, columnDefinition = "boolean default false")
//    private boolean hidden_from_debtor_flag;

    // 비즈니스 로직 메서드들

    /**
     * 계약 취소
     */
    public void cancel() {
        this.contractStatus = SysCommonCodes.Contract.CANCELED;
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * 계약 완료 처리 (체결됨으로 변경)
     */
    public void complete() {
        this.contractStatus = SysCommonCodes.Contract.CONTRACTED;
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * NFT 정보 설정
     * @param tokenId NFT 토큰 ID
//     * @param imageUrl NFT 이미지 URL
     */
    public void setNftInfo(BigInteger tokenId, String imageUrl ) {
        this.tokenId = tokenId;
        this.nftImageUrl = imageUrl;
    }

    /**
     * 계약 상태 변경
     * @param newStatus 새로운 계약 상태
     */
    public void updateStatus(SysCommonCodes.Contract newStatus) {
        this.contractStatus = newStatus;
        this.updatedAt = ZonedDateTime.now();

        if (SysCommonCodes.Contract.CONTRACTED.equals(newStatus)) {
            this.contractDate = ZonedDateTime.now()
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);
            this.maturityDate = contractDate.plusMonths(this.loanTerm).minusDays(1);
            this.updatedAt = ZonedDateTime.now();
        } else if (SysCommonCodes.Contract.CANCELED.equals(newStatus)) {
            this.updatedAt = ZonedDateTime.now();
        } else if (SysCommonCodes.Contract.REJECTED.equals(newStatus)) {
            this.rejectedAt = ZonedDateTime.now();
        } else if (SysCommonCodes.Contract.REQUESTED.equals(newStatus)) {
            // 요청 상태에서 특별히 설정할 필드가 없음
        } else if (SysCommonCodes.Contract.MODIFICATION_REQUESTED.equals(newStatus)) {
            this.rejectedAt = ZonedDateTime.now();
        }
    }

//    /**
//     * 계약 가시성 설정 (특정 사용자에게 숨기기)
//     * @param userId 사용자 ID
//     * @param hidden 숨김 여부
//     */
//    public void setHiddenForUser(Integer userId, boolean hidden) {
//        if (getCreditor().getUserId().equals(userId)) {
//            this.hidden_from_creditor_flag = hidden;
//        } else if (getDebtor().getUserId().equals(userId)) {
//            this.hidden_from_debtor_flag = hidden;
//        }
//
//        this.updatedAt = ZonedDateTime.now();
//    }
//
//    /**
//     * 특정 사용자에게 계약이 삭제되었는지 확인
//     * @param userId 사용자 ID
//     * @return 해당 사용자에게 계약이 삭제된 상태인지 여부
//     */
//    public boolean isHiddenForUser(Integer userId) {
//        if (getCreditor() != null && getCreditor().getUserId().equals(userId)) {
//            return hidden_from_creditor_flag;
//        } else if (getDebtor() != null && getDebtor().getUserId().equals(userId)) {
//            return hidden_from_debtor_flag;
//        }
//        return false; // 해당 사용자가 계약의 당사자가 아닌 경우
//    }

    /**
     * 암호화 키 설정
     * @param encryptedKey 마스터 키로 암호화된 계약 키
     * @param version 키 버전
     */
    public void setEncryptionKey(String encryptedKey, Integer version) {
        this.encryptedContractKey = encryptedKey;
        this.keyVersion = version;
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * ContractRequestDTO로부터 Contract 엔티티 생성
     * @param dto 계약 요청 DTO
     * @param creditor 채권자 (대출해주는 사람)
     * @param debtor 채무자 (대출받는 사람)
     * @return 계약 엔티티
     */
    public static Contract from(ContractRequestDTO dto, User creditor, User debtor) {
        return Contract.builder()
                .creditor(creditor)
                .debtor(debtor)
                .loanAmount(dto.getLa())
                .interestRate(dto.getIr())
                .contractDate(dto.getContractDt())
                .maturityDate(dto.calculateMaturityDate())
                .prepaymentInterestRate(dto.getEarlypayFee())
                .graceLineDays(7) // 기본값 설정 또는 설정 정보에서 가져옴
                .contractTerms(dto.getAddTerms())
                .repaymentType(SysCommonCodes.Repayment.fromCode(dto.getRepayType()))
                .monthlyPaymentDate(dto.getMpDt())
                .defaultInterestRate(dto.getDir())
                .defaultCount(dto.getDefCnt())
                .earlyPayment(dto.getEarlypay())
                .promissoryNoteTransferabilityFlag(dto.getPnTransFlag())
                .loanTerm(dto.getLt())
                .message(dto.getMessage())
                .contractStatus(SysCommonCodes.Contract.REQUESTED) // 초기 상태는 REQUESTED
                .createdAt(ZonedDateTime.now())
                .deletedFlag(false)
                .build();
    }

    public void changeCreditor(User newCreditor) {
        this.creditor = newCreditor;
        this.updatedAt = ZonedDateTime.now();
    }

}