package com.rabbit.auction.controller;

import com.rabbit.auction.controller.swagger.BidControllerSwagger;
import com.rabbit.auction.domain.dto.request.BidRequestDTO;
import com.rabbit.auction.domain.dto.response.BidResponseDTO;
import com.rabbit.auction.service.BidService;
import com.rabbit.global.response.CustomApiResponse;
import com.rabbit.global.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auctions/{auctionId}/bids")
@RequiredArgsConstructor
@Slf4j
public class BidController {
    private final BidService bidService;

    @BidControllerSwagger.AddBidApi
    @PostMapping
    public ResponseEntity<CustomApiResponse<?>> addBid(@Valid @RequestBody BidRequestDTO bidRequest,
                                                       @PathVariable("auctionId") Integer auctionId,
                                                       Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        bidService.addBid(bidRequest, auctionId, Integer.parseInt(userId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomApiResponse.success(MessageResponse.of("입찰 성공했습니다.")));
    }

    @BidControllerSwagger.GetBidListApi
    @GetMapping
    public ResponseEntity<CustomApiResponse<?>> getBids(@PathVariable("auctionId") Integer auctionId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        List<BidResponseDTO> result = bidService.getBids(auctionId);

        return ResponseEntity.ok(CustomApiResponse.success(result));
    }
}
