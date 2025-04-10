package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.loan.domain.dto.response.ContractEventDTO;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final Web3j web3j;

    // 스마트 컨트랙트 Wrapper 주입 (Transfer 이벤트는 이 컨트랙트에서 발생)
    private final PromissoryNote promissoryNote;
    private final RepaymentScheduler repaymentScheduler;

    // 조회할 블록 범위 설정 (0번 블록부터 최신 블록까지)
//    private final DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(BigInteger.ZERO);
    private final DefaultBlockParameter endBlock = DefaultBlockParameterName.LATEST;

    public List<ContractEventDTO> getEventList(BigInteger tokenId) {
//        try {
            // 각 이벤트 조회를 비동기 싱글로 감쌈
            // subscribeOn(Schedulers.io()) → IO 작업용 비동기 스레드에서 실행됨
//
//            Single<List<ContractEventDTO>> repaymentSingle = Single.fromCallable(() -> getRepaymentEvents(tokenId))
//                    .subscribeOn(Schedulers.io())
//                    .timeout(5, TimeUnit.SECONDS) // 각 호출에 개별 타임아웃 적용
//                    .onErrorReturnItem(Collections.emptyList());
//
//            Single<List<ContractEventDTO>> earlyRepaymentSingle = Single.fromCallable(() -> getEarlyRepaymentPrincipalEvents(tokenId))
//                    .subscribeOn(Schedulers.io())
//                    .timeout(5, TimeUnit.SECONDS)
//                    .onErrorReturnItem(Collections.emptyList());
//
//            Single<List<ContractEventDTO>> assignmentSingle = Single.fromCallable(() -> getAssignmentEvents(tokenId))
//                    .subscribeOn(Schedulers.io())
//                    .timeout(5, TimeUnit.SECONDS)
//                    .onErrorReturnItem(Collections.emptyList());
//
//            Single<List<ContractEventDTO>> overdueSingle = Single.fromCallable(() -> getOverdueEvents(tokenId))
//                    .subscribeOn(Schedulers.io())
//                    .timeout(5, TimeUnit.SECONDS)
//                    .onErrorReturnItem(Collections.emptyList());
//
//            Single<List<ContractEventDTO>> overdueResolvedSingle = Single.fromCallable(() -> getOverdueResolvedEvents(tokenId))
//                    .subscribeOn(Schedulers.io())
//                    .timeout(5, TimeUnit.SECONDS)
//                    .onErrorReturnItem(Collections.emptyList());
//
//            // zip 연산자로 병렬 실행된 결과를 취합
//            return Single.zip(
//                            repaymentSingle,
//                            earlyRepaymentSingle,
//                            assignmentSingle,
//                            overdueSingle,
//                            overdueResolvedSingle,
//
//                            // 결과 병합 함수 (각 이벤트 결과를 하나의 리스트로 합침)
//                            (repayment, early, assign, overdue, resolved) -> {
//                                List<ContractEventDTO> merged = new ArrayList<>();
//                                merged.addAll(repayment);
//                                merged.addAll(early);
//                                merged.addAll(assign);
//                                merged.addAll(overdue);
//                                merged.addAll(resolved);
//                                return merged;
//                            })
//                    .timeout(7, TimeUnit.SECONDS) // zip 전체에도 타임아웃을 걸어 안정성 확보
//                    .blockingGet() // 최종 결과를 동기적으로 기다림 (단, 병렬 실행됨)
//                    .stream()
//                    // timestamp 기준 최신순 정렬
//                    .sorted(Comparator.comparing(ContractEventDTO::getTimestamp).reversed())
//                    // timestamp 날짜 자르기 (yyyy-MM-ddTHH:mm:ss → yyyy-MM-dd)
//                    .peek(event -> {
//                        String timestamp = event.getTimestamp();
//                        if (timestamp != null && timestamp.length() >= 10) {
//                            event.setTimestamp(timestamp.substring(0, 10));
//                        }
//                    })
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            log.error("[EventService] getEventList (병렬 처리) error", e);
//            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT 이벤트 목록 병렬 조회 중 오류가 발생했습니다.");
//        }


        try {
            List<ContractEventDTO> allEvents = new ArrayList<>();

            allEvents.addAll(getRepaymentEvents(tokenId));
            allEvents.addAll(getEarlyRepaymentPrincipalEvents(tokenId));
            allEvents.addAll(getAssignmentEvents(tokenId));
            allEvents.addAll(getOverdueEvents(tokenId));
            allEvents.addAll(getOverdueResolvedEvents(tokenId));

            // 최신순 정렬 (timestamp는 yyyy-MM-ddTHH:mm:ss 형식이므로 앞부분만 잘라서 정렬)
            return allEvents.stream()
                    .sorted((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()))
//                    .peek(event -> {
//                        // 날짜 문자열을 yyyy-MM-dd 형식으로 자르기
//                        String timestamp = event.getTimestamp();
//                        if (timestamp != null && timestamp.length() >= 10) {
//                            event.setTimestamp(timestamp.substring(0, 10));
//                        }
//                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[EventService] getEventList error", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT 이벤트 목록 조회 중 오류가 발생했습니다.");
        }
    }

    // NFT에서 발생한 상환 (RepaymentProcessed) 이벤트 조회
    public List<ContractEventDTO> getRepaymentEvents(BigInteger tokenId) {
        try {
            log.info("[블록체인] 상환 이벤트 조회 시작 - 토큰 ID: {}", tokenId);

            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = latestBlockNumber.subtract(BigInteger.valueOf(10_000)).max(BigInteger.ZERO);
            DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(startBlockNumber);

            EthFilter filter = new EthFilter(startBlock, endBlock, repaymentScheduler.getContractAddress());

            // 이벤트 시그니처 토픽
            filter.addSingleTopic(EventEncoder.encode(RepaymentScheduler.REPAYMENTPROCESSED_EVENT));

            // 첫 번째 인덱스 토픽(originalTokenId)으로 필터링
            filter.addSingleTopic("0x" + TypeEncoder.encode(new Uint256(tokenId)));

            // 발견한 이벤트 내역을 남을 리스트
            List<RepaymentScheduler.RepaymentProcessedEventResponse> eventList = new ArrayList<>();

            List<RepaymentScheduler.RepaymentProcessedEventResponse> eventResponses =
                    repaymentScheduler.repaymentProcessedEventFlowable(filter)
                            .map(event -> {
                                log.info("[블록체인] 이벤트 발견: tokenId={}, amount={}, remainingPrincipal={}, nextMpDt={}",
                                        event.tokenId, event.amount, event.remainingPrincipal, event.nextMpDt);

                                eventList.add(event);

                                return event;
                            })
                            .toList()
                            .timeout(3, TimeUnit.SECONDS)
                            .onErrorReturnItem(Collections.emptyList())
                            .blockingGet();

            log.info("[블록체인] RepaymentProcessed 이벤트 파싱 결과: {}건", eventList.size());

            return eventList.stream().map(e -> {
                String date = null;
                try {
                    EthBlock block = web3j.ethGetBlockByNumber(
                            DefaultBlockParameter.valueOf(e.log.getBlockNumber()), false).send();
                    BigInteger timestamp = block.getBlock().getTimestamp();
                    date = Instant.ofEpochSecond(timestamp.longValue())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime()
                            .toString();
                } catch (Exception ex) {
                    log.warn("블록 시간 조회 실패", ex);
                }

                return ContractEventDTO.builder()
                        .eventType("상환")                // 상환 이벤트로 표현
                        .from(e.from)                             // 이벤트에 from 주소는 없음 (필요 시 추론)
                        .to(e.to)                         // 채권자 지갑 주소
                        .intAmt(e.amount.longValue())                           // 상환 이벤트 금액
                        .timestamp(date != null ? date : e.log.getBlockNumber().toString())
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[EventService] getRepaymentEvents error", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT의 이벤트 내역 조회 중 오류가 발생했습니다.");
        }
    }

    // NFT에서 발생한 중도 상환 (EarlyRepaymentPrincipal) 이벤트 조회
    public List<ContractEventDTO> getEarlyRepaymentPrincipalEvents(BigInteger tokenId) {
        try {
            log.info("[블록체인] 중도 상환 이벤트 조회 시작 - 토큰 ID: {}", tokenId);

            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = latestBlockNumber.subtract(BigInteger.valueOf(10_000)).max(BigInteger.ZERO);
            DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(startBlockNumber);

            EthFilter filter = new EthFilter(startBlock, endBlock, repaymentScheduler.getContractAddress());

            // 이벤트 시그니처 토픽
            filter.addSingleTopic(EventEncoder.encode(RepaymentScheduler.EARLYREPAYMENTPRINCIPAL_EVENT));

            // 첫 번째 인덱스 토픽(originalTokenId)으로 필터링
            filter.addSingleTopic("0x" + TypeEncoder.encode(new Uint256(tokenId)));

            // 발견한 이벤트 내역을 남을 리스트
            List<RepaymentScheduler.EarlyRepaymentPrincipalEventResponse> eventList = new ArrayList<>();

            List<RepaymentScheduler.EarlyRepaymentPrincipalEventResponse> eventResponses =
                    repaymentScheduler.earlyRepaymentPrincipalEventFlowable(filter)
                            .map(event -> {
                                log.info("[블록체인] 이벤트 발견: tokenId={}, principalAmount={}, remainingPrincipal={}, isFullRepayment={}",
                                        event.tokenId, event.principalAmount, event.remainingPrincipal, event.isFullRepayment);

                                eventList.add(event);

                                return event;
                            })
                            .toList()
                            .timeout(3, TimeUnit.SECONDS)
                            .onErrorReturnItem(Collections.emptyList())
                            .blockingGet();

            log.info("[블록체인] EarlyRepaymentPrincipal 이벤트 파싱 결과: {}건", eventList.size());

            return eventList.stream().map(e -> {
                String date = null;
                try {
                    EthBlock block = web3j.ethGetBlockByNumber(
                            DefaultBlockParameter.valueOf(e.log.getBlockNumber()), false).send();
                    BigInteger timestamp = block.getBlock().getTimestamp();
                    date = Instant.ofEpochSecond(timestamp.longValue())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime()
                            .toString();
                } catch (Exception ex) {
                    log.warn("블록 시간 조회 실패", ex);
                }

                return ContractEventDTO.builder()
                        .eventType("중도 상환")                // 중도 상환 이벤트로 표현
                        .from(null)                             // 이벤트에 from 주소는 없음 (필요 시 추론)
                        .to(null)                         // 채권자 지갑 주소
                        .intAmt(e.principalAmount.longValue())                           // 상환 이벤트 금액
                        .timestamp(date != null ? date : e.log.getBlockNumber().toString())
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[EventService] getEarlyRepaymentPrincipalEvents error", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT의 이벤트 내역 조회 중 오류가 발생했습니다.");
        }
    }

    // NFT에서 발생한 양도양수 (AppendixNFTMinted) 이벤트 조회
    public List<ContractEventDTO> getAssignmentEvents(BigInteger tokenId) {
        try {
            log.info("[블록체인] 양도양수 이벤트 조회 시작 - 토큰 ID: {}", tokenId);

            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = latestBlockNumber.subtract(BigInteger.valueOf(10_000)).max(BigInteger.ZERO);
            DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(startBlockNumber);

            EthFilter filter = new EthFilter(startBlock, endBlock, promissoryNote.getContractAddress());

            // 이벤트 시그니처 토픽
            filter.addSingleTopic(EventEncoder.encode(PromissoryNote.APPENDIXNFTMINTED_EVENT));

            // 첫 번째 인덱스 토픽(appendixTokenId)는 무시 - null을 추가
            filter.addNullTopic(); 

            // 두 번째 인덱스 토픽(originalTokenId)으로 필터링
            filter.addSingleTopic("0x" + TypeEncoder.encode(new Uint256(tokenId)));

            // 발견한 이벤트 내역을 남을 리스트
            List<PromissoryNote.AppendixNFTMintedEventResponse> eventList = new ArrayList<>();

            List<PromissoryNote.AppendixNFTMintedEventResponse> eventResponses =
                    promissoryNote.appendixNFTMintedEventFlowable(filter)
                            .map(event -> {
                                log.info("[블록체인] 이벤트 발견: appendixTokenId={}, originalTokenId={}, newOwner={}",
                                        event.appendixTokenId, event.originalTokenId, event.newOwner);

                                eventList.add(event);

                                return event;
                            })
                            .toList()
                            .timeout(3, TimeUnit.SECONDS)
                            .onErrorReturnItem(Collections.emptyList())
                            .blockingGet();

            log.info("[블록체인] AppendixNFTMinted 이벤트 파싱 결과: {}건", eventList.size());

            return eventList.stream().map(e -> {
                String date = null;
                try {
                    EthBlock block = web3j.ethGetBlockByNumber(
                            DefaultBlockParameter.valueOf(e.log.getBlockNumber()), false).send();
                    BigInteger timestamp = block.getBlock().getTimestamp();
                    date = Instant.ofEpochSecond(timestamp.longValue())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime()
                            .toString();
                } catch (Exception ex) {
                    log.warn("블록 시간 조회 실패", ex);
                }

                return ContractEventDTO.builder()
                        .eventType("양도")                // 양도 이벤트로 표현
                        .from(e.from)                             // 이벤트에 from 주소는 없음 (필요 시 추론)
                        .to(e.newOwner)                         // 양수인 지갑 주소
                        .intAmt(null)                           // 상환 이벤트 아님
                        .timestamp(date != null ? date : e.log.getBlockNumber().toString())
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[EventService] getAssignmentEvents error", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT의 이벤트 내역 조회 중 오류가 발생했습니다.");
        }
    }

    // NFT에서 발생한 연체(RepaymentOverdue) 이벤트 조회
    public List<ContractEventDTO> getOverdueEvents(BigInteger tokenId) {
        try {
            log.info("[블록체인] 연체 이벤트 조회 시작 - 토큰 ID: {}", tokenId);

            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = latestBlockNumber.subtract(BigInteger.valueOf(10_000)).max(BigInteger.ZERO);
            DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(startBlockNumber);

            EthFilter filter = new EthFilter(startBlock, endBlock, promissoryNote.getContractAddress());

            // 이벤트 시그니처 토픽
            filter.addSingleTopic(EventEncoder.encode(RepaymentScheduler.REPAYMENTOVERDUE_EVENT));

            // 첫 번째 인덱스 토픽(originalTokenId)으로 필터링
            filter.addSingleTopic("0x" + TypeEncoder.encode(new Uint256(tokenId)));

            // 발견한 이벤트 내역을 남을 리스트
            List<RepaymentScheduler.RepaymentOverdueEventResponse> eventList = new ArrayList<>();

            List<RepaymentScheduler.RepaymentOverdueEventResponse> eventResponses =
                    repaymentScheduler.repaymentOverdueEventFlowable(filter)
                            .map(event -> {
                                log.info("[블록체인] 이벤트 발견: tokenId={}, overdueStartDate={}, totalDefaultCount={}",
                                        event.tokenId, event.overdueStartDate, event.totalDefaultCount);

                                eventList.add(event);

                                return event;
                            })
                            .toList()
                            .timeout(3, TimeUnit.SECONDS)
                            .onErrorReturnItem(Collections.emptyList())
                            .blockingGet();

            log.info("[블록체인] RepaymentOverdue 이벤트 파싱 결과: {}건", eventList.size());

            return eventList.stream().map(e -> {
                String date = null;
                try {
                    EthBlock block = web3j.ethGetBlockByNumber(
                            DefaultBlockParameter.valueOf(e.log.getBlockNumber()), false).send();
                    BigInteger timestamp = block.getBlock().getTimestamp();
                    date = Instant.ofEpochSecond(timestamp.longValue())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime()
                            .toString();
                } catch (Exception ex) {
                    log.warn("블록 시간 조회 실패", ex);
                }

                return ContractEventDTO.builder()
                        .eventType("연체")                // 연체 이벤트로 표현
                        .from(null)                             // 채무자
                        .to(null)                         // 채권자 지갑 주소
                        .intAmt(null)                           // 금액
                        .timestamp(date != null ? date : e.log.getBlockNumber().toString())
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[EventService] getOverdueEvents error", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT의 이벤트 내역 조회 중 오류가 발생했습니다.");
        }
    }

    // NFT에서 발생한 연체 상환 (OverdueResolved) 이벤트 조회
    public List<ContractEventDTO> getOverdueResolvedEvents(BigInteger tokenId) {
        try {
            log.info("[블록체인] 연체 상환 이벤트 조회 시작 - 토큰 ID: {}", tokenId);

            BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            BigInteger startBlockNumber = latestBlockNumber.subtract(BigInteger.valueOf(10_000)).max(BigInteger.ZERO);
            DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(startBlockNumber);

            EthFilter filter = new EthFilter(startBlock, endBlock, promissoryNote.getContractAddress());

            // 이벤트 시그니처 토픽
            filter.addSingleTopic(EventEncoder.encode(RepaymentScheduler.OVERDUERESOLVED_EVENT));

            // 첫 번째 인덱스 토픽(originalTokenId)으로 필터링
            filter.addSingleTopic("0x" + TypeEncoder.encode(new Uint256(tokenId)));

            // 발견한 이벤트 내역을 남을 리스트
            List<RepaymentScheduler.OverdueResolvedEventResponse> eventList = new ArrayList<>();

            List<RepaymentScheduler.OverdueResolvedEventResponse> eventResponses =
                    repaymentScheduler.overdueResolvedEventFlowable(filter)
                            .map(event -> {
                                log.info("[블록체인] 이벤트 발견: tokenId={}, paidOverdueAmount={}",
                                        event.tokenId, event.paidOverdueAmount);

                                eventList.add(event);

                                return event;
                            })
                            .toList()
                            .timeout(3, TimeUnit.SECONDS)
                            .onErrorReturnItem(Collections.emptyList())
                            .blockingGet();

            log.info("[블록체인] OverdueResolved 이벤트 파싱 결과: {}건", eventList.size());

            return eventList.stream().map(e -> {
                String date = null;
                try {
                    EthBlock block = web3j.ethGetBlockByNumber(
                            DefaultBlockParameter.valueOf(e.log.getBlockNumber()), false).send();
                    BigInteger timestamp = block.getBlock().getTimestamp();
                    date = Instant.ofEpochSecond(timestamp.longValue())
                            .atZone(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime()
                            .toString();
                } catch (Exception ex) {
                    log.warn("블록 시간 조회 실패", ex);
                }

                return ContractEventDTO.builder()
                        .eventType("연체 상환")                // 연체 이벤트로 표현
                        .from(e.from)                             // 채무자
                        .to(e.to)                         // 채권자 지갑 주소
                        .intAmt(e.paidOverdueAmount.longValue())                           // 금액
                        .timestamp(date != null ? date : e.log.getBlockNumber().toString())
                        .build();
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("[EventService] getOverdueResolvedEvents error", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT의 이벤트 내역 조회 중 오류가 발생했습니다.");
        }
    }
}
