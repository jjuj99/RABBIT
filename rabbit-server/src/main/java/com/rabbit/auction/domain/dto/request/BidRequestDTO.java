package com.rabbit.auction.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidRequestDTO {
    private Long bidAmount;
}
