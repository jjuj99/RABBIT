package com.rabbit.coin.repository;

import com.rabbit.coin.domain.entity.CoinLog;
import com.rabbit.coin.domain.enums.CoinLogStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoinLogRepository extends JpaRepository<CoinLog, Integer> {
    Optional<CoinLog> findByOrderId(String orderId);

    List<CoinLog> findByUserIdAndStatus(Integer userId, CoinLogStatus status);
}
