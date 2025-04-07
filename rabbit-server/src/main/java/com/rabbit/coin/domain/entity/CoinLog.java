package com.rabbit.coin.domain.entity;

import com.rabbit.coin.domain.enums.CoinLogStatus;
import com.rabbit.coin.domain.enums.CoinLogType;
import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "coin_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CoinLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer coinLogId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinLogType type; // 입금, 출금

    @Column(nullable = false)
    private String account; // 메타마스크 계좌

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = true)
    private String orderId;

    @Column(nullable = true)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CoinLogStatus status; // 성공, 실패
}