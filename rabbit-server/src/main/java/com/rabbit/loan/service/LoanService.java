package com.rabbit.loan.service;

import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.service.RepaymentSchedulerService;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.loan.domain.dto.response.*;
import com.rabbit.loan.util.DataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final PromissoryNoteService promissoryNoteService;
    private final RepaymentSchedulerService repaymentSchedulerService;

    public BorrowSummaryResponseDTO borrowSummary(int userId) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
        List<BorrowListResponseDTO> contracts = new ArrayList<>(); // <-- 객체 나중에 바꿔야 함!!

        // 1-2. 만약 리스트가 비어있다면, 빈 값을 담아서 객체 반환
        if (contracts.isEmpty()) {
            return  BorrowSummaryResponseDTO.builder()
                    .totalOutgoingLa(0L)
                    .monthlyOutgoingLa(0L)
                    .nextOutgoingDt(null)
                    .build();
        }

        try {
            Long totalOutgoingLa = 0L;
            Long monthlyOutgoingLa = 0L;
            List<BigInteger> outgoingDts = new ArrayList<>();

            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (BorrowListResponseDTO contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인 추가

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
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }
    }

    public List<BorrowListResponseDTO> borrowList(int userId) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
        List<BorrowListResponseDTO> contracts = new ArrayList<>(); // <-- 객체 나중에 바꿔야 함!!

        // 1-2. 만약 리스트가 비어있다면, 빈 리스트를 반환
        if (contracts.isEmpty()) {
            return  Collections.emptyList();
        }

        try {
            List<BorrowListResponseDTO> response = new ArrayList<>();

            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (BorrowListResponseDTO contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인 추가

                response.add(BorrowListResponseDTO.builder()
                        .contractId(1)
                        .tokenId(contract.getTokenId())
                        .nftImage(promissoryMetadata.nftImage)
                        .crName(promissoryMetadata.crInfo.crName)
                        .crWallet(promissoryMetadata.crInfo.crWalletAddress)
                        .la(promissoryMetadata.la.longValue())
                        .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                        .matDt(promissoryMetadata.matDt)
                        .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                        .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueFlag))
                        .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                        .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                        .aoi(repaymentInfo.aoi.longValue())
                        .aoiDays(repaymentInfo.defCnt.intValue())
                        .build()
                );
            }

            return response;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }
    }

    public BorrowDetailResponseDTO borrowDetail(int contractId, int userId) {
        // 1. 해당 차용증 Id에서 token Id를 가져온다.
        BigInteger tokenId = BigInteger.valueOf(4);

        // 차용증에 채권자, 채무자에 userId가 아니라면 (당사자가 아니라면 에러처리 추가해야 함)

        try {
            PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(tokenId);
            RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(tokenId);

            return BorrowDetailResponseDTO.builder()
                    .contractId(1)
                    .tokenId(tokenId)
                    .nftImage(promissoryMetadata.nftImage)
                    .crName(promissoryMetadata.crInfo.crName)
                    .crWallet(promissoryMetadata.crInfo.crWalletAddress)
                    .la(promissoryMetadata.la.longValue())
                    .totalAmount(DataUtil.calculateTotalAmount(promissoryMetadata, repaymentInfo))
                    .repayType(repaymentInfo.repayType)
                    .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                    .dir(DataUtil.getRateAsDouble(promissoryMetadata.dir))
                    .defCnt(repaymentInfo.defCnt.intValue())
                    .contractDt(promissoryMetadata.contractDate)
                    .matDt(promissoryMetadata.matDt)
                    .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                    .progressRate(DataUtil.calculateProgressRate(promissoryMetadata.contractDate, promissoryMetadata.matDt)) // 금액 기준? 날짜 기준? -> 일단 날짜로
                    .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueFlag))
                    .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                    .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                    .aoi(repaymentInfo.aoi.longValue())
                    .aoiDays(repaymentInfo.defCnt.intValue())
                    .earlypayFlag(promissoryMetadata.earlyPayFlag)
                    .earlypayFee(DataUtil.getRateAsDouble(promissoryMetadata.earlyPayFee))
                    .accel(repaymentInfo.accel.intValue())
                    .accelDir(0) // 기한 이익 상실 연체 이자율을 없는 거 같은데
                    .addTerms(promissoryMetadata.addTerms.addTerms)
                    // .eventList() // 이벤트 내역 불러오기도 있는가
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }
    }

    public LentSummaryResponseDTO lentSummary(int userId) {
        // 1. 유저 Id로 해당 유저가 채권자인 차용증 리스트를 호출한다.
        List<BorrowListResponseDTO> contracts = new ArrayList<>(); // <-- 객체 나중에 바꿔야 함!!

        // 1-2. 만약 리스트가 비어있다면, 빈 값을 담아서 객체 반환
        if (contracts.isEmpty()) {
            return  LentSummaryResponseDTO.builder()
                    .totalIncomingLa(0L)
                    .monthlyIncomingLa(0L)
                    .nextIncomingDt(null)
                    .build();
        }

        try {
            Long totalIncomingLa = 0L;
            Long monthlyIncomingLa = 0L;
            List<BigInteger> incomingDts = new ArrayList<>();

            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (BorrowListResponseDTO contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인 추가

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

    public List<LentListResponseDTO> lentList(int userId) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
        List<LentListResponseDTO> contracts = new ArrayList<>(); // <-- 객체 나중에 바꿔야 함!!

        // 1-2. 만약 리스트가 비어있다면, 빈 리스트를 반환
        if (contracts.isEmpty()) {
            return  Collections.emptyList();
        }

        try {
            List<LentListResponseDTO> response = new ArrayList<>();

            // 2. 토큰 Id를 조회하며, 각 NFT 정보를 호출
            for (LentListResponseDTO contract : contracts) {
                PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(contract.getTokenId());
                RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(contract.getTokenId());

                // 상환 상태가 활성화되어있는지 확인 추가

                response.add(LentListResponseDTO.builder()
                        .contractId(1)
                        .tokenId(contract.getTokenId())
                        .nftImage(promissoryMetadata.nftImage)
                        .drName(promissoryMetadata.drInfo.drName)
                        .drWallet(promissoryMetadata.drInfo.drWalletAddress)
                        .la(promissoryMetadata.la.longValue())
                        .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                        .matDt(promissoryMetadata.matDt)
                        .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                        .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueFlag))
                        .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                        .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                        .aoi(repaymentInfo.aoi.longValue())
                        .aoiDays(repaymentInfo.defCnt.intValue())
                        .build()
                );
            }

            return response;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }
    }

    public LentDetailResponseDTO lentDetail(int contractId, int userId) {
        // 1. 해당 차용증 Id에서 token Id를 가져온다.
        BigInteger tokenId = BigInteger.valueOf(4);

        // 차용증에 채권자, 채무자에 userId가 아니라면 (당사자가 아니라면 에러처리 추가해야 함)

        try {
            PromissoryNote.PromissoryMetadata promissoryMetadata = promissoryNoteService.getPromissoryMetadata(tokenId);
            RepaymentScheduler.RepaymentInfo repaymentInfo = repaymentSchedulerService.getPaymentInfo(tokenId);

            return LentDetailResponseDTO.builder()
                    .contractId(1)
                    .tokenId(tokenId)
                    .nftImage(promissoryMetadata.nftImage)
                    .drName(promissoryMetadata.drInfo.drName)
                    .drWallet(promissoryMetadata.drInfo.drWalletAddress)
                    .la(promissoryMetadata.la.longValue())
                    .totalAmount(DataUtil.calculateTotalAmount(promissoryMetadata, repaymentInfo))
                    .repayType(repaymentInfo.repayType)
                    .ir(DataUtil.getRateAsDouble(promissoryMetadata.ir))
                    .dir(DataUtil.getRateAsDouble(promissoryMetadata.dir))
                    .defCnt(repaymentInfo.defCnt.intValue())
                    .contractDt(promissoryMetadata.contractDate)
                    .matDt(promissoryMetadata.matDt)
                    .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                    .progressRate(DataUtil.calculateProgressRate(promissoryMetadata.contractDate, promissoryMetadata.matDt)) // 금액 기준? 날짜 기준? -> 일단 날짜로
                    .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueFlag))
                    .nextMpDt(DataUtil.getNextMpDt(repaymentInfo.nextMpDt))
                    .nextAmount(DataUtil.calculateMonthlyPayment(repaymentInfo))
                    .aoi(repaymentInfo.aoi.longValue())
                    .aoiDays(repaymentInfo.defCnt.intValue())
                    .earlypayFlag(promissoryMetadata.earlyPayFlag)
                    .earlypayFee(DataUtil.getRateAsDouble(promissoryMetadata.earlyPayFee))
                    .accel(repaymentInfo.accel.intValue())
                    .accelDir(0) // 기한 이익 상실 연체 이자율을 없는 거 같은데
                    .addTerms(promissoryMetadata.addTerms.addTerms)
                    // .eventList() // 이벤트 내역 불러오기도 있는가
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }
    }
}
