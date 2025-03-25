package com.rabbit.auction.domain.dto.request;

import com.rabbit.global.request.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Builder
@Schema(description = "경매 목록 조회를 위한 필터링 검색 요청")
public class AuctionFilterRequestDTO extends PageRequestDTO {
    private Long minPrice;
    private Long maxPrice;
    private BigDecimal minRr;      // 최소 수익률
    private BigDecimal maxRr;      // 최대 수익률
    private String repayType;      // 상환 방식 (ex. 원리금 균등 등)
    private String matTerm;        // 만기 조건 (1개월, 3개월, custom 등)
    private ZonedDateTime matStart; // 만기 시작일 (custom일 경우)
    private ZonedDateTime matEnd;   // 만기 종료일 (custom일 경우)
}