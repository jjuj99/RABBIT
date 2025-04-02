package com.rabbit.auction.repository;

import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.auction.domain.entity.Auction;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface AuctionRepositoryCustom {
    Page<AuctionResponseDTO> searchAuctions(AuctionFilterRequestDTO request, Pageable pageable);

    Page<MyAuctionResponseDTO> getMyBidAuction(Integer userId, Pageable pageable);

    List<Auction> findSimilarAuctionsByPrincipalAndDays(@Valid Integer auctionId, Long basePrincipal, Integer baseDays);
}
