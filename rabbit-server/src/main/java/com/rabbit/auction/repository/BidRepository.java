package com.rabbit.auction.repository;

import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByAuction(Auction auction);

    List<Bid> findAllByAuction_AuctionIdOrderByCreatedAtDesc(Integer auctionId);

    List<Bid> findAllByAuction_AuctionIdOrderByBidAmountDescCreatedAtAsc(Integer auctionId);
}
