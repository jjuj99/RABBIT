package com.rabbit.auction.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SimilarAuctionResponseDTO {
    private TargetAuctionResponseDTO targetAuction;
    private List<ComparisonAuctionResponseDTO> comparisonAuctions;
}
