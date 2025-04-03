package com.rabbit.blockchain.service;

import com.rabbit.blockchain.domain.dto.response.RepaymentInfoDTO;
import com.rabbit.blockchain.util.BlockChainUtil;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentSchedulerService {

    @Value("${blockchain.repaymentScheduler.address}")
    private String contractAddress;

    private final BlockChainUtil blockChainUtil;

    /**
     * 특정 토큰 ID의 상환 정보를 조회합니다.
     *
     * @param tokenId 조회할 토큰 ID
     * @return 상환 정보 DTO
     */
    public RepaymentInfoDTO getRepaymentInfo(BigInteger tokenId) {
        try {
            log.info("토큰 ID {} 상환 정보 조회 시작", tokenId);

            // 1. 호출할 스마트 컨트랙트 함수 정의
            Function function = new Function(
                    "getRepaymentInfo",
                    List.of(new Uint256(tokenId)),
                    List.of(
                            new TypeReference<Uint256>() {},      // tokenId
                            new TypeReference<Uint256>() {},      // initialPrincipal
                            new TypeReference<Uint256>() {},      // remainingPrincipal
                            new TypeReference<Uint256>() {},      // ir (이자율)
                            new TypeReference<Uint256>() {},      // dir (연체 이자율)
                            new TypeReference<Uint256>() {},      // mpDt (월 납부일)
                            new TypeReference<Uint256>() {},      // nextMpDt (다음 납부일)
                            new TypeReference<Uint256>() {},      // totalPayments
                            new TypeReference<Uint256>() {},      // remainingPayments
                            new TypeReference<Uint256>() {},      // fixedPaymentAmount
                            new TypeReference<Uint256>() {},      // repayType
                            new TypeReference<Address>() {},      // drWalletAddress
                            new TypeReference<Bool>() {},         // activeFlag
                            new TypeReference<Bool>() {},         // overdueFlag
                            new TypeReference<Uint256>() {},      // overdueStartDate
                            new TypeReference<Uint256>() {},      // overdueDays
                            new TypeReference<Uint256>() {},      // aoi (누적 연체 이자)
                            new TypeReference<Uint256>() {},      // defCnt (현재 연체 횟수)
                            new TypeReference<Uint256>() {},      // accel (기한이익상실 횟수)
                            new TypeReference<Uint256>() {},      // currentIr (현재 적용 이자율)
                            new TypeReference<Uint256>() {}       // totalDefCnt (총 누적 연체 횟수)
                    )
            );

            // 2. BlockChainUtil을 이용해 함수 호출
            List<Type> output = blockChainUtil.callFunction(null, contractAddress, function);

            // 3. 결과가 없는 경우
            if (output == null || output.isEmpty()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "토큰 ID에 해당하는 상환 일정이 없습니다: " + tokenId);
            }

            // 4. 결과 매핑
            return mapToRepaymentInfoDTO(output);

        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("getRepaymentInfo 실패: 토큰 ID = " + tokenId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "상환 정보 조회 실패");
        }
    }

    /**
     * 블록체인 응답을 DTO로 매핑합니다.
     */
    private RepaymentInfoDTO mapToRepaymentInfoDTO(List<Type> output) {
        int idx = 0;

        BigInteger tokenId = ((Uint256) output.get(idx++)).getValue();
        BigInteger initialPrincipal = ((Uint256) output.get(idx++)).getValue();
        BigInteger remainingPrincipal = ((Uint256) output.get(idx++)).getValue();
        BigInteger interestRate = ((Uint256) output.get(idx++)).getValue();
        BigInteger defaultInterestRate = ((Uint256) output.get(idx++)).getValue();
        BigInteger monthlyPaymentDay = ((Uint256) output.get(idx++)).getValue();
        BigInteger nextPaymentTimestamp = ((Uint256) output.get(idx++)).getValue();
        BigInteger totalPayments = ((Uint256) output.get(idx++)).getValue();
        BigInteger remainingPayments = ((Uint256) output.get(idx++)).getValue();
        BigInteger fixedPaymentAmount = ((Uint256) output.get(idx++)).getValue();
        BigInteger repayType = ((Uint256) output.get(idx++)).getValue();
        String debtorWalletAddress = ((Address) output.get(idx++)).getValue();
        Boolean activeFlag = ((Bool) output.get(idx++)).getValue();
        Boolean overdueFlag = ((Bool) output.get(idx++)).getValue();

        // 나머지 필드는 건너뜀

        // 다음 납부일 변환 (Unix 타임스탬프 -> YYYY-MM-DD)
        String nextPaymentDate = convertTimestampToDateString(nextPaymentTimestamp);

        // 상환 방식 변환
        String repaymentType;
        if (repayType.equals(BigInteger.ZERO)) {
            repaymentType = "EPIP"; // 원리금균등상환
        } else if (repayType.equals(BigInteger.ONE)) {
            repaymentType = "EPP";  // 원금균등상환
        } else {
            repaymentType = "BP";   // 만기일시상환
        }

        // DTO로 변환
        return RepaymentInfoDTO.builder()
                .tokenId(tokenId)
                .initialPrincipal(initialPrincipal)
                .remainingPrincipal(remainingPrincipal)
                .interestRate(interestRate)
                .defaultInterestRate(defaultInterestRate)
                .nextPaymentDate(nextPaymentDate)
                .totalPayments(totalPayments.intValue())
                .remainingPayments(remainingPayments.intValue())
                .fixedPaymentAmount(fixedPaymentAmount)
                .repaymentType(repaymentType)
                .overdueFlag(overdueFlag)
                .build();
    }

    /**
     * Unix 타임스탬프를 YYYY-MM-DD 형식의 문자열로 변환합니다.
     */
    private String convertTimestampToDateString(BigInteger timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp.longValue()),
                ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE); // YYYY-MM-DD
    }

    /**
     * 활성화된 모든 상환 일정의 토큰 ID 목록을 조회합니다.
     */
    public List<BigInteger> getActiveRepayments() {
        try {
            log.info("활성화된 상환 일정 목록 조회 시작");

            // 1. 호출할 스마트 컨트랙트 함수 정의
            Function function = new Function(
                    "getActiveRepayments",
                    Collections.emptyList(), // 파라미터 없음
                    List.of(new TypeReference<DynamicArray<Uint256>>() {}) // 반환 타입: 토큰 ID 배열
            );

            // 2. BlockChainUtil을 이용해 함수 호출
            List<Type> output = blockChainUtil.callFunction(null, contractAddress, function);

            // 3. 결과가 없는 경우
            if (output == null || output.isEmpty()) {
                return Collections.emptyList();
            }

            // 4. 결과 디코딩 (리턴값 추출)
            @SuppressWarnings("unchecked")
            List<Uint256> tokenIdList = (List<Uint256>) output.get(0).getValue();

            // 5. tokenId 값만 추출 (BigInteger로)
            return tokenIdList.stream()
                    .map(Uint256::getValue)
                    .toList();

        } catch (Exception e) {
            log.error("getActiveRepayments 실패", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성화된 상환 일정 목록 조회 실패");
        }
    }
}
