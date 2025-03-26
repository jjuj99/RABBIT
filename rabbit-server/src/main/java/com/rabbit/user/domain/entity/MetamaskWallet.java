package com.rabbit.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Table(name = "metamask_wallets")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class MetamaskWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer walletId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String walletAddress;

    @Column(nullable = false)
    private boolean primaryFlag;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime updatedAt;
}
