package com.rabbit.auction.service;

import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.enums.AuctionStatus;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;

    public void addAuction(@Valid AuctionRequestDTO auctionRequest) {
        //NFT의 소유자가 맞는지 확인
        //이미 경매가 진행중인지 확인
        auctionRepository.findByTokenIdAndAuctionStatus(auctionRequest.getTokenId(), AuctionStatus.ING)
                .ifPresent(auction ->{
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "해당 NFT는 이미 경매가 진행 중입니다.");
                });

        Auction auction= Auction.builder()
                .userId(1)  //아직 임의로 설정해둠
                .minimumBid(auctionRequest.getMinimumBid())
                .endDate(auctionRequest.getEndDate())
                .tokenId(auctionRequest.getTokenId())
                .auctionStatus(AuctionStatus.ING)
                .sellerSign(auctionRequest.getSellerSign())
                .createdAt(ZonedDateTime.now())
                .build();

        auctionRepository.save(auction);
    }
}
