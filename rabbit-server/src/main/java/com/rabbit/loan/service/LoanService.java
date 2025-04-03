package com.rabbit.loan.service;

import com.rabbit.blockchain.domain.dto.response.PromissoryMetadataDTO;
import com.rabbit.blockchain.domain.dto.response.RepaymentInfoDTO;
import com.rabbit.blockchain.service.PromissoryNoteService;
import com.rabbit.blockchain.service.RepaymentSchedulerService;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.blockchain.util.BlockChainUtil;
import com.rabbit.loan.domain.dto.response.BorrowDetailResponseDTO;
import com.rabbit.loan.domain.dto.response.BorrowListResponseDTO;
import com.rabbit.loan.domain.dto.response.BorrowSummaryResponseDTO;
import com.rabbit.loan.util.DateUtils;
import com.rabbit.loan.domain.dto.response.BorrowListResponseDTO;
import com.rabbit.loan.domain.dto.response.BorrowSummaryResponseDTO;
import com.rabbit.loan.util.DateUtils;
import com.rabbit.loan.domain.dto.response.BorrowSummaryResponseDTO;
import com.rabbit.loan.util.LoanCalculationUtil;
import com.rabbit.user.repository.MetamaskWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hibernate.validator.internal.engine.messageinterpolation.el.RootResolver.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final MetamaskWalletRepository metamaskWalletRepository;

    private final PromissoryNoteService promissoryNoteService;
    private final RepaymentSchedulerService repaymentSchedulerService;

    public BorrowSummaryResponseDTO borrowSummary(int userId) {
        // 1. 해당 유저가 채무자로 존재하는 차용증의 토큰 리스트를 조회한다.
        List<BigInteger> tokenIdList = new ArrayList<>();

        // 1-2. 만약 리스트가 비어있다면, 전체 0 데이터를 반환
        if (tokenIdList.isEmpty()) {
            return BorrowSummaryResponseDTO.builder()
                    .totalOutgoingLa(0)
                    .monthlyOutgoingLa(0)
                    .nextOutgoingDt("-")
                    .build();
        }

        try {
            DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate today = LocalDate.now();
            List<String> nextDtList = new ArrayList<>(); // 가장 가까운 날짜를 구하기 위해서

            // 2. 각 tokenId에 대한 메타데이터 조회 및 합계 계산
            BigInteger totalOutgoingLa = BigInteger.ZERO;
            BigInteger monthlyOutgoingLa = BigInteger.ZERO;

            // 3. tokenId로 각 NFT의 정보를 조회
            for (BigInteger tokenId : tokenIdList) {
                // NFT의 상환 정보를 호출
                RepaymentInfoDTO repaymentInfo = repaymentSchedulerService.getRepaymentInfo(tokenId);

                // 전체 대출한 금액에 더하기
                totalOutgoingLa = totalOutgoingLa.add(repaymentInfo.getInitialPrincipal());

                // 이번달 상환액 더하기
                monthlyOutgoingLa = monthlyOutgoingLa.add(LoanCalculationUtil.calculateMonthlyPayment(repaymentInfo));

                // 리스트에 날짜 담기
                nextDtList.add(repaymentInfo.getNextPaymentDate());
            }

            // 가장 가까운 날 찾기
            String nextOutgoingDt = nextDtList.stream()
                    .map(dateStr -> LocalDate.parse(dateStr, FORMATTER))
                    .filter(date -> !date.isBefore(today)) // 오늘 이후
                    .min(Comparator.naturalOrder())        // 가장 빠른 날짜
                    .map(FORMATTER::format)               // 다시 문자열로 변환
                    .orElse(null);

            // 4. 결과 반환
            return BorrowSummaryResponseDTO.builder()
                    .totalOutgoingLa(totalOutgoingLa.intValue())
                    .monthlyOutgoingLa(monthlyOutgoingLa.intValue())
                    .nextOutgoingDt(nextOutgoingDt)
                    .build();

        } catch (Exception e) {
//            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }
    }

    public List<BorrowListResponseDTO> borrowList(int userId) {
        // 1. 해당 유저가 채무자로 존재하는 차용증의 토큰 리스트를 조회한다. -> 차용증 객체로
        List<BigInteger> tokenIdList = new ArrayList<>();

        try {
            // 결과 반환 리스트
            List<BorrowListResponseDTO> list = new ArrayList<>();

            // 2. tokenId로 각 NFT의 정보를 조회
            for (BigInteger tokenId : tokenIdList) {
                // NFT의 메타데이터 호출
                PromissoryMetadataDTO dto = promissoryNoteService.getPromissoryMetadata(tokenId);

                BorrowListResponseDTO response = BorrowListResponseDTO.builder()
                        .contractId(1) // <-- 객체에서 가져오기
                        .tokenId(String.valueOf(tokenId))
                        .nftImage(dto.getNftImage())
                        .crName(dto.getCreditorName())
                        .crWallet(dto.getCreditorWalletAddress())
                        .la(dto.getLoanAmount())
                        .ir(dto.getInterestRate())
                        .matDt(dto.getMaturityDate())
                        .remainTerms(DateUtils.calculateRemainTerms(dto.getMaturityDate())) // 확인
                        .pnStatus(null) // 확인
                        .nextMpDt(null) // 확인
                        .nextAmount(null) // 확인
                        .aoi(null) // 확인
                        .aoiDays(null) // 확인
                        .build();

                list.add(response);
            }

            // 4. 결과 반환
            return list;
        } catch (Exception e) {
//            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }
    }

    public BorrowDetailResponseDTO borrowDetail(int contractId, int userId) {
        // 1. 해당 차용증 Id에서 token Id를 가져온다.
        BigInteger tokenId = null;

        try {
            // 2. tokenId로 NFT의 정보를 조회
            PromissoryMetadataDTO dto = promissoryNoteService.getPromissoryMetadata(tokenId);

            BorrowDetailResponseDTO response = BorrowDetailResponseDTO.builder()
                    .contractId("1") // <-- 객체에서 가져오기
                    .tokenId(String.valueOf(tokenId))
                    .nftImage(dto.getNftImage())
                    .crName(dto.getCreditorName())
                    .crWallet(dto.getCreditorWalletAddress())
                    .la(dto.getLoanAmount())
                    .ir(dto.getInterestRate())
                    .matDt(dto.getMaturityDate())
                    .remainTerms(DateUtils.calculateRemainTerms(dto.getMaturityDate())) // 확인
                    .pnStatus(null) // 확인
                    .nextMpDt(null) // 확인
                    .nextAmount(null) // 확인
                    .aoi(null) // 확인
                    .aoiDays(null) // 확인
                    .earlypayFlag(null)
                    .earlypayFee(null)
                    .accel(null)
                    .accelDir(null)
                    .addTerms(null)
                    .eventList(null) // <- 이벤트 리스트 내역 담기
                    .build();

            // 3. 결과 반환
            return response;
        } catch (Exception e) {
//            log.error("❌ NFT 목록 조회 실패 - userId={}, wallet={}", 1, walletAddress, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "NFT 목록 조회 중 오류가 발생했습니다.");
        }

    }
}
