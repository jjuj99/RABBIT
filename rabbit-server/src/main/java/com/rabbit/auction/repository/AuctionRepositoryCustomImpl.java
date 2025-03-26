package com.rabbit.auction.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.entity.QAuction;
import com.rabbit.auction.domain.enums.AuctionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static com.querydsl.core.types.Projections.fields;

import java.util.List;

@RequiredArgsConstructor
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AuctionResponseDTO> searchAuctions(AuctionFilterRequestDTO req, Pageable pageable) {
        QAuction auction = QAuction.auction;

        BooleanBuilder builder = new BooleanBuilder();
        if (req.getMinPrice() != null) builder.and(auction.price.goe(req.getMinPrice()));
        if (req.getMaxPrice() != null) builder.and(auction.price.loe(req.getMaxPrice()));

        //진행중인 경매만 가져오기
        builder.and(auction.auctionStatus.eq(AuctionStatus.ING));
        
        List<AuctionResponseDTO> content = queryFactory
                .select(fields(
                        AuctionResponseDTO.class,
                        auction.auctionId.as("auctionId"),
                        auction.price,
                        auction.endDate,
                        auction.createdAt
                ))
                .from(auction)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .select(auction.count())
                .from(auction)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }
}

