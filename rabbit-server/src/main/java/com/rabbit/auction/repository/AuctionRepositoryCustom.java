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

    /**
     * 새로운 메서드: JOIN을 사용하여 모든 필요한 데이터를 한번에 가져옴
     * 이 메서드는 보다 상세한 구현이 필요하며,
     * AuctionRepositoryCustomImpl 클래스에서 구현해야 함
     */
    Page<AuctionResponseDTO> searchAuctionsWithLeftJoin(AuctionFilterRequestDTO request, Pageable pageable);

    Page<MyAuctionResponseDTO> getMyBidAuction(Integer userId, Pageable pageable);

    List<Auction> findSimilarAuctionsByPrincipalAndDays(@Valid Integer auctionId, Long basePrincipal, Integer baseDays);
}
