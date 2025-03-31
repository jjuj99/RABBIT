package com.rabbit.auction.service;

import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionDetailResponseDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.repository.BidRepository;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeService;
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
    private final SysCommonCodeService sysCommonCodeService;
    // 코드 타입 상수 정의
    private static final String AUCTION_STATUS = SysCommonCodes.Auction.values()[0].getCodeType();
    private static final String BID_STATUS = SysCommonCodes.Bid.values()[0].getCodeType();

    public void addAuction(@Valid AuctionRequestDTO auctionRequest) {
        //NFT의 소유자가 맞는지 확인
        //이미 경매가 진행중인지 확인
        auctionRepository.findByTokenIdAndAuctionStatus(auctionRequest.getTokenId(), SysCommonCodes.Auction.ING)
                .ifPresent(auction ->{
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "해당 NFT는 이미 경매가 진행 중입니다.");
                });

        Auction auction= Auction.builder()
                .userId(1)  //아직 임의로 설정해둠
                .minimumBid(auctionRequest.getMinimumBid())
                .endDate(auctionRequest.getEndDate())
                .tokenId(auctionRequest.getTokenId())
                .auctionStatus(SysCommonCodes.Auction.ING)
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
        auction.setAuctionStatus(SysCommonCodes.Auction.CANCELED);
    }

    public PageResponseDTO<MyAuctionResponseDTO> getMyBidAuctions(Integer userId, Pageable pageable) {
        Page<MyAuctionResponseDTO> result = auctionRepository.getMyBidAuction(userId, pageable);

        // 국제화된 상태명 설정
        // page 처리 된 곳이기 때문에 매번 국제화를 불러와도 성능 차이 미미함 (10 - 50개 정도이기 때문)
        // but 엑셀 저장과 같이 한번에 수만건 처리해야하는 batch 작업이라면 국제화한 code name 을 미리 계산해서 사용하는 것이 성능에 좋음
        result.getContent().forEach(dto -> {
            if (dto.getAuctionStatus() != null) {
                dto.setAuctionStatusName(sysCommonCodeService.getCodeName(
                        AUCTION_STATUS, dto.getAuctionStatus().getCode()));
            }
            if (dto.getBidStatus() != null) {
                dto.setBidStatusName(sysCommonCodeService.getCodeName(
                        BID_STATUS, dto.getBidStatus()));
            }
        });

        return PageResponseDTO.<MyAuctionResponseDTO>builder()
                .content(result.getContent())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }

    public AuctionDetailResponseDTO getAuctionDetail(Integer auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 경매를 찾을 수 없습니다."));

        //블록체인에서 직접 읽어온 값 추가 필요

        return AuctionDetailResponseDTO.builder()
                .auctionId(auction.getAuctionId())
                .price(auction.getPrice())
                .endDate(auction.getEndDate())
                .createdAt(auction.getCreatedAt())
                .build();
    }
}
