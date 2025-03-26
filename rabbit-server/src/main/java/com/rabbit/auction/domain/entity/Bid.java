package com.rabbit.auction.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Table(name="bids")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Setter
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="auction_id", nullable=false)
    private Auction auction;

    @Column(nullable=false)
    private Integer userId;

    @Column(nullable=false)
    private Long bidAmount;

    @Column(nullable=false)
    private String bidderSign;

    @Column(nullable=false)
    private ZonedDateTime createdAt;
}
