package com.rabbit.blockchain.domain.entity;

import java.time.LocalDate;
import java.time.Instant;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 차용증 NFT 엔티티
 */
@Entity
@Table(name = "promissory_note")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromissoryNoteEntity {

    @Id
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "creditor_name", nullable = false)
    private String creditorName;

    @Column(name = "creditor_wallet_address", nullable = false, length = 42)
    private String creditorWalletAddress;

    @Column(name = "creditor_sign", nullable = false, columnDefinition = "TEXT")
    private String creditorSign;

    @Column(name = "creditor_info_hash", nullable = false, length = 128)
    private String creditorInfoHash;

    @Column(name = "debtor_name", nullable = false)
    private String debtorName;

    @Column(name = "debtor_wallet_address", nullable = false, length = 42)
    private String debtorWalletAddress;

    @Column(name = "debtor_sign", nullable = false, columnDefinition = "TEXT")
    private String debtorSign;

    @Column(name = "debtor_info_hash", nullable = false, length = 128)
    private String debtorInfoHash;

    // 계산이 많은 컬럼은 원시 타입으로
    @Column(name = "loan_amount", nullable = false)
    private Long loanAmount;

    @Column(name = "interest_rate", nullable = false)
    private Integer interestRate;

    @Column(name = "loan_term", nullable = false)
    private Integer loanTerm;

    @Column(name = "repayment_type", nullable = false, length = 50)
    private String repaymentType;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "monthly_payment_date", nullable = false)
    private Integer monthlyPaymentDate;

    @Column(name = "default_interest_rate", nullable = false)
    private Integer defaultInterestRate;

    @Column(name = "contract_date", nullable = false)
    private LocalDate contractDate;

    @Column(name = "earlypay_flag", nullable = false)
    private boolean earlypayFlag;

    @Column(name = "earlypay_fee", nullable = false)
    private Integer earlypayFee;

    @Column(name = "acceleration_clause", nullable = false)
    private Integer accelerationClause;

    @Column(name = "add_terms", columnDefinition = "TEXT")
    private String addTerms;

    @Column(name = "add_terms_hash", length = 128)
    private String addTermsHash;

    @Column(name = "nft_image", columnDefinition = "TEXT")
    private String nftImage;

    @Column(name = "deleted_flag", nullable = false)
    private boolean deletedFlag;

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