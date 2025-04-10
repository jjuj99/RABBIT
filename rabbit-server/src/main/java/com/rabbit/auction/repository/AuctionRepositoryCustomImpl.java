package com.rabbit.auction.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rabbit.auction.domain.dto.request.AuctionFilterRequestDTO;
import com.rabbit.auction.domain.dto.response.AuctionResponseDTO;
import com.rabbit.auction.domain.dto.response.MyAuctionResponseDTO;
import com.rabbit.auction.domain.entity.Auction;
import com.rabbit.auction.domain.entity.QAuction;
import com.rabbit.auction.domain.entity.QBid;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.promissorynote.domain.entity.QPromissoryNoteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static com.querydsl.core.types.Projections.fields;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
                        auction.price.coalesce(auction.minimumBid).as("price"),
                        auction.endDate,
                        auction.createdAt,
                        auction.tokenId
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

    @Override
    public Page<AuctionResponseDTO> searchAuctionsWithLeftJoin(AuctionFilterRequestDTO request, Pageable pageable) {
        QAuction auction = QAuction.auction;
        QPromissoryNoteEntity promissoryNote = QPromissoryNoteEntity.promissoryNoteEntity;

        // 조건 빌더
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(auction.auctionStatus.eq(SysCommonCodes.Auction.ING));

        // 가격 필터링
        if (request.getMinPrice() != null) {
            builder.and(auction.price.goe(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            builder.and(auction.price.loe(request.getMaxPrice()));
        }

        // 이자율 필터링
        if (request.getMinIr() != null) {
            builder.and(promissoryNote.interestRate.goe(request.getMinIr().multiply(BigDecimal.valueOf(10000)).intValue()));
        }
        if (request.getMaxIr() != null) {
            builder.and(promissoryNote.interestRate.loe(request.getMaxIr().multiply(BigDecimal.valueOf(10000)).intValue()));
        }

        // 상환 유형 필터링
        if (request.getRepayType() != null && !request.getRepayType().isEmpty()) {
            List<String> repayTypeCodes = request.getRepayType().stream()
                    .map(displayOrder -> {
                        // displayOrder 값으로 해당하는 Repayment 열거형 찾기
                        return SysCommonCodes.Repayment.values()[displayOrder - 1].getCode();
                    })
                    .collect(Collectors.toList());

            builder.and(promissoryNote.repaymentType.in(repayTypeCodes));
        }

        // 만기일 필터링
        if (request.getMatTerm() != null) {
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime endDate = null;

            switch (request.getMatTerm()) {
                case 1 -> endDate = now.plusMonths(1);
                case 3 -> endDate = now.plusMonths(3);
                case 6 -> endDate = now.plusMonths(6);
                case 12 -> endDate = now.plusMonths(12);
            }

            if (endDate != null) {
                builder.and(promissoryNote.maturityDate.loe(endDate.toLocalDate()));
            }
        } else if (request.getMatStart() != null && request.getMatEnd() != null) {
            builder.and(promissoryNote.maturityDate.goe(request.getMatStart().toLocalDate()));
            builder.and(promissoryNote.maturityDate.loe(request.getMatEnd().toLocalDate()));
        }

        // 쿼리 실행 - bean() 프로젝션 사용해 setter 호출
        // LEFT JOIN으로 변경
        JPAQuery<AuctionResponseDTO> query = queryFactory
                .select(Projections.bean(AuctionResponseDTO.class,
                        auction.auctionId,
                        auction.price,
                        auction.endDate,
                        auction.createdAt,
                        auction.tokenId,
                        promissoryNote.nftImage.as("nftImageUrl"),
                        promissoryNote.earlypayFlag,
                        promissoryNote.repaymentType.as("repayType"),
                        promissoryNote.debtorWalletAddress.as("drWallet"), // 채무자 wallet 주소를 drWallet 필드에 매핑
                        promissoryNote.interestRate
                ))
                .from(auction)
                .leftJoin(promissoryNote).on(auction.tokenId.eq(promissoryNote.tokenId))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(auction.endDate.asc()); // 또는 다른 정렬 기준

        // 결과 조회
        List<AuctionResponseDTO> content = query.fetch();

        // 전체 카운트 쿼리도 LEFT JOIN으로 변경
        JPAQuery<Long> countQuery = queryFactory
                .select(auction.count())
                .from(auction)
                .leftJoin(promissoryNote).on(auction.tokenId.eq(promissoryNote.tokenId))
                .where(builder);

        Long total = countQuery.fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
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
                        auction.tokenId,
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

    @Override
    public List<Auction> findSimilarAuctionsByPrincipalAndDays(Integer targetId, Long basePrincipal, Integer baseDays) {
        QAuction auction = QAuction.auction;

        BigDecimal principal = BigDecimal.valueOf(basePrincipal);

        return queryFactory.selectFrom(auction)
                .where(
                        auction.auctionStatus.eq(SysCommonCodes.Auction.COMPLETED),
                        auction.auctionId.ne(targetId),
                        auction.remainPrincipal.between(
                                principal.multiply(BigDecimal.valueOf(0.9)),
                                principal.multiply(BigDecimal.valueOf(1.1))
                        ),
                        auction.remainRepaymentDate.between(
                                (int)(baseDays * 0.9),
                                (int)(baseDays * 1.1)
                        )
                )
                .orderBy(auction.returnRate.asc())
                .fetch();
    }
}

