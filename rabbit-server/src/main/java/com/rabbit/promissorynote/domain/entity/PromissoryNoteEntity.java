package com.rabbit.promissorynote.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "promissory_note")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromissoryNoteEntity {

    /**
     * 블록체인 토큰 ID
     */
    @Id
    @Column(name = "token_id", columnDefinition = "BIGINT")
    private BigInteger tokenId;

    /**
     * 채권자 이름
     */
    @Column(name = "creditor_name", nullable = false)
    private String creditorName;

    /**
     * 채권자 블록체인 지갑 주소
     */
    @Column(name = "creditor_wallet_address", nullable = false, length = 42)
    private String creditorWalletAddress;

    /**
     * 채권자 서명
     */
    @Column(name = "creditor_sign", nullable = false, columnDefinition = "TEXT")
    private String creditorSign;

    /**
     * 채권자 정보 해시값
     */
    @Column(name = "creditor_info_hash", nullable = false, length = 128)
    private String creditorInfoHash;

    /**
     * 채무자 이름
     */
    @Column(name = "debtor_name", nullable = false)
    private String debtorName;

    /**
     * 채무자 블록체인 지갑 주소
     */
    @Column(name = "debtor_wallet_address", nullable = false, length = 42)
    private String debtorWalletAddress;

    /**
     * 채무자 서명
     */
    @Column(name = "debtor_sign", nullable = false, columnDefinition = "TEXT")
    private String debtorSign;

    /**
     * 채무자 정보 해시값
     */
    @Column(name = "debtor_info_hash", nullable = false, length = 128)
    private String debtorInfoHash;

    /**
     * 대출 금액
     */
    @Column(name = "loan_amount", nullable = false)
    private Long loanAmount;

    /**
     * 이자율 (%)
     */
    @Column(name = "interest_rate", nullable = false)
    private Integer interestRate;

    /**
     * 대출 기간 (개월)
     */
    @Column(name = "loan_term", nullable = false)
    private Integer loanTerm;

    /**
     * 상환 유형 (원리금균등상환, 원금균등상환, 만기일시상환 등)
     */
    @Column(name = "repayment_type", nullable = false, length = 50)
    private String repaymentType;

    /**
     * 만기일
     */
    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    /**
     * 월 납부일 (1-31)
     */
    @Column(name = "monthly_payment_date", nullable = false)
    private Integer monthlyPaymentDate;

    /**
     * 연체 이자율 (%)
     */
    @Column(name = "default_interest_rate", nullable = false)
    private Integer defaultInterestRate;

    /**
     * 계약 체결일
     */
    @Column(name = "contract_date", nullable = false)
    private LocalDate contractDate;

    /**
     * 조기 상환 가능 여부
     */
    @Column(name = "earlypay_flag", nullable = false)
    private boolean earlypayFlag;

    /**
     * 조기 상환 수수료 비율 (%)
     */
    @Column(name = "earlypay_fee", nullable = false)
    private Integer earlypayFee;

    /**
     * 기한이익상실 조항 (연체 횟수)
     */
    @Column(name = "acceleration_clause", nullable = false)
    private Integer accelerationClause;

    /**
     * 추가 계약 조항
     */
    @Column(name = "add_terms", columnDefinition = "TEXT")
    private String addTerms;

    /**
     * 추가 계약 조항 해시값
     */
    @Column(name = "add_terms_hash", columnDefinition = "TEXT")
    private String addTermsHash;

    /**
     * NFT 이미지 URI
     */
    @Column(name = "nft_image", columnDefinition = "TEXT")
    private String nftImage;

    /**
     * 삭제 여부
     */
    @Column(name = "deleted_flag", nullable = false)
    private boolean deletedFlag;

    /**
     * 생성 일시
     */
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    /**
     * NFT 삭제 여부 설정
     * @param deleted 삭제 여부
     */
    public void setDeletedFlag(boolean deleted) {
        this.deletedFlag = deleted;
    }
}