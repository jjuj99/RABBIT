package com.rabbit.user.repository;

import com.rabbit.user.domain.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Integer> {
}
