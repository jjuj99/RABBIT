package com.rabbit.coin.repository;

import com.rabbit.coin.domain.entity.CoinLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinLogRepository extends JpaRepository<CoinLog, Integer> {
    Optional<CoinLog> findByOrderId(String orderId);
}
