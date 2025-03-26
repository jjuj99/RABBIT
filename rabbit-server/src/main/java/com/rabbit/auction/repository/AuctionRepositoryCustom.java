package com.rabbit.auction.repository;

import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface AuctionRepositoryCustom {
    Page<AuctionResponseDTO> searchAuctions(AuctionFilterRequestDTO request, Pageable pageable);
}
