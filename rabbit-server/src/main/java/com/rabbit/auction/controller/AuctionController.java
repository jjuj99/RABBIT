package com.rabbit.auction.controller;

import com.rabbit.auction.controller.swagger.AuctionControllerSwagger;
import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionDetailResponseDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.SimilarAuctionResponseDTO;
import com.rabbit.auction.service.AuctionService;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.request.PageRequestDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.global.response.PageResponseDTO;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;

    @AuctionControllerSwagger.InsertAuctionApi
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> addAuction(@Valid @RequestBody AuctionRequestDTO auctionRequest, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        //최소 입찰가
        if(auctionRequest.getMinimumBid()<=0){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최소 입찰가는 0보다 커야 합니다.");
        }
        //경매 종료 시간
        if (auctionRequest.getEndDate() == null || auctionRequest.getEndDate().isBefore(ZonedDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "경매 종료 시간은 현재 시간 이후여야 합니다.");
        }

        //파라미터 누락
        if(auctionRequest.getTokenId() == null || auctionRequest.getSellerSign() == null){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "필수 파라미터가 누락되었습니다.");
        }

        auctionService.addAuction(auctionRequest, Integer.parseInt(userId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success(MessageResponse.of("경매 등록 성공했습니다.")));
    }

    @AuctionControllerSwagger.SearchAuctionApi
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> searchAuctions(
            @Parameter(hidden = true) @Valid AuctionFilterRequestDTO searchRequest
    ) {
        if (searchRequest.getMinPrice() != null && searchRequest.getMaxPrice() != null &&
                searchRequest.getMinPrice() > searchRequest.getMaxPrice()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최소 금액이 최대 금액보다 클 수 없습니다.");
        }
        if (searchRequest.getMinIr() != null && searchRequest.getMaxIr() != null &&
                searchRequest.getMinIr().compareTo(searchRequest.getMaxIr()) > 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최소 수익률이 최대 수익률보다 클 수 없습니다.");
        }
        if (searchRequest.getMatStart() != null && searchRequest.getMatEnd() != null &&
                searchRequest.getMatStart().isAfter(searchRequest.getMatEnd())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작일이 종료일보다 늦을 수 없습니다.");
        }

        Pageable pageable = searchRequest.toPageable("createdAt", Sort.Direction.DESC);

        PageResponseDTO<AuctionResponseDTO> result = auctionService.searchAuctions(searchRequest, pageable);

        return ResponseEntity.ok(CustomApiResponse.success(result));
    }

    @AuctionControllerSwagger.CancelAuctionApi
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> cancelAuction(
            @PathVariable("auctionId") Integer auctionId) {

        auctionService.cancelAuction(auctionId);

        return  ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("경매가 취소되었습니다.")));
    }

    @AuctionControllerSwagger.GetMyBidAuctionsApi
    @GetMapping("/my-bids")
    public ResponseEntity<CustomApiResponse<?>> getMyBidAuctions(@Valid PageRequestDTO pageRequest) {
        Integer userId = 4;

        Pageable pageable = pageRequest.toPageable("bidDate", Sort.Direction.DESC);

        PageResponseDTO<MyAuctionResponseDTO> myBidList = auctionService.getMyBidAuctions(userId, pageable);

        return ResponseEntity.ok(CustomApiResponse.success(myBidList));
    }

    @AuctionControllerSwagger.GetAuctionDetailApi
    @GetMapping("/{auctionId}")
    public ResponseEntity<CustomApiResponse<AuctionDetailResponseDTO>> getAuctionDetail(
            @PathVariable("auctionId") Integer auctionId) {

        AuctionDetailResponseDTO auctionDetailResponse = auctionService.getAuctionDetail(auctionId);

        return ResponseEntity.ok(CustomApiResponse.success(auctionDetailResponse));
    }

    @GetMapping("/{auctionId}/similar")
    public ResponseEntity<CustomApiResponse<?>> getSimilarAuctions(@Valid @PathVariable Integer auctionId) {
        SimilarAuctionResponseDTO response = auctionService.getSimilarAuctions(auctionId);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }
}
