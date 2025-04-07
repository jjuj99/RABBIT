package com.rabbit.user.repository;

import com.rabbit.user.domain.entity.RefundAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundAccountRepository extends JpaRepository<RefundAccount, Integer> {

    Optional<RefundAccount> findByUserIdAndPrimaryFlagTrue(Integer userId);

    Optional<RefundAccount> findByUserIdAndAccountNumber(Integer userId, String accountNumber);
}
