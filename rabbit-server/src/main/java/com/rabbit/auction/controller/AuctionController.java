package com.rabbit.auction.controller;

import com.rabbit.auction.controller.swagger.AuctionControllerSwagger;
import com.rabbit.auction.service.AuctionService;
import com.rabbit.auction.domain.dto.request.AuctionRequestDTO;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;

    @AuctionControllerSwagger.InsertAuctionApi
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> addAuction(@Valid @RequestBody AuctionRequestDTO auctionRequest) {
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

        auctionService.addAuction(auctionRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success(MessageResponse.of("경매 등록 성공했습니다.")));
    }
}
