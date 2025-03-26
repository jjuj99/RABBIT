package com.rabbit.auction.service;

import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.enums.AuctionStatus;
import com.rabbit.auction.repository.BidRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

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

    public PageResponseDTO<AuctionResponseDTO> searchAuctions(AuctionFilterRequestDTO request, Pageable pageable) {
        Page<AuctionResponseDTO> result = auctionRepository.searchAuctions(request, pageable);

        //블록체인 읽어와 다른 조건 필터링 구현 필요

        return PageResponseDTO.<AuctionResponseDTO>builder()
                .content(result.getContent())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }

    public void cancelAuction(@Valid Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        //입찰자 존재시 취소 불가
        boolean hasBids=bidRepository.existsByAuction(auction);
        if(hasBids){
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰자가 존재해 경매를 취소할 수 없습니다.");
        }

        //cancel로 상태 변경
        auction.setAuctionStatus(AuctionStatus.CANCELED);
    }

    public PageResponseDTO<MyAuctionResponseDTO> getMyBidAuctions(Integer userId, Pageable pageable) {
        Page<MyAuctionResponseDTO> result = auctionRepository.getMyBidAuction(userId, pageable);

        return PageResponseDTO.<MyAuctionResponseDTO>builder()
                .content(result.getContent())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }
}
