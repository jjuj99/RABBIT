package com.rabbit.contract.repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rabbit.contract.domain.dto.request.ContractSearchRequestDTO;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.domain.entity.QContract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ContractRepositoryCustomImpl implements ContractRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<Contract> findBySearchCondition(ContractSearchRequestDTO searchRequest, Pageable pageable) {
        QContract contract = QContract.contract;

        // 기본 조건: 삭제되지 않은 계약만 조회
        BooleanExpression baseCondition = contract.deletedFlag.eq(false);

        // 추가 조건 생성
        BooleanExpression conditions = baseCondition;

        // 검색 조건 적용
        if (searchRequest.getSearchCondition() != null) {
            // 상태 필터링
            if (searchRequest.getSearchCondition().getContractStatus() != null) {
                conditions = conditions.and(contract.contractStatus.eq(searchRequest.getSearchCondition().getContractStatus()));
            }

            // 날짜 범위 필터링
            conditions = conditions.and(createDateRangeCondition(contract,
                    searchRequest.getSearchCondition().getStartDate(),
                    searchRequest.getSearchCondition().getEndDate()));

            // 키워드 검색 (채권자 또는 채무자 이름)
            if (StringUtils.hasText(searchRequest.getSearchCondition().getKeyword())) {
                String safeKeyword = sanitizeSearchKeyword(searchRequest.getSearchCondition().getKeyword());
                conditions = conditions.and(
                        contract.creditor.nickname.containsIgnoreCase(safeKeyword)
                                .or(contract.debtor.nickname.containsIgnoreCase(safeKeyword))
                );
            }
        }

        // 쿼리 생성 및 조건 적용
        JPAQuery<Contract> query = queryFactory
                .selectFrom(contract)
                .where(conditions);

        // 정렬 적용
        OrderSpecifier<?> orderBy = createOrderSpecifier(contract, searchRequest);
        query.orderBy(orderBy);

        // 페이징 적용
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // 결과 조회
        List<Contract> result = query.fetch();

        // 전체 카운트 조회 - fetchCount() 대체
        Long totalCount = queryFactory
                .select(contract.count())
                .from(contract)
                .where(conditions)
                .fetchOne();

        // null 체크 (Long은 래퍼 클래스이므로 null 가능)
        long count = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Page<Contract> findContractsByUserIdAndType(Integer userId, String type, Pageable pageable) {
        QContract contract = QContract.contract;

        // 기본 조건: 삭제되지 않은 계약
        BooleanExpression conditions = contract.deletedFlag.eq(false);

        // 계약 유형에 따라 조건 추가
        if ("sent".equalsIgnoreCase(type)) {
            // 보낸 계약 (사용자가 채권자인 경우)
            conditions = conditions.and(contract.creditor.userId.eq(userId));
        } else if ("received".equalsIgnoreCase(type)) {
            // 받은 계약 (사용자가 채무자인 경우)
            conditions = conditions.and(contract.debtor.userId.eq(userId));
        } else {
            // 모든 관련 계약
            conditions = conditions.and(contract.creditor.userId.eq(userId).or(contract.debtor.userId.eq(userId)));
        }

        // 쿼리 실행
        JPAQuery<Contract> query = queryFactory
                .selectFrom(contract)
                .where(conditions)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 정렬 적용
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                PathBuilder<Contract> pathBuilder = new PathBuilder<>(Contract.class, "contract");
                query.orderBy(order.isAscending() ?
                        pathBuilder.getString(order.getProperty()).asc() :
                        pathBuilder.getString(order.getProperty()).desc());
            });
        } else {
            // 기본 정렬
            query.orderBy(contract.createdAt.desc());
        }

        List<Contract> result = query.fetch();

        // 전체 카운트 조회 - fetchCount() 대체
        Long totalCount = queryFactory
                .select(contract.count())
                .from(contract)
                .where(conditions)
                .fetchOne();

        // null 체크 (Long은 래퍼 클래스이므로 null 가능)
        long count = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(result, pageable, count);
    }

    /**
     * 정렬 조건 생성
     * @param contract QContract 객체
     * @param searchRequest 검색 요청 정보
     * @return 정렬 조건
     */
    private OrderSpecifier<?> createOrderSpecifier(QContract contract, ContractSearchRequestDTO searchRequest) {
        String sortBy = searchRequest.getSortBy();
        boolean isAsc = "ASC".equalsIgnoreCase(searchRequest.getSortDirection());

        // DTO와 Entity 간의 필드명 매핑
        if (StringUtils.hasText(sortBy)) {
            switch (sortBy) {
                case "createdAt":
                    return isAsc ? contract.createdAt.asc() : contract.createdAt.desc();
                case "la":  // 대출 금액 (loan amount)
                    return isAsc ? contract.loanAmount.asc() : contract.loanAmount.desc();
                case "matDt":  // 만기일 (maturity date)
                    return isAsc ? contract.maturityDate.asc() : contract.maturityDate.desc();
                case "ir":  // 이자율 (interest rate)
                    return isAsc ? contract.interestRate.asc() : contract.interestRate.desc();
                case "lt":  // 대출 기간 (loan term)
                    return isAsc ? contract.loanTerm.asc() : contract.loanTerm.desc();
                default:
                    // 기본 정렬
                    return contract.createdAt.desc();
            }
        }
        // 기본 정렬
        return contract.createdAt.desc();
    }

    /**
     * 날짜 범위 조건 생성
     * @param contract QContract 객체
     * @param startDateStr 시작 날짜 문자열 (yyyy-MM-dd)
     * @param endDateStr 종료 날짜 문자열 (yyyy-MM-dd)
     * @return 조건식 또는 null
     */
    private BooleanExpression createDateRangeCondition(QContract contract, String startDateStr, String endDateStr) {
        BooleanExpression dateCondition = null;

        try {
            if (StringUtils.hasText(startDateStr)) {
                LocalDate startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
                ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneId.systemDefault());
                dateCondition = contract.createdAt.goe(startDateTime);
            }

            if (StringUtils.hasText(endDateStr)) {
                LocalDate endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
                ZonedDateTime endDateTime = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1);

                if (dateCondition != null) {
                    dateCondition = dateCondition.and(contract.createdAt.loe(endDateTime));
                } else {
                    dateCondition = contract.createdAt.loe(endDateTime);
                }
            }
        } catch (DateTimeParseException e) {
            log.warn("날짜 형식 파싱 오류: startDate={}, endDate={}", startDateStr, endDateStr, e);
            // 날짜 형식 오류 시 조건 없음
        }

        return dateCondition != null ? dateCondition : null;
    }

    /**
     * 검색 키워드 보안 처리
     * SQL 인젝션 방지를 위한 키워드 정제
     * @param keyword 사용자 입력 키워드
     * @return 정제된 키워드
     */
    private String sanitizeSearchKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }
        // SQL 인젝션 방지를 위한 위험 문자 제거
        return keyword.replaceAll("[%_'\";]", "");
    }
}