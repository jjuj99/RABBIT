package com.rabbit.auth.repository;

import com.rabbit.auth.domain.entity.SsafyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SsafyAccountRepository extends JpaRepository<SsafyAccount, Integer> {

    Optional<SsafyAccount> findByEmail(String email);
}
