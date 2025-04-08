package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.PromissoryNote;
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
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromissoryNoteService {

    private final Web3j web3j;
    private final Credentials credentials;

    @Value("${blockchain.promissoryNote.address}")
    private String contractAddress;

//    @Value("${blockchain.gas.price:50000000000}")
//    private BigInteger gasPrice; // 기본값 50 Gwei
//
//    @Value("${blockchain.gas.limit:4500000}")
//    private BigInteger gasLimit; // 기본값 4,500,000

    /**
     * 차용증 NFT 발행 함수
     */
    public BigInteger mintPromissoryNote(PromissoryNote.PromissoryMetadata metadata, String recipient) throws Exception {
        log.info("차용증 NFT 발행 시작 - 수신자: {}", recipient);

        // 가스 설정
//        ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

        // 컨트랙트 로드
        PromissoryNote contract = PromissoryNote.load(
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()       // 가스 설정 (기본값 사용)
        );

        try {
            // 민팅 트랜잭션 전송
            log.info("민팅 트랜잭션 전송 - 수신자: {}, 주소: {}", recipient, contractAddress);
            TransactionReceipt receipt = contract.mint(metadata, recipient).send();

            // 트랜잭션 성공 확인
            if (!receipt.isStatusOK()) {
                log.error("민팅 트랜잭션 실패 - 상태: {}", receipt.getStatus());
                throw new RuntimeException("NFT 발행 트랜잭션이 실패했습니다.");
            }

            // 토큰 ID 추출
            List<PromissoryNote.PromissoryNoteMintedEventResponse> events =
                    PromissoryNote.getPromissoryNoteMintedEvents(receipt);

            if (events.isEmpty()) {
                log.error("민팅 이벤트를 찾을 수 없음");
                throw new RuntimeException("NFT 발행 이벤트를 찾을 수 없습니다.");
            }

            BigInteger tokenId = events.get(0).tokenId;
            log.info("차용증 NFT 발행 성공 - 토큰 ID: {}, 트랜잭션: {}",
                    tokenId, receipt.getTransactionHash());

            return tokenId;
        } catch (Exception e) {
            log.error("민팅 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("NFT 발행 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    public PromissoryNote.PromissoryMetadata getPromissoryMetadata(BigInteger tokenId) throws Exception {
        // 1. 스마트 컨트랙트 로드
        PromissoryNote contract = PromissoryNote.load(
                contractAddress,               // 컨트랙트 주소
                web3j,                         // web3j 인스턴스
                credentials,                   // 서비스 지갑 (서명용)
                new DefaultGasProvider()       // 가스 설정 (기본값 사용)
        );

        // 2. Function 정의
        Function function = new Function(
                "getPromissoryMetadata",
                Collections.singletonList(new Uint256(tokenId)),
                Collections.singletonList(new TypeReference<PromissoryNote.PromissoryMetadata>() {})
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
                log.error("[PromissoryNote] ERROR : response decoed is Emtpy");
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT 조회 중 오류가 발생했습니다.");
            }

            // 7. 응답 반환
            return (PromissoryNote.PromissoryMetadata) decoded.get(0);
        } catch (Exception e) {
            log.error("[PromissoryNote] ERROR : {}", e.getMessage());
            throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "NFT 조회 중 오류가 발생했습니다.");
        }
    }
}
