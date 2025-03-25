package com.rabbit.auction.service;

import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.request.BidRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.entity.Bid;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.enums.AuctionStatus;
import com.rabbit.auction.repository.BidRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.PageResponseDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.Optional;

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
                .pageNo(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }

    @Transactional
    public void addBid(@Valid BidRequestDTO bidRequest, Integer auctionId, Integer userId) {
        // 경매 존재하는지 확인
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "경매를 찾을 수 없습니다."));

        // 경매가 마감되지 않았는지 확인
        if(auction.getEndDate().isBefore(ZonedDateTime.now())){
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "이미 마감된 경매입니다.");
        }

        // 입찰 금액이 현재 금액보다 큰지 확인
        if(auction.getPrice()>=bidRequest.getBidAmount()){
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰 금액이 현재 경매가보다 낮습니다.");
        }

        //해당 입찰자에게 금액만큼 잔고가 있는지 확인 -> 서명 후 스마트컨트랙트로 예치
        //이전 입찰자에게는 예치한 금액 돌려주기

        Bid bid = Bid.builder()
                .userId(userId)
                .bidAmount(bidRequest.getBidAmount())
                .auction(auction)
                .bidderSign("") //메타마스크 서명 얻어오기
                .createdAt(ZonedDateTime.now())
                .build();

        // 입찰목록에 추가
        bidRepository.save(bid);

        // auction에 현재가, 입찰자 업데이트
        auction.updatePriceAndBidder(bidRequest.getBidAmount(), userId);
    }


}
