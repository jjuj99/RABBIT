package com.rabbit.contract.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.user.domain.entity.User;

public interface ContractRepository extends JpaRepository<Contract, Long>, ContractRepositoryCustom {

    // 특정 ID의 계약 조회 (삭제되지 않은 계약만)
    Optional<Contract> findByContractIdAndDeletedFlagFalse(Integer contractId);

    // 사용자가 채권자인 계약 목록 조회 (User 엔티티 대신 userId 사용)
    @Query("SELECT c FROM Contract c WHERE c.creditor.userId = :userId AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByCreditorId(@Param("userId") Integer userId);

    // 사용자가 채권자인 계약 페이지네이션 조회 (User 엔티티 대신 userId 사용)
    @Query("SELECT c FROM Contract c WHERE c.creditor.userId = :userId AND c.deletedFlag = false")
    Page<Contract> findByCreditorIdAndDeletedFlagFalse(@Param("userId") Integer userId, Pageable pageable);

    // 사용자가 채무자인 계약 목록 조회 (User 엔티티 대신 userId 사용)
    @Query("SELECT c FROM Contract c WHERE c.debtor.userId = :userId AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByDebtorId(@Param("userId") Integer userId);

    // 사용자가 채무자인 계약 페이지네이션 조회 (User 엔티티 대신 userId 사용)
    @Query("SELECT c FROM Contract c WHERE c.debtor.userId = :userId AND c.deletedFlag = false")
    Page<Contract> findByDebtorIdAndDeletedFlagFalse(@Param("userId") Integer userId, Pageable pageable);

    // 특정 상태의 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.contractStatus = :status AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByContractStatus(@Param("status") SysCommonCodes.Contract status);

    // 채권자가 특정 사용자이고 특정 상태인 계약 조회 (User 엔티티 대신 userId 사용)
    @Query("SELECT c FROM Contract c WHERE c.creditor.userId = :userId AND c.contractStatus = :status AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByCreditorIdAndContractStatus(@Param("userId") Integer userId, @Param("status") SysCommonCodes.Contract status);

    // 채무자가 특정 사용자이고 특정 상태인 계약 조회 (User 엔티티 대신 userId 사용)
    @Query("SELECT c FROM Contract c WHERE c.debtor.userId = :userId AND c.contractStatus = :status AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByDebtorIdAndContractStatus(@Param("userId") Integer userId, @Param("status") SysCommonCodes.Contract status);

    // 기존 메서드 호환성 유지
    // 사용자가 채권자인 계약 페이지네이션 조회
    Page<Contract> findByCreditorAndDeletedFlagFalse(User creditor, Pageable pageable);

    // 사용자가 채무자인 계약 페이지네이션 조회
    Page<Contract> findByDebtorAndDeletedFlagFalse(User debtor, Pageable pageable);

    // 사용자가 채무자인 계약 목록 조회
    @Query("SELECT c FROM Contract c WHERE c.creditor = :user AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByCreditor(@Param("user") User user);

    // 사용자가 채권자인 계약 목록 조회
    @Query("SELECT c FROM Contract c WHERE c.debtor = :user AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByDebtor(@Param("user") User user);

    // 채권자가 특정 사용자이고 특정 상태인 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.creditor = :user AND c.contractStatus = :status AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByCreditorAndContractStatus(@Param("user") User user, @Param("status") SysCommonCodes.Contract status);

    // 채무자가 특정 사용자이고 특정 상태인 계약 조회
    @Query("SELECT c FROM Contract c WHERE c.debtor = :user AND c.contractStatus = :status AND c.deletedFlag = false ORDER BY c.createdAt DESC")
    List<Contract> findByDebtorAndContractStatus(@Param("user") User user, @Param("status") SysCommonCodes.Contract status);

    // nft tokenId로 Contract 조회
    Optional<Contract> findByTokenId(BigInteger tokenId);

    // 휴대폰 번호로 채무자 찾기
//    @Query("SELECT c FROM Contract c WHERE c.debtor.phone = :phone AND c.deletedFlag = false ORDER BY c.createdAt DESC")
//    List<Contract> findByDebtorPhone(@Param("phone") String phone);
}