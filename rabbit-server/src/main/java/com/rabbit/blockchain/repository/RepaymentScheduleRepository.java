package com.rabbit.blockchain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rabbit.blockchain.domain.entity.RepaymentSchedule;

/**
 * 상환 일정 저장소
 */
@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {

    /**
     * 활성화된 상환 일정 조회
     *
     * @param tokenId 토큰 ID
     * @return 상환 일정
     */
    Optional<RepaymentSchedule> findByTokenIdAndActiveFlagTrue(Long tokenId);

    /**
     * 활성화된 연체 상태인 상환 일정 목록 조회
     *
     * @return 연체 상환 일정 목록
     */
    List<RepaymentSchedule> findByActiveFlagTrueAndOverdueFlagTrue();

    /**
     * 활성화되고 다음 납부일이 현재보다 이전인 상환 일정 목록 조회
     *
     * @param now 현재 시간
     * @return 납부일이 지난 상환 일정 목록
     */
    List<RepaymentSchedule> findByActiveFlagTrueAndNextMonthlyPaymentDateBefore(Instant now);

    /**
     * 특정 채무자의 활성화된 상환 일정 목록 조회
     *
     * @param walletAddress 채무자 지갑 주소
     * @return 상환 일정 목록
     */
    List<RepaymentSchedule> findByDebtorWalletAddressAndActiveFlagTrue(String walletAddress);

    /**
     * 활성화된 모든 상환 일정의 토큰 ID 목록 조회
     *
     * @return 토큰 ID 목록
     */
    @Query("SELECT r.tokenId FROM RepaymentSchedule r WHERE r.activeFlag = true")
    List<Long> findAllActiveTokenIds();

    /**
     * 다음 납부일 기준 가장 임박한 활성 상환 일정 목록 조회
     *
     * @param limit 조회할 개수
     * @return 상환 일정 목록
     */
    @Query(value = "SELECT * FROM repayment_schedule " +
            "WHERE active_flag = true " +
            "ORDER BY next_monthly_payment_date ASC " +
            "LIMIT :limit", nativeQuery = true)
    List<RepaymentSchedule> findTopUpcomingPayments(@Param("limit") int limit);

    /**
     * 기한이익상실 상태가 될 수 있는 상환 일정 목록 조회 (연체 횟수 >= 기한이익상실 조건)
     *
     * @return 상환 일정 목록
     */
    @Query("SELECT r FROM RepaymentSchedule r " +
            "WHERE r.activeFlag = true AND r.overdueFlag = true " +
            "AND r.defaultCount >= r.accelerationClause")
    List<RepaymentSchedule> findEligibleForAcceleration();
}