package com.rabbit.auction.service;

import com.rabbit.auction.domain.dto.request.BidRequestDTO;
import com.rabbit.auction.domain.dto.response.BidResponseDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.entity.Bid;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.repository.BidRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    @Transactional
    public void addBid(@Valid BidRequestDTO bidRequest, Integer auctionId, Integer userId) {
        // 경매 존재하는지 확인
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

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

    public List<BidResponseDTO> getBids(Integer auctionId) {
        //경매가 존재하는지 확인
        if (!auctionRepository.existsById(auctionId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다.");
        }

        List<Bid> bids = bidRepository.findAllByAuction_AuctionIdOrderByCreatedAtDesc(auctionId);

        List<BidResponseDTO> bidResponses = bids.stream()
                .map(bid -> BidResponseDTO.builder()
                        .bidId(bid.getBidId())
                        .bidAmount(bid.getBidAmount())
                        .createdAt(bid.getCreatedAt())
                        .build())
                .toList();
        return bidResponses;
    }
}
