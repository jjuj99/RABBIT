package com.rabbit.auction.domain.dto.request;

import com.rabbit.global.request.PageRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AuctionFilterRequestDTO extends PageRequestDTO {
    private Long minPrice;
    private Long maxPrice;
    private BigDecimal minIr;      // 최소 수익률
    private BigDecimal maxIr;      // 최대 수익률
    private List<Integer> repayType;      // 상환 방식 (1,2,3)
    private Integer matTerm;        // 만기 조건 (1개월, 3개월, custom 등)
    private ZonedDateTime matStart; // 만기 시작일 (custom일 경우)
    private ZonedDateTime matEnd;   // 만기 종료일 (custom일 경우)
}