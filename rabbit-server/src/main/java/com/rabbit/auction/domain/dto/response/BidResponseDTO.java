package com.rabbit.auction.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponseDTO {
    private Integer bidId;
    private Long bidAmount;
    private ZonedDateTime createdAt;
}
