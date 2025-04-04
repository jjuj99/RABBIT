package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentSchedulerService {

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${blockchain.repaymentScheduler.address}")
    private String contractAddress;

    public RepaymentScheduler.RepaymentInfo getPaymentInfo(BigInteger tokenId) throws Exception {
        // 1. 스마트 컨트랙트 로드
        RepaymentScheduler contract = RepaymentScheduler.load(
                contractAddress,          // 컨트랙트 주소
                web3j,                    // web3j 인스턴스
                credentials,              // 서비스 지갑 (서명용)
                new DefaultGasProvider()  // 기본 가스 설정
        );

        // 2. 함수 호출 및 결과 반환
        RepaymentScheduler.RepaymentInfo info = contract.repaymentSchedules(tokenId).send();

        // 3. 로깅 (선택)
        log.info("조회된 RepaymentInfo: tokenId={}, remainingPrincipal={}, nextMpDt={}",
                info.tokenId,
                info.remainingPrincipal,
                info.nextMpDt);

        return info;
    }




//
//
//    public RepaymentInfoDTO getRepaymentInfo(BigInteger tokenId) {
//        try {
//            log.info("토큰 ID {} 상환 정보 조회 시작", tokenId);
//
//            // 1. 호출할 스마트 컨트랙트 함수 정의
//            Function function = new Function(
//                    "getRepaymentInfo",
//                    List.of(new Uint256(tokenId)),
//                    List.of(
//                            new TypeReference<Uint256>() {},      // tokenId
//                            new TypeReference<Uint256>() {},      // initialPrincipal
//                            new TypeReference<Uint256>() {},      // remainingPrincipal
//                            new TypeReference<Uint256>() {},      // ir (이자율)
//                            new TypeReference<Uint256>() {},      // dir (연체 이자율)
//                            new TypeReference<Uint256>() {},      // mpDt (월 납부일)
//                            new TypeReference<Uint256>() {},      // nextMpDt (다음 납부일)
//                            new TypeReference<Uint256>() {},      // totalPayments
//                            new TypeReference<Uint256>() {},      // remainingPayments
//                            new TypeReference<Uint256>() {},      // fixedPaymentAmount
//                            new TypeReference<Uint256>() {},      // repayType
//                            new TypeReference<Address>() {},      // drWalletAddress
//                            new TypeReference<Bool>() {},         // activeFlag
//                            new TypeReference<Bool>() {},         // overdueFlag
//                            new TypeReference<Uint256>() {},      // overdueStartDate
//                            new TypeReference<Uint256>() {},      // overdueDays
//                            new TypeReference<Uint256>() {},      // aoi (누적 연체 이자)
//                            new TypeReference<Uint256>() {},      // defCnt (현재 연체 횟수)
//                            new TypeReference<Uint256>() {},      // accel (기한이익상실 횟수)
//                            new TypeReference<Uint256>() {},      // currentIr (현재 적용 이자율)
//                            new TypeReference<Uint256>() {}       // totalDefCnt (총 누적 연체 횟수)
//                    )
//            );
//
//            // 2. BlockChainUtil을 이용해 함수 호출
//            List<Type> output = blockChainUtil.callFunction(null, contractAddress, function);
//
//            // 3. 결과가 없는 경우
//            if (output == null || output.isEmpty()) {
//                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "토큰 ID에 해당하는 상환 일정이 없습니다: " + tokenId);
//            }
//
//            // 4. 결과 매핑
//            return mapToRepaymentInfoDTO(output);
//
//        } catch (BusinessException e) {
//            // 비즈니스 예외는 그대로 전파
//            throw e;
//        } catch (Exception e) {
//            log.error("getRepaymentInfo 실패: 토큰 ID = " + tokenId, e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "상환 정보 조회 실패");
//        }
//    }
//
//    /**
//     * 블록체인 응답을 DTO로 매핑합니다.
//     */
//    private RepaymentInfoDTO mapToRepaymentInfoDTO(List<Type> output) {
//        int idx = 0;
//
//        BigInteger tokenId = ((Uint256) output.get(idx++)).getValue();
//        BigInteger initialPrincipal = ((Uint256) output.get(idx++)).getValue();
//        BigInteger remainingPrincipal = ((Uint256) output.get(idx++)).getValue();
//        BigInteger interestRate = ((Uint256) output.get(idx++)).getValue();
//        BigInteger defaultInterestRate = ((Uint256) output.get(idx++)).getValue();
//        BigInteger monthlyPaymentDay = ((Uint256) output.get(idx++)).getValue();
//        BigInteger nextPaymentTimestamp = ((Uint256) output.get(idx++)).getValue();
//        BigInteger totalPayments = ((Uint256) output.get(idx++)).getValue();
//        BigInteger remainingPayments = ((Uint256) output.get(idx++)).getValue();
//        BigInteger fixedPaymentAmount = ((Uint256) output.get(idx++)).getValue();
//        BigInteger repayType = ((Uint256) output.get(idx++)).getValue();
//        String debtorWalletAddress = ((Address) output.get(idx++)).getValue();
//        Boolean activeFlag = ((Bool) output.get(idx++)).getValue();
//        Boolean overdueFlag = ((Bool) output.get(idx++)).getValue();
//
//        // 나머지 필드는 건너뜀
//
//        // 다음 납부일 변환 (Unix 타임스탬프 -> YYYY-MM-DD)
//        String nextPaymentDate = convertTimestampToDateString(nextPaymentTimestamp);
//
//        // 상환 방식 변환
//        String repaymentType;
//        if (repayType.equals(BigInteger.ZERO)) {
//            repaymentType = "EPIP"; // 원리금균등상환
//        } else if (repayType.equals(BigInteger.ONE)) {
//            repaymentType = "EPP";  // 원금균등상환
//        } else {
//            repaymentType = "BP";   // 만기일시상환
//        }
//
//        // DTO로 변환
//        return RepaymentInfoDTO.builder()
//                .tokenId(tokenId)
//                .initialPrincipal(initialPrincipal)
//                .remainingPrincipal(remainingPrincipal)
//                .interestRate(interestRate)
//                .defaultInterestRate(defaultInterestRate)
//                .nextPaymentDate(nextPaymentDate)
//                .totalPayments(totalPayments.intValue())
//                .remainingPayments(remainingPayments.intValue())
//                .fixedPaymentAmount(fixedPaymentAmount)
//                .repaymentType(repaymentType)
//                .overdueFlag(overdueFlag)
//                .build();
//    }
//
//    /**
//     * Unix 타임스탬프를 YYYY-MM-DD 형식의 문자열로 변환합니다.
//     */
//    private String convertTimestampToDateString(BigInteger timestamp) {
//        LocalDateTime dateTime = LocalDateTime.ofInstant(
//                Instant.ofEpochSecond(timestamp.longValue()),
//                ZoneId.systemDefault()
//        );
//        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE); // YYYY-MM-DD
//    }
//
//    /**
//     * 활성화된 모든 상환 일정의 토큰 ID 목록을 조회합니다.
//     */
//    public List<BigInteger> getActiveRepayments() {
//        try {
//            log.info("활성화된 상환 일정 목록 조회 시작");
//
//            // 1. 호출할 스마트 컨트랙트 함수 정의
//            Function function = new Function(
//                    "getActiveRepayments",
//                    Collections.emptyList(), // 파라미터 없음
//                    List.of(new TypeReference<DynamicArray<Uint256>>() {}) // 반환 타입: 토큰 ID 배열
//            );
//
//            // 2. BlockChainUtil을 이용해 함수 호출
//            List<Type> output = blockChainUtil.callFunction(null, contractAddress, function);
//
//            // 3. 결과가 없는 경우
//            if (output == null || output.isEmpty()) {
//                return Collections.emptyList();
//            }
//
//            // 4. 결과 디코딩 (리턴값 추출)
//            @SuppressWarnings("unchecked")
//            List<Uint256> tokenIdList = (List<Uint256>) output.get(0).getValue();
//
//            // 5. tokenId 값만 추출 (BigInteger로)
//            return tokenIdList.stream()
//                    .map(Uint256::getValue)
//                    .toList();
//
//        } catch (Exception e) {
//            log.error("getActiveRepayments 실패", e);
//            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성화된 상환 일정 목록 조회 실패");
//        }
//    }
}
