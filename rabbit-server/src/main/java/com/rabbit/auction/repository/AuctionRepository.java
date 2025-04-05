package com.rabbit.auction.repository;

import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import jakarta.persistence.LockModeType;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Integer>, AuctionRepositoryCustom {
    // 특정 토큰 ID로 ING 상태의 경매가 이미 존재하는지 체크
    Optional<Auction> findByTokenIdAndAuctionStatus(BigInteger tokenId, SysCommonCodes.Auction auctionStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.auctionId = :auctionId")
    Optional<Auction> findByIdForUpdate(@Param("auctionId") Integer auctionId);

    boolean existsByTokenIdAndAuctionStatus(BigInteger tokenId, SysCommonCodes.Auction auction);
}
