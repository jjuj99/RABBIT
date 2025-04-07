package com.rabbit.blockchain.service;

import com.rabbit.blockchain.domain.dto.RepaymentInfo;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        // 2. Function 정의
        Function function = new Function(
                "getRepaymentInfo",
                Collections.singletonList(new Uint256(tokenId)),
                Collections.singletonList(new TypeReference<RepaymentScheduler.RepaymentInfo>() {})
        );

        try {
            // 3. Function 호출 인코딩
            String encodedFunction = FunctionEncoder.encode(function);

            // 4. call 실행
            String rawResponse = web3j.ethCall(
                    Transaction.createEthCallTransaction(credentials.getAddress(), contractAddress, encodedFunction),
                    DefaultBlockParameterName.LATEST
            ).send().getValue();

            // 5. 응답 디코딩
            List<Type> decoded = FunctionReturnDecoder.decode(rawResponse, function.getOutputParameters());

            // 6. 디코딩 값이 비어있다면, 에러 반환
            if (decoded.isEmpty()) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "1");
            }

            // 7. 응답 반환
            return (RepaymentScheduler.RepaymentInfo) decoded.get(0);
        } catch (Exception e) {
            log.error("[RepaymentScheduler] ERROR : ", e);
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "1");
        }
    }

    public RepaymentInfo getRepaymentInfo(BigInteger tokenId) throws Exception {
        Function function = new Function(
                "getRepaymentInfo",
                Collections.singletonList(new Uint256(tokenId)),
                Arrays.asList(
                        new TypeReference<Uint256>() {}, // tokenId
                        new TypeReference<Uint256>() {}, // initialPrincipal
                        new TypeReference<Uint256>() {}, // remainingPrincipal
                        new TypeReference<Uint256>() {}, // ir
                        new TypeReference<Uint256>() {}, // dir
                        new TypeReference<Uint256>() {}, // mpDt
                        new TypeReference<Uint256>() {}, // nextMpDt
                        new TypeReference<Uint256>() {}, // totalPayments
                        new TypeReference<Uint256>() {}, // remainingPayments
                        new TypeReference<Uint256>() {}, // fixedPaymentAmount
                        new TypeReference<Utf8String>() {}, // repayType
                        new TypeReference<Address>() {}, // drWalletAddress
                        new TypeReference<Bool>() {}, // activeFlag
                        new TypeReference<Bool>() {}, // overdueFlag
                        new TypeReference<Uint256>() {}, // overdueStartDate
                        new TypeReference<Uint256>() {}, // overdueDays
                        new TypeReference<Uint256>() {}, // aoi
                        new TypeReference<Uint256>() {}, // defCnt
                        new TypeReference<Uint256>() {}, // accel
                        new TypeReference<Uint256>() {}, // currentIr
                        new TypeReference<Uint256>() {}  // totalDefCnt
                )
        );

        String encodedFunction = FunctionEncoder.encode(function);

        String rawResponse = web3j.ethCall(
                Transaction.createEthCallTransaction(credentials.getAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
        ).send().getValue();

        List<Type> values = FunctionReturnDecoder.decode(rawResponse, function.getOutputParameters());

        if (values.isEmpty()) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "응답 없음");
        }

        return new RepaymentInfo(
                (BigInteger) values.get(0).getValue(),  // tokenId
                (BigInteger) values.get(1).getValue(),  // initialPrincipal
                (BigInteger) values.get(2).getValue(),  // remainingPrincipal
                (BigInteger) values.get(3).getValue(),  // ir
                (BigInteger) values.get(4).getValue(),  // dir
                (BigInteger) values.get(5).getValue(),  // mpDt
                (BigInteger) values.get(6).getValue(),  // nextMpDt
                (BigInteger) values.get(7).getValue(),  // totalPayments
                (BigInteger) values.get(8).getValue(),  // remainingPayments
                (BigInteger) values.get(9).getValue(),  // fixedPaymentAmount
                ((Utf8String) values.get(10)).getValue(), // repayType
                ((Address) values.get(11)).getValue(), // drWalletAddress
                ((Bool) values.get(12)).getValue(), // activeFlag
                ((Bool) values.get(13)).getValue(), // overdueFlag
                (BigInteger) values.get(14).getValue(), // overdueStartDate
                (BigInteger) values.get(15).getValue(), // overdueDays
                (BigInteger) values.get(16).getValue(), // aoi
                (BigInteger) values.get(17).getValue(), // defCnt
                (BigInteger) values.get(18).getValue(), // accel
                (BigInteger) values.get(19).getValue(), // currentIr
                (BigInteger) values.get(20).getValue()  // totalDefCnt
        );
    }
}
