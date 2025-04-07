package com.rabbit.loan.service;

import com.rabbit.blockchain.service.EventService;
import com.rabbit.auction.repository.AuctionRepository;
import com.rabbit.bankApi.service.BankService;
import com.rabbit.blockchain.domain.dto.RepaymentInfo;
import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.service.RepaymentSchedulerService;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.global.util.DateTimeUtils;
import com.rabbit.global.util.LoanUtil;
import com.rabbit.loan.domain.dto.response.LentAuctionResponseDTO;
import com.rabbit.loan.domain.dto.response.*;
import com.rabbit.loan.util.DataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final PromissoryNoteService promissoryNoteService;
    private final RepaymentSchedulerService repaymentSchedulerService;
    private final EventService eventService;

    private final ContractRepository contractRepository;
    private final AuctionRepository auctionRepository;
    private final BankService bankService;
    private final LoanUtil loanUtil;

    public BorrowSummaryResponseDTO borrowSummary(int userId) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
//        List<Contract> contracts = contractRepository.findByDebtorId(userId);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(Contract.builder()
                .contractId(1)
                .tokenId(BigInteger.valueOf(1))
                .build());
        contracts.add(Contract.builder()
                .contractId(2)
                .tokenId(BigInteger.valueOf(2))
                .build());
        contracts.add(Contract.builder()
                .contractId(3)
                .tokenId(BigInteger.valueOf(3))
                .build());

        // 1-2. 만약 리스트가 비어있다면, 빈 값을 담아서 객체 반환
        if (contracts.isEmpty()) {
            return  BorrowSummaryResponseDTO.builder()
                    .totalOutgoingLa(0L)
                    .monthlyOutgoingLa(0L)
                    .nextOutgoingDt(null)
                    .build();
        }

        try {
            long totalOutgoingLa = 0L;
            long monthlyOutgoingLa = 0L;
            List<BigInteger> outgoingDts = new ArrayList<>();

            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (Contract contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인
                if(!repaymentInfo.activeFlag) continue;

                // 3. 게산
                totalOutgoingLa += promissoryMetadata.la.longValue();
                monthlyOutgoingLa += DataUtil.calculateMonthlyPayment(repaymentInfo);
                outgoingDts.add(repaymentInfo.nextMpDt);
            }

            // 4. 최소 날짜 찾기
            String nextOutgoingDt = DataUtil.getFastestNextPaymentDate(outgoingDts);

            return BorrowSummaryResponseDTO.builder()
                    .totalOutgoingLa(totalOutgoingLa)
                    .monthlyOutgoingLa(monthlyOutgoingLa)
                    .nextOutgoingDt(nextOutgoingDt)
                    .build();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }
    }

    public PageResponseDTO<BorrowListResponseDTO> borrowList(int userId, Pageable pageable) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
//        List<Contract> contracts = contractRepository.findByDebtorId(userId);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(Contract.builder()
                .contractId(1)
                .tokenId(BigInteger.valueOf(1))
                .build());
        contracts.add(Contract.builder()
                .contractId(2)
                .tokenId(BigInteger.valueOf(2))
                .build());
        contracts.add(Contract.builder()
                .contractId(3)
                .tokenId(BigInteger.valueOf(3))
                .build());

        // 1-2. 만약 리스트가 비어있다면, 빈 리스트를 반환
        if (contracts.isEmpty()) {
            return PageResponseDTO.<BorrowListResponseDTO>builder()
                    .content(Collections.emptyList())
                    .pageNumber(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalElements(0)
                    .build();
        }

        List<BorrowListResponseDTO> response = new ArrayList<>();

        try {
            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (Contract contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인
                if(!repaymentInfo.activeFlag) continue;

                response.add(BorrowListResponseDTO.builder()
                        .contractId(contract.getContractId())
                        .tokenId(contract.getTokenId())
                        .nftImage(promissoryMetadata.nftImage)
                        .crName(promissoryMetadata.crInfo.crName)
                        .crWallet(promissoryMetadata.crInfo.crWalletAddress)
                        .la(promissoryMetadata.la.longValue())
                        .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                        .matDt(promissoryMetadata.matDt)
                        .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                        .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueInfo.overdueFlag))
                        .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                        .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                        .aoi(repaymentInfo.overdueInfo.aoi.longValue())
                        .aoiDays(repaymentInfo.overdueInfo.defCnt.intValue())
                        .build()
                );
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }

        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        List<BorrowListResponseDTO> pagedList = response.stream()
                .skip(offset)
                .limit(limit)
                .toList();

        return PageResponseDTO.<BorrowListResponseDTO>builder()
                .content(pagedList)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(response.size())
                .build();
    }

    public BorrowDetailResponseDTO borrowDetail(int contractId, int userId) {
        // 1. 해당 Id의 차용증 정보를 가져온다.
//        Contract contract = contractRepository.findByContractIdAndDeletedFlagFalse(contractId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 차용증의 정보가 존재하지 않습니다."));
//
//        // 차용증에 채권자, 채무자에 userId가 아니라면 (당사자가 아니라면 에러처리 추가해야 함)
//        if (contract.getCreditor().getUserId() != userId && contract.getDebtor().getUserId() != userId) {
//            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 정보에 접근 권한이 없습니다.");
//        }

        Contract contract = Contract.builder()
                .contractId(17)
                .tokenId(BigInteger.valueOf(11))
                .build();

        try {
            PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
            RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

            List<ContractEventDTO> events = eventService.getEventList(contract.getTokenId());

            return BorrowDetailResponseDTO.builder()
                    .contractId(contractId)
                    .tokenId(contract.getTokenId())
                    .nftImage(promissoryMetadata.nftImage)
                    .crName(promissoryMetadata.crInfo.crName)
                    .crWallet(promissoryMetadata.crInfo.crWalletAddress)
                    .la(promissoryMetadata.la.longValue())
                    .totalAmount(DataUtil.calculateTotalAmount(repaymentInfo))
                    .repayType(DataUtil.getRepayTypeString(repaymentInfo.repayType))
                    .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                    .dir(DataUtil.getRateAsDouble(promissoryMetadata.dir))
                    .defCnt(repaymentInfo.overdueInfo.defCnt.intValue())
                    .contractDt(promissoryMetadata.contractDate)
                    .matDt(promissoryMetadata.matDt)
                    .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                    .progressRate(DataUtil.calculateProgressRate(promissoryMetadata.contractDate, promissoryMetadata.matDt)) // 금액 기준? 날짜 기준? -> 일단 날짜로
                    .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueInfo.overdueFlag))
                    .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                    .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                    .aoi(repaymentInfo.overdueInfo.aoi.longValue())
                    .aoiDays(repaymentInfo.overdueInfo.defCnt.intValue())
                    .earlypayFlag(promissoryMetadata.earlyPayFlag)
                    .earlypayFee(DataUtil.getRateAsDouble(promissoryMetadata.earlyPayFee))
                    .accel(repaymentInfo.overdueInfo.accel.intValue())
                    .accelDir(20) // 기한 이익 상실 연체 이자율을 없는 거 같은데
                    .addTerms(promissoryMetadata.addTerms.addTerms)
                    .addTermsHash(promissoryMetadata.addTerms.addTermsHash)
                     .eventList(events) // 이벤트 내역 불러오기도 있는가
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }
    }

    public LentSummaryResponseDTO lentSummary(int userId) {
        // 1. 유저 Id로 해당 유저가 채권자인 차용증 리스트를 호출한다.
//        List<Contract> contracts = contractRepository.findByCreditorId(userId);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(Contract.builder()
                .contractId(1)
                .tokenId(BigInteger.valueOf(1))
                .build());
        contracts.add(Contract.builder()
                .contractId(2)
                .tokenId(BigInteger.valueOf(2))
                .build());
        contracts.add(Contract.builder()
                .contractId(3)
                .tokenId(BigInteger.valueOf(3))
                .build());

        // 1-2. 만약 리스트가 비어있다면, 빈 값을 담아서 객체 반환
        if (contracts.isEmpty()) {
            return  LentSummaryResponseDTO.builder()
                    .totalIncomingLa(0L)
                    .monthlyIncomingLa(0L)
                    .nextIncomingDt(null)
                    .build();
        }

        try {
            long totalIncomingLa = 0L;
            long monthlyIncomingLa = 0L;
            List<BigInteger> incomingDts = new ArrayList<>();

            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (Contract contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인
                if(!repaymentInfo.activeFlag) continue;

                // 3. 게산
                totalIncomingLa += promissoryMetadata.la.longValue();
                monthlyIncomingLa += DataUtil.calculateMonthlyPayment(repaymentInfo);
                incomingDts.add(repaymentInfo.nextMpDt);
            }

            // 4. 최소 날짜 찾기
            String nextIncomingDt = DataUtil.getFastestNextPaymentDate(incomingDts);

            return LentSummaryResponseDTO.builder()
                    .totalIncomingLa(totalIncomingLa)
                    .monthlyIncomingLa(monthlyIncomingLa)
                    .nextIncomingDt(nextIncomingDt)
                    .build();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }
    }

    public PageResponseDTO<LentListResponseDTO> lentList(int userId, Pageable pageable) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
//        List<Contract> contracts = contractRepository.findByCreditorId(userId);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(Contract.builder()
                .contractId(1)
                .tokenId(BigInteger.valueOf(1))
                .build());
        contracts.add(Contract.builder()
                .contractId(2)
                .tokenId(BigInteger.valueOf(2))
                .build());
        contracts.add(Contract.builder()
                .contractId(3)
                .tokenId(BigInteger.valueOf(3))
                .build());

        // 1-2. 만약 리스트가 비어있다면, 빈 리스트를 반환
        if (contracts.isEmpty()) {
            return  PageResponseDTO.<LentListResponseDTO>builder()
                    .content(Collections.emptyList())
                    .pageNumber(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalElements(0)
                    .build();
        }

        List<LentListResponseDTO> response = new ArrayList<>();

        try {
            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (Contract contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인
                if(!repaymentInfo.activeFlag) continue;

                response.add(LentListResponseDTO.builder()
                        .contractId(contract.getContractId())
                        .tokenId(contract.getTokenId())
                        .nftImage(promissoryMetadata.nftImage)
                        .drName(promissoryMetadata.drInfo.drName)
                        .drWallet(promissoryMetadata.drInfo.drWalletAddress)
                        .la(promissoryMetadata.la.longValue())
                        .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                        .matDt(promissoryMetadata.matDt)
                        .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                        .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueInfo.overdueFlag))
                        .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                        .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                        .aoi(repaymentInfo.overdueInfo.aoi.longValue())
                        .aoiDays(repaymentInfo.overdueInfo.defCnt.intValue())
                        .build()
                );
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }

        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        List<LentListResponseDTO> pagedList = response.stream()
                .skip(offset)
                .limit(limit)
                .toList();

        return PageResponseDTO.<LentListResponseDTO>builder()
                .content(pagedList)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(response.size())
                .build();
    }

    public LentDetailResponseDTO lentDetail(int contractId, int userId) {
        // 1. 해당 차용증 Id의 차용증 객체 호출
//        Contract contract = contractRepository.findByContractIdAndDeletedFlagFalse(contractId)
//                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 차용증의 정보가 존재하지 않습니다."));
//
//        // 차용증에 채권자, 채무자에 userId가 아니라면 (당사자가 아니라면 에러처리 추가해야 함)
//        if (contract.getCreditor().getUserId() != userId && contract.getDebtor().getUserId() != userId) {
//            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 정보에 접근 권한이 없습니다.");
//        }

        Contract contract = Contract.builder()
                .contractId(3)
                .tokenId(BigInteger.valueOf(3))
                .build();

        try {
            PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
            RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

            List<ContractEventDTO> events = eventService.getEventList(contract.getTokenId());

            return LentDetailResponseDTO.builder()
                    .contractId(contractId)
                    .tokenId(contract.getTokenId())
                    .nftImage(promissoryMetadata.nftImage)
                    .drName(promissoryMetadata.drInfo.drName)
                    .drWallet(promissoryMetadata.drInfo.drWalletAddress)
                    .la(promissoryMetadata.la.longValue())
                    .totalAmount(DataUtil.calculateTotalAmount(repaymentInfo))
                    .repayType(DataUtil.getRepayTypeString(repaymentInfo.repayType))
                    .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                    .dir(DataUtil.getRateAsDouble(promissoryMetadata.dir))
                    .defCnt(repaymentInfo.overdueInfo.defCnt.intValue())
                    .contractDt(promissoryMetadata.contractDate)
                    .matDt(promissoryMetadata.matDt)
                    .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                    .progressRate(DataUtil.calculateProgressRate(promissoryMetadata.contractDate, promissoryMetadata.matDt)) // 금액 기준? 날짜 기준? -> 일단 날짜로
                    .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueInfo.overdueFlag))
                    .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                    .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                    .aoi(repaymentInfo.overdueInfo.aoi.longValue())
                    .aoiDays(repaymentInfo.overdueInfo.defCnt.intValue())
                    .earlypayFlag(promissoryMetadata.earlyPayFlag)
                    .earlypayFee(DataUtil.getRateAsDouble(promissoryMetadata.earlyPayFee))
                    .accel(repaymentInfo.overdueInfo.accel.intValue())
                    .accelDir(20) // 기한 이익 상실 연체 이자율을 없는 거 같은데
                    .addTerms(promissoryMetadata.addTerms.addTerms)
                    .addTermsHash(promissoryMetadata.addTerms.addTermsHash)
                    .eventList(events) // 이벤트 내역 불러오기도 있는가
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }
    }

    public PageResponseDTO<LentAuctionResponseDTO> getAuctionAvailable(Integer userId, Pageable pageable) {
        List<Contract> contracts = contractRepository.findByCreditorId(userId);

        // 이미 경매중인 차용증 제외
        List<LentAuctionResponseDTO> content = contracts.stream()
                .filter(contract -> !auctionRepository.existsByTokenIdAndAuctionStatus(
                        contract.getTokenId(), SysCommonCodes.Auction.ING))
                .map(contract -> {
                    try {
                        PromissoryNote.PromissoryMetadata metadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                        RepaymentInfo repaymentInfo = repaymentSchedulerService.getRepaymentInfo(contract.getTokenId());

                        // 채무자 신용점수 조회
                        String creditScore = bankService.getCreditScore(contract.getDebtor().getUserId());

                        BigDecimal ir = new BigDecimal(metadata.ir).divide(BigDecimal.valueOf(10000));
                        BigDecimal dir = new BigDecimal(metadata.dir).divide(BigDecimal.valueOf(10000));
                        BigDecimal earlyPayFee = new BigDecimal(metadata.earlyPayFee).divide(BigDecimal.valueOf(10000));

                        // 만기수취액 계산
                        BigDecimal totalAmount = loanUtil.calculateTotalRepaymentAmount(
                                new BigDecimal(repaymentInfo.remainingPrincipal),
                                ir,
                                repaymentInfo.remainingPayments.intValue(),
                                SysCommonCodes.Repayment.toCalculationType(metadata.repayType),
                                LoanUtil.RoundingStrategy.HALF_UP,
                                LoanUtil.TruncationStrategy.WON,
                                LoanUtil.LegalLimits.getDefaultLimits()
                        );

                        return LentAuctionResponseDTO.builder()
                                .crId(contract.getCreditor().getUserId())
                                .crName(contract.getCreditor().getUserName())
                                .matDt(DateTimeUtils.toZonedDateTimeAtEndOfDay(metadata.matDt))
                                .tokenId(contract.getTokenId())
                                .la(repaymentInfo.remainingPrincipal.longValue())
                                .ir(ir)
                                .totalAmount(totalAmount.longValue()) // 수취액 로직 필요 시 수정
                                .repayType(SysCommonCodes.Repayment.fromCode(metadata.repayType).getCodeName())
                                .dir(dir)
                                .earlypayFlag(metadata.earlyPayFlag)
                                .earlypayFee(earlyPayFee)
                                .defCnt(repaymentInfo.defCnt.intValue())
                                .creditScore(creditScore)
                                .build();
                    } catch (Exception e) {
                        log.warn("[채권자 NFT 조회 실패] tokenId={}", contract.getTokenId(), e);
                        throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "경매 가능한 차용증 조회에 실패했습니다.");
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        int offset = (int) pageable.getOffset();
        int pageSize = pageable.getPageSize();

        List<LentAuctionResponseDTO> paged = content.stream()
                .skip(offset)
                .limit(pageSize)
                .toList();

        return PageResponseDTO.<LentAuctionResponseDTO>builder()
                .content(paged)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements(content.size())
                .build();
    }
}
