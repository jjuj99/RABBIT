package com.rabbit.auction.domain.entity;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Table(
        name = "auctions",
        indexes = {
                @Index(name = "idx_auction_status", columnList = "auction_status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Setter
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer auctionId;

    @Column(nullable = false)
//    @JoinColumn(name="user_id")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Integer userId;

    @Column(nullable = false)
    private Long minimumBid;

    @Column(nullable = false)
    private ZonedDateTime endDate;

    @Column(nullable = false)
    private String tokenId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_status", nullable = false, length = 50)
    private SysCommonCodes.Auction auctionStatus;

    private Long price;

    @Column(nullable = false)
    private String sellerSign;

    private Integer winningBidder;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    private Long remainPrincipal;

    private Integer remainRepaymentDate;

    private BigDecimal returnRate;

    public void updatePriceAndBidder(Long price, Integer bidderId) {
        this.price = price;
        this.winningBidder = bidderId;
    }
}
