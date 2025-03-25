package com.rabbit.auction.repository;

import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByAuction(Auction auction);
}
