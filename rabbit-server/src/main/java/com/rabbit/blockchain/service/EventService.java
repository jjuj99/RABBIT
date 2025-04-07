package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.loan.domain.dto.response.ContractEventDTO;
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
    private final DefaultBlockParameter startBlock = DefaultBlockParameter.valueOf(BigInteger.ZERO);
    private final DefaultBlockParameter endBlock = DefaultBlockParameterName.LATEST;

    public List<ContractEventDTO> getEventList(BigInteger tokenId) {
        try {
            List<ContractEventDTO> allEvents = new ArrayList<>();

            allEvents.addAll(getRepaymentEvents(tokenId));
            allEvents.addAll(getAssignmentEvents(tokenId));
            allEvents.addAll(getOverdueEvents(tokenId));
            allEvents.addAll(getOverdueResolvedEvents(tokenId));

            // 최신순 정렬 (timestamp는 yyyy-MM-ddTHH:mm:ss 형식이므로 앞부분만 잘라서 정렬)
            return allEvents.stream()
                    .sorted((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()))
                    .peek(event -> {
                        // 날짜 문자열을 yyyy-MM-dd 형식으로 자르기
                        String timestamp = event.getTimestamp();
                        if (timestamp != null && timestamp.length() >= 10) {
                            event.setTimestamp(timestamp.substring(0, 10));
                        }
                    })
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

    // NFT에서 발생한 양도양수 (AppendixNFTMinted) 이벤트 조회
    public List<ContractEventDTO> getAssignmentEvents(BigInteger tokenId) {
        try {
            log.info("[블록체인] 양도양수 이벤트 조회 시작 - 토큰 ID: {}", tokenId);

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
