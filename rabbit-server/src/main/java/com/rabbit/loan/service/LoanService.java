package com.rabbit.loan.service;

import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.service.RepaymentSchedulerService;
import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.loan.domain.dto.response.BorrowDetailResponseDTO;
import com.rabbit.loan.domain.dto.response.BorrowListResponseDTO;
import com.rabbit.loan.domain.dto.response.BorrowSummaryResponseDTO;
import com.rabbit.loan.domain.dto.response.LentSummaryResponseDTO;
import com.rabbit.loan.util.DataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        return null;
    }

    public List<BorrowListResponseDTO> borrowList(int userId) {
        // 1. 유저 Id로 해당 유저가 채무자인 차용증 리스트를 호출한다.
        List<BorrowListResponseDTO> contracts = new ArrayList<>(); // <-- 객체 나중에 바꿔야 함!!

        // 상환 상태가 활성화되어있어야 한다.

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

                response.add(BorrowListResponseDTO.builder()
                        .contractId(1)
                        .tokenId(contract.getTokenId())
                        .nftImage(promissoryMetadata.nftImage)
                        .crName(promissoryMetadata.crInfo.crName)
                        .crWallet(promissoryMetadata.crInfo.crWalletAddress)
                        .la(promissoryMetadata.la.longValue())
                        .ir(DataUtil.getIrAsDouble(promissoryMetadata.ir))
                        .matDt(promissoryMetadata.matDt)
                        .remainTerms(DataUtil.calculateRemainTerms(promissoryMetadata.matDt))
                        .pnStatus(DataUtil.getStatusString(repaymentInfo.overdueFlag))
                        .nextMpDt(String.valueOf(repaymentInfo.nextMpDt)) // String으로 변경 예정이라 함
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
        return null;
    }

    public LentSummaryResponseDTO lentSummary(int userId) {
        return null;
    }

//    public BorrowSummaryResponseDTO borrowSummary(int userId) {
//        // 1. 해당 유저가 채무자로 존재하는 차용증의 토큰 리스트를 조회한다.
//        List<BigInteger> tokenIdList = new ArrayList<>();
//
//        // 1-2. 만약 리스트가 비어있다면, 전체 0 데이터를 반환
//        if (tokenIdList.isEmpty()) {
//            return BorrowSummaryResponseDTO.builder()
//                    .totalOutgoingLa(0)
//                    .monthlyOutgoingLa(0)
//                    .nextOutgoingDt("-")
//                    .build();
//        }
//
//        try {
//            DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate today = LocalDate.now();
//            List<String> nextDtList = new ArrayList<>(); // 가장 가까운 날짜를 구하기 위해서
//
//            // 2. 각 tokenId에 대한 메타데이터 조회 및 합계 계산
//            BigInteger totalOutgoingLa = BigInteger.ZERO;
//            BigInteger monthlyOutgoingLa = BigInteger.ZERO;
//
//            // 3. tokenId로 각 NFT의 정보를 조회
//            for (BigInteger tokenId : tokenIdList) {
//                // NFT의 상환 정보를 호출
//                RepaymentInfoDTO repaymentInfo = repaymentSchedulerService.getRepaymentInfo(tokenId);
//
//                // 전체 대출한 금액에 더하기
//                totalOutgoingLa = totalOutgoingLa.add(repaymentInfo.getInitialPrincipal());
//
//                // 이번달 상환액 더하기
//                monthlyOutgoingLa = monthlyOutgoingLa.add(LoanCalculationUtil.calculateMonthlyPayment(repaymentInfo));
//
//                // 리스트에 날짜 담기
//                nextDtList.add(repaymentInfo.getNextPaymentDate());
//            }
//
//            // 가장 가까운 날 찾기
//            String nextOutgoingDt = nextDtList.stream()
//                    .map(dateStr -> LocalDate.parse(dateStr, FORMATTER))
//                    .filter(date -> !date.isBefore(today)) // 오늘 이후
//                    .min(Comparator.naturalOrder())        // 가장 빠른 날짜
//                    .map(FORMATTER::format)               // 다시 문자열로 변환
//                    .orElse(null);
//
//            // 4. 결과 반환
//            return BorrowSummaryResponseDTO.builder()
//                    .totalOutgoingLa(totalOutgoingLa.intValue())
//                    .monthlyOutgoingLa(monthlyOutgoingLa.intValue())
//                    .nextOutgoingDt(nextOutgoingDt)
//                    .build();
//
//        } catch (Exception e) {
////            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
//        }
//    }
//
//    public List<BorrowListResponseDTO> borrowList(int userId) {
//        // 1. 해당 유저가 채무자로 존재하는 차용증의 토큰 리스트를 조회한다. -> 차용증 객체로
//        List<BigInteger> tokenIdList = new ArrayList<>();
//
//        try {
//            // 결과 반환 리스트
//            List<BorrowListResponseDTO> list = new ArrayList<>();
//
//            // 2. tokenId로 각 NFT의 정보를 조회
//            for (BigInteger tokenId : tokenIdList) {
//                // NFT의 메타데이터 호출
//                PromissoryMetadataDTO dto = promissoryNoteService.getPromissoryMetadata(tokenId);
//
//                BorrowListResponseDTO response = BorrowListResponseDTO.builder()
//                        .contractId(1) // <-- 객체에서 가져오기
//                        .tokenId(String.valueOf(tokenId))
//                        .nftImage(dto.getNftImage())
//                        .crName(dto.getCreditorName())
//                        .crWallet(dto.getCreditorWalletAddress())
//                        .la(dto.getLoanAmount())
//                        .ir(dto.getInterestRate())
//                        .matDt(dto.getMaturityDate())
//                        .remainTerms(DateUtils.calculateRemainTerms(dto.getMaturityDate())) // 확인
//                        .pnStatus(null) // 확인
//                        .nextMpDt(null) // 확인
//                        .nextAmount(null) // 확인
//                        .aoi(null) // 확인
//                        .aoiDays(null) // 확인
//                        .build();
//
//                list.add(response);
//            }
//
//            // 4. 결과 반환
//            return list;
//        } catch (Exception e) {
////            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
//        }
//    }
//
//    public BorrowDetailResponseDTO borrowDetail(int contractId, int userId) {
//        // 1. 해당 차용증 Id에서 token Id를 가져온다.
//        BigInteger tokenId = null;
//
//        try {
//            // 2. tokenId로 NFT의 정보를 조회
//            PromissoryMetadataDTO dto = promissoryNoteService.getPromissoryMetadata(tokenId);
//
//            BorrowDetailResponseDTO response = BorrowDetailResponseDTO.builder()
//                    .contractId("1") // <-- 객체에서 가져오기
//                    .tokenId(String.valueOf(tokenId))
//                    .nftImage(dto.getNftImage())
//                    .crName(dto.getCreditorName())
//                    .crWallet(dto.getCreditorWalletAddress())
//                    .la(dto.getLoanAmount())
//                    .ir(dto.getInterestRate())
//                    .matDt(dto.getMaturityDate())
//                    .remainTerms(DateUtils.calculateRemainTerms(dto.getMaturityDate())) // 확인
//                    .pnStatus(null) // 확인
//                    .nextMpDt(null) // 확인
//                    .nextAmount(null) // 확인
//                    .aoi(null) // 확인
//                    .aoiDays(null) // 확인
//                    .earlypayFlag(null)
//                    .earlypayFee(null)
//                    .accel(null)
//                    .accelDir(null)
//                    .addTerms(null)
//                    .eventList(null) // <- 이벤트 리스트 내역 담기
//                    .build();
//
//            // 3. 결과 반환
//            return response;
//        } catch (Exception e) {
////            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
//        }
//
//    }
//
//    public LentSummaryResponseDTO lentSummary(int userId) {
//        // 1. 내가 채권자인 차용증들에서 tokenId를 리스트로 불러온다.
//        List<BigInteger> tokenIdList = new ArrayList<>();
//
//        // 1-2. 만약 리스트가 비어있다면, 전체 0 데이터를 반환
//        if (tokenIdList.isEmpty()) {
//            return LentSummaryResponseDTO.builder()
//                    .totalIncomingLa(0)
//                    .monthlyIncomingLa(0)
//                    .nextIncomingDt("-")
//                    .build();
//        }
//
//        try {
//            DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate today = LocalDate.now();
//            List<String> nextDtList = new ArrayList<>(); // 가장 가까운 날짜를 구하기 위해서
//
//            // 2. 각 tokenId에 대한 메타데이터 조회 및 합계 계산
//            BigInteger totalIncomingLa = BigInteger.ZERO;
//            BigInteger monthlyIncomingLa = BigInteger.ZERO;
//
//            // 3. tokenId로 각 NFT의 정보를 조회
//            for (BigInteger tokenId : tokenIdList) {
//                // NFT의 상환 정보를 호출
//                RepaymentInfoDTO repaymentInfo = repaymentSchedulerService.getRepaymentInfo(tokenId);
//
//                // 전체 대출한 금액에 더하기
//                totalIncomingLa = totalIncomingLa.add(repaymentInfo.getInitialPrincipal());
//
//                // 이번달 상환액 더하기
//                monthlyIncomingLa = monthlyIncomingLa.add(LoanCalculationUtil.calculateMonthlyPayment(repaymentInfo));
//
//                // 리스트에 날짜 담기
//                nextDtList.add(repaymentInfo.getNextPaymentDate());
//            }
//
//            // 가장 가까운 날 찾기
//            String nextIncomingDt = nextDtList.stream()
//                    .map(dateStr -> LocalDate.parse(dateStr, FORMATTER))
//                    .filter(date -> !date.isBefore(today)) // 오늘 이후
//                    .min(Comparator.naturalOrder())        // 가장 빠른 날짜
//                    .map(FORMATTER::format)               // 다시 문자열로 변환
//                    .orElse(null);
//
//            // 4. 결과 반환
//            return LentSummaryResponseDTO.builder()
//                    .totalIncomingLa(totalIncomingLa.intValue())
//                    .monthlyIncomingLa(monthlyIncomingLa.intValue())
//                    .nextIncomingDt("-")
//                    .build();
//
//        } catch (Exception e) {
////            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
//        }
//    }
}
