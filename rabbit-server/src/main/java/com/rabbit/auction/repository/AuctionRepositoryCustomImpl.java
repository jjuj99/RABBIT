package com.rabbit.auction.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.auction.domain.entity.QAuction;
import com.rabbit.auction.domain.entity.QBid;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
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
        builder.and(auction.auctionStatus.eq(SysCommonCodes.Auction.ING));
        
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

    public Page<MyAuctionResponseDTO> getMyBidAuction(Integer userId, Pageable pageable){
        QAuction auction = QAuction.auction;
        QBid bid=QBid.bid;
        QBid subBid = new QBid("subBid");

        //경매당 가장 최근 입찰 1건 추려내기
        JPQLQuery<Tuple> latestBids = JPAExpressions
                .select(subBid.auction.auctionId, subBid.createdAt.max())
                .from(subBid)
                .where(subBid.userId.eq(userId))
                .groupBy(subBid.auction.auctionId);

        //필터링한 bid와 auction join
        List<MyAuctionResponseDTO> content = queryFactory
                .select(Projections.constructor(MyAuctionResponseDTO.class,
                        auction.auctionId,
                        bid.createdAt,
                        auction.auctionStatus,
                        auction.price,
                        bid.bidAmount,
                        ExpressionUtils.as(
                                new CaseBuilder()
                                        .when(auction.auctionStatus.eq(SysCommonCodes.Auction.ING)).then("PENDING")
                                        .when(auction.winningBidder.eq(userId)).then("WON")
                                        .otherwise("LOST"),
                                "bidStatus"
                        ),
                        JPAExpressions
                                .select(subBid.count())
                                .from(subBid)
                                .where(subBid.auction.auctionId.eq(auction.auctionId))
                ))
                .from(bid)
                .join(bid.auction, auction)
                .where(
                        Expressions.list(bid.auction.auctionId, bid.createdAt)
                                .in(latestBids)
                )
                .orderBy(bid.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(bid.auction.auctionId.countDistinct())
                .from(bid)
                .where(bid.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}

