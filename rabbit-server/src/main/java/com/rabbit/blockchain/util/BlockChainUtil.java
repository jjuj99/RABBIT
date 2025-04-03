package com.rabbit.blockchain.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BlockChainUtil {

    private final Web3j web3j;
    private final Credentials credentials;

    /**
     * 스마트 컨트랙트의 읽기 함수(view, pure)를 호출하는 유틸
     *
     * @param fromAddress      호출자 주소 (eth_call 요청 시 사용, null 허용)
     * @param contractAddress 컨트랙트 주소
     * @param function        호출할 함수 정의 (Function)
     * @return 함수 실행 결과 (List<Type>)
     */
    public List<Type> callFunction(String fromAddress, String contractAddress, Function function) throws Exception {
        // 1. 함수 인코딩 (파라미터, 시그니처 등)
        String encodedFunction = FunctionEncoder.encode(function);

        // 2. 호출자 주소 지정 (null인 경우 기본 credentials 주소 사용)
        String from = (fromAddress != null) ? fromAddress : credentials.getAddress();

        // 2. Web3j를 통한 eth_call 요청
        EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                        from,               // 호출자 주소
                        contractAddress,    // 스마트 컨트랙트 주소
                        encodedFunction     // 바이트 인코딩된 함수
                ),
                DefaultBlockParameterName.LATEST
        ).send();

        // 3. 결과 디코딩
        return FunctionReturnDecoder.decode(
                response.getResult(),
                function.getOutputParameters()
        );
    }
}
