package com.rabbit.blockchain.service;

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
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
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
}
