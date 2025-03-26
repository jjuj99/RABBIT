package com.rabbit.user.repository;

import com.rabbit.user.domain.entity.RefundAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundAccountRepository extends JpaRepository<RefundAccount, Integer> {
}
