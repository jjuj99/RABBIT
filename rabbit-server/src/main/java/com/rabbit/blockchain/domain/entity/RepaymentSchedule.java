package com.rabbit.blockchain.domain.entity;

import java.time.Instant;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상환 일정 엔티티
 */
@Entity
@Table(name = "repayment_schedule")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentSchedule {

    @Id
    @Column(name = "token_id")
    private Long tokenId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id",
            foreignKey = @ForeignKey(name = "repayment_schedule_token_id_fk"),
            insertable = false, updatable = false)
    @MapsId
    private PromissoryNoteEntity promissoryNote;

    @Column(name = "initial_principal", nullable = false)
    private Long initialPrincipal;

    @Column(name = "remaining_principal", nullable = false)
    private Long remainingPrincipal;

    @Column(name = "interest_rate", nullable = false)
    private Integer interestRate;

    @Column(name = "default_interest_rate", nullable = false)
    private Integer defaultInterestRate;

    @Column(name = "monthly_payment_date", nullable = false)
    private Integer monthlyPaymentDate;

    @Column(name = "next_monthly_payment_date", nullable = false)
    private Instant nextMonthlyPaymentDate;

    @Column(name = "total_payments", nullable = false)
    private Integer totalPayments;

    @Column(name = "remaining_payments", nullable = false)
    private Integer remainingPayments;

    @Column(name = "fixed_payment_amount")
    private Long fixedPaymentAmount;

    @Column(name = "repayment_type", nullable = false, length = 50)
    private String repaymentType;

    @Column(name = "debtor_wallet_address", nullable = false, length = 42)
    private String debtorWalletAddress;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag;

    @Column(name = "overdue_flag", nullable = false)
    private Boolean overdueFlag;

    @Column(name = "overdue_start_date")
    private Instant overdueStartDate;

    @Column(name = "overdue_days", nullable = false)
    private Integer overdueDays;

    @Column(name = "accumulated_overdue_interest", nullable = false)
    private Long accumulatedOverdueInterest;

    @Column(name = "default_count", nullable = false)
    private Integer defaultCount;

    @Column(name = "acceleration_clause", nullable = false)
    private Integer accelerationClause;

    @Column(name = "current_interest_rate", nullable = false)
    private Integer currentInterestRate;

    @Column(name = "total_default_count", nullable = false)
    private Integer totalDefaultCount;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * 상환 처리 및 남은 원금/납부횟수 업데이트
     *
     * @param amount 상환 금액
     * @param principalAmount 원금 상환액
     * @param newRemainingPrincipal 새로운 남은 원금
     * @param nextDate 다음 납부일
     * @return 갱신된 상환 일정
     */
    public RepaymentSchedule updateAfterPayment(Long amount, Long principalAmount,
                                                Long newRemainingPrincipal, Instant nextDate) {
        this.remainingPrincipal = newRemainingPrincipal;
        this.remainingPayments = this.remainingPayments - 1;
        this.nextMonthlyPaymentDate = nextDate;

        // 연체 상태 해제
        if (this.overdueFlag) {
            this.overdueFlag = false;
            this.overdueStartDate = null;
            this.overdueDays = 0;
            this.accumulatedOverdueInterest = 0L;
            this.currentInterestRate = this.interestRate;
        }

        return this;
    }

    /**
     * 연체 상태 업데이트
     *
     * @param overdueFlag 연체 여부
     * @param overdueStartDate 연체 시작일
     * @param overdueDays 연체 일수
     * @param accumulatedOverdueInterest 누적 연체 이자
     * @param defaultCount 연체 횟수
     * @param currentInterestRate 현재 적용 이자율
     * @param totalDefaultCount 총 연체 횟수
     * @return 갱신된 상환 일정
     */
    public RepaymentSchedule updateOverdueStatus(Boolean overdueFlag, Instant overdueStartDate,
                                                 Integer overdueDays, Long accumulatedOverdueInterest,
                                                 Integer defaultCount, Integer currentInterestRate,
                                                 Integer totalDefaultCount) {
        this.overdueFlag = overdueFlag;
        this.overdueStartDate = overdueStartDate;
        this.overdueDays = overdueDays;
        this.accumulatedOverdueInterest = accumulatedOverdueInterest;
        this.defaultCount = defaultCount;
        this.currentInterestRate = currentInterestRate;
        this.totalDefaultCount = totalDefaultCount;

        return this;
    }

    /**
     * 상환 일정 활성화 설정
     *
     * @param active 활성화 여부
     * @return 갱신된 상환 일정
     */
    public RepaymentSchedule setActive(Boolean active) {
        this.activeFlag = active;
        return this;
    }

    /**
     * 기한이익상실 조건 확인
     *
     * @return 기한이익상실 여부
     */
    public boolean checkAcceleration() {
        return this.defaultCount >= this.accelerationClause;
    }
}