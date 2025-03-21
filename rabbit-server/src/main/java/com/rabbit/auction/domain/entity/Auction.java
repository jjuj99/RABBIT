package com.rabbit.auction.domain.entity;

import com.rabbit.auction.domain.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Table(name="auctions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Setter
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    @Column(nullable = false)
    private AuctionStatus auctionStatus;

    private Long price;

    @Column(nullable = false)
    private String sellerSign;

    private Integer winningBidder;

    @Column(nullable = false)
    private ZonedDateTime createdAt;
}
