package com.rabbit.auction.controller;

import com.rabbit.auction.controller.swagger.AuctionControllerSwagger;
import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.*;
import com.rabbit.auction.service.AuctionService;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.request.PageRequestDTO;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.loan.domain.dto.response.ContractEventDTO;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;

    @AuctionControllerSwagger.InsertAuctionApi
    @PostMapping("/add")
    public ResponseEntity<CustomApiResponse<AuctionIdDTO>> addAuction(@Valid @RequestBody AuctionRequestDTO auctionRequest, Authentication authentication) {
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
        if(auctionRequest.getTokenId() == null){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "필수 파라미터가 누락되었습니다.");
        }

        AuctionIdDTO auctionIdDTO = auctionService.addAuction(auctionRequest, Integer.parseInt(userId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success(auctionIdDTO));
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

    @GetMapping("/my-auctions")
    public ResponseEntity<CustomApiResponse<?>> myAuctions(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        List<AuctionMyListResponseDTO> result = auctionService.myAuctionList(Integer.parseInt(userId));

        return ResponseEntity.ok(CustomApiResponse.success(result));
    }

    @AuctionControllerSwagger.CancelAuctionApi
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> cancelAuction(
            @PathVariable("auctionId") Integer auctionId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        auctionService.cancelAuction(auctionId, Integer.valueOf(userId));

        return  ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("경매가 취소되었습니다.")));
    }

    @DeleteMapping("/internal/{auctionId}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> deleteAuction(@PathVariable("auctionId") Integer auctionId) {

        auctionService.deleteAuction(auctionId);

        return  ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("경매가 삭제되었습니다.")));
    }

    @AuctionControllerSwagger.GetMyBidAuctionsApi
    @GetMapping("/my-bids")
    public ResponseEntity<CustomApiResponse<?>> getMyBidAuctions(@Valid PageRequestDTO pageRequest, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        Pageable pageable = pageRequest.toPageable("bidDate", Sort.Direction.DESC);

        PageResponseDTO<MyAuctionResponseDTO> myBidList = auctionService.getMyBidAuctions(Integer.parseInt(userId), pageable);

        return ResponseEntity.ok(CustomApiResponse.success(myBidList));
    }

    @AuctionControllerSwagger.GetAuctionDetailApi
    @GetMapping("/{auctionId}/info")
    public ResponseEntity<CustomApiResponse<AuctionDetailResponseDTO>> getAuctionDetail(
            @PathVariable("auctionId") Integer auctionId, Authentication authentication) {
        Integer userId;
        if (authentication == null) {
            userId = null;
        } else {
            String id = (String) authentication.getPrincipal();
            userId = Integer.valueOf(id);
        }

        AuctionDetailResponseDTO auctionDetailResponse = auctionService.getAuctionDetail(auctionId, userId);

        return ResponseEntity.ok(CustomApiResponse.success(auctionDetailResponse));
    }

    @AuctionControllerSwagger.GetSimilarAuctionsApi
    @GetMapping("/{auctionId}/similar")
    public ResponseEntity<CustomApiResponse<?>> getSimilarAuctions(@Valid @PathVariable Integer auctionId) {

        SimilarAuctionResponseDTO response = auctionService.getSimilarAuctions(auctionId);

        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @AuctionControllerSwagger.GetAuctionEventApi
    @GetMapping("/{auctionId}/event")
    public ResponseEntity<CustomApiResponse<?>> getAuctionEvents(@Valid @PathVariable Integer auctionId) {

        List<ContractEventDTO> events = auctionService.getAuctionEvents(auctionId);

        return ResponseEntity.ok(CustomApiResponse.success(events));
    }

    @PostMapping("/{auctionId}/force-end")
    public ResponseEntity<CustomApiResponse<?>> forceEndAuction(@PathVariable Integer auctionId) {
        auctionService.processAuctionEnd(auctionId);
        return ResponseEntity.ok(CustomApiResponse.success("경매 종료 처리 완료"));
    }
}
