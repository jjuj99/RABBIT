package com.rabbit.auction.service;

import com.rabbit.auction.domain.dto.request.BidRequestDTO;
import com.rabbit.auction.domain.dto.response.BidResponseDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.entity.Bid;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.repository.BidRepository;
import com.rabbit.blockchain.service.PromissoryNoteAuctionService;
import com.rabbit.blockchain.service.RabbitCoinService;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.notification.domain.dto.request.NotificationRequestDTO;
import com.rabbit.notification.service.NotificationService;
import com.rabbit.sse.domain.dto.response.NotiResponseDTO;
import com.rabbit.sse.service.SseEventPublisher;
import com.rabbit.user.domain.entity.MetamaskWallet;
import com.rabbit.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final SseEventPublisher sseEventPublisher;
    private final NotificationService notificationService;
    private final UserService userService;
    private final PromissoryNoteAuctionService promissoryNoteAuctionService;
    private final RabbitCoinService rabbitCoinService;

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
        if(auction.getPrice()!=null && auction.getPrice()>=bidRequest.getBidAmount()){
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰 금액이 현재 경매가보다 낮습니다.");
        }

        // 양도자가 아닌지 확인
        if(auction.getAssignor().getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "양도자는 경매에 참여할 수 없습니다.");
        }

        //해당 입찰자에게 금액만큼 잔고가 있는지 확인
        MetamaskWallet currentBidderWallet = userService.getWalletByUserIdAndPrimaryFlagTrue(userId);

        Integer previousBidderId = auction.getWinningBidder();

        BigInteger currentBalance = rabbitCoinService.balanceOf(currentBidderWallet.getWalletAddress());
        BigInteger previousAmount = promissoryNoteAuctionService.getBiddingAmount(auction.getTokenId());
        BigInteger bidAmount = BigInteger.valueOf(bidRequest.getBidAmount());

        log.info("가진 금액 {}", currentBalance);

        // 현재 입찰자가 가격을 올려서 입찰
        if(previousBidderId!=null && previousBidderId.equals(userId)){
            if(currentBalance.add(previousAmount).compareTo(bidAmount) < 0){
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰 금액이 부족합니다.");
            }
        }else{  //다른 사람이 입찰
            if(currentBalance.compareTo(bidAmount) < 0){
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "입찰 금액이 부족합니다.");
            }
        }

        // 스마트 컨트랙트로 코인 예치 및 이전 사람에게는 돌려주기
        promissoryNoteAuctionService.depositRAB(
                auction.getTokenId(),                          // tokenId
                BigInteger.valueOf(bidRequest.getBidAmount()),                // amount
                currentBidderWallet.getWalletAddress()                                           // bidder address
        );

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

        //SSE 전송
        BidResponseDTO response = BidResponseDTO.builder()
                .bidId(bid.getBidId())
                .bidAmount(bid.getBidAmount())
                .createdAt(bid.getCreatedAt())
                .build();

        sseEventPublisher.publish(
                "bid-updated",                  // 타입
                "auction-" + auctionId,         // 키
                response                        // 데이터
        );

        if (previousBidderId != null && !previousBidderId.equals(userId)) {
            notificationService.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(previousBidderId)
                            .type(SysCommonCodes.NotificationType.BID_FAILED)
                            .relatedId(auctionId)
                            .relatedType(SysCommonCodes.NotificationRelatedType.AUCTION)
                            .build()
            );
        }
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
