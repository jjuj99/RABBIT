package com.rabbit.blockchain.service;

import com.rabbit.blockchain.domain.dto.response.PromissoryMetadataDTO;
import com.rabbit.blockchain.util.BlockChainUtil;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.loan.domain.dto.response.BorrowSummaryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromissoryNoteService {

    @Value("${blockchain.promissoryNote.address}")
    private String contractAddress;

    private final BlockChainUtil blockChainUtil;

    /**
     * 토큰 ID로 차용증 NFT 메타데이터를 조회합니다.
     *
     * @param tokenId 조회할 토큰 ID
     * @return 차용증 메타데이터 DTO
     */
    public PromissoryMetadataDTO getPromissoryMetadata(BigInteger tokenId) {
        try {
            log.info("토큰 ID {} 메타데이터 조회 시작", tokenId);

            // 1. 호출할 스마트 컨트랙트 함수 정의
            Function function = new Function(
                    "getPromissoryMetadata",
                    List.of(new Uint256(tokenId)),
                    List.of(
                            // PromissoryMetadata 구조체에 대한 타입 참조
                            new TypeReference<Utf8String>() {},      // nftImage

                            // CrInfo 구조체
                            new TypeReference<Utf8String>() {},      // crSign
                            new TypeReference<Utf8String>() {},      // crName
                            new TypeReference<Address>() {},         // crWalletAddress
                            new TypeReference<Utf8String>() {},      // crInfoHash

                            // DrInfo 구조체
                            new TypeReference<Utf8String>() {},      // drSign
                            new TypeReference<Utf8String>() {},      // drName
                            new TypeReference<Address>() {},         // drWalletAddress
                            new TypeReference<Utf8String>() {},      // drInfoHash

                            // 대출 정보
                            new TypeReference<Uint256>() {},         // la (차용 금액)
                            new TypeReference<Uint256>() {},         // ir (이자율)
                            new TypeReference<Uint256>() {},         // lt (대출 기간)
                            new TypeReference<Utf8String>() {},      // repayType (상환 방식)
                            new TypeReference<Utf8String>() {},      // matDt (만기일)
                            new TypeReference<Uint256>() {},         // mpDt (월 납부일)
                            new TypeReference<Uint256>() {},         // dir (연체 이자율)
                            new TypeReference<Utf8String>() {},      // contractDate (계약일)
                            new TypeReference<Bool>() {},            // earlyPayFlag (중도상환 가능 여부)
                            new TypeReference<Uint256>() {},         // earlyPayFee (중도상환 수수료)
                            new TypeReference<Uint256>() {},         // accel (기한이익상실 횟수)

                            // AddTerms 구조체
                            new TypeReference<Utf8String>() {},      // addTerms
                            new TypeReference<Utf8String>() {}       // addTermsHash
                    )
            );

            // 2. BlockChainUtil을 이용해 함수 호출
            List<Type> output = blockChainUtil.callFunction(null, contractAddress, function);

            // 3. 결과가 없는 경우
            if (output == null || output.isEmpty()) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "토큰 ID에 해당하는 차용증이 없습니다: " + tokenId);
            }

            // 4. 결과 매핑
            return mapToPromissoryMetadataDTO(tokenId, output);

        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            log.error("getPromissoryMetadata 실패: 토큰 ID = " + tokenId, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "차용증 메타데이터 조회 실패");
        }
    }

    /**
     * 블록체인 응답을 DTO로 매핑합니다.
     */
    private PromissoryMetadataDTO mapToPromissoryMetadataDTO(BigInteger tokenId, List<Type> output) {
        int idx = 0;

        String nftImage = ((Utf8String) output.get(idx++)).getValue();

        // CrInfo (채권자 정보)
        String crSign = ((Utf8String) output.get(idx++)).getValue();
        String crName = ((Utf8String) output.get(idx++)).getValue();
        String crWalletAddress = ((Address) output.get(idx++)).getValue();
        String crInfoHash = ((Utf8String) output.get(idx++)).getValue();

        // DrInfo (채무자 정보)
        String drSign = ((Utf8String) output.get(idx++)).getValue();
        String drName = ((Utf8String) output.get(idx++)).getValue();
        String drWalletAddress = ((Address) output.get(idx++)).getValue();
        String drInfoHash = ((Utf8String) output.get(idx++)).getValue();

        // 대출 정보
        BigInteger loanAmount = ((Uint256) output.get(idx++)).getValue();
        BigInteger interestRate = ((Uint256) output.get(idx++)).getValue();
        BigInteger loanTerm = ((Uint256) output.get(idx++)).getValue();
        String repaymentType = ((Utf8String) output.get(idx++)).getValue();
        String maturityDate = ((Utf8String) output.get(idx++)).getValue();
        BigInteger monthlyPaymentDay = ((Uint256) output.get(idx++)).getValue();
        BigInteger defaultInterestRate = ((Uint256) output.get(idx++)).getValue();
        String contractDate = ((Utf8String) output.get(idx++)).getValue();
        Boolean earlyPaymentFlag = ((Bool) output.get(idx++)).getValue();
        BigInteger earlyPaymentFee = ((Uint256) output.get(idx++)).getValue();
        BigInteger acceleration = ((Uint256) output.get(idx++)).getValue();

        // AddTerms
        String additionalTerms = ((Utf8String) output.get(idx++)).getValue();
        String additionalTermsHash = ((Utf8String) output.get(idx)).getValue();

        // DTO로 변환하여 반환
        return PromissoryMetadataDTO.builder()
                .tokenId(tokenId.longValue())
                .nftImage(nftImage)
                .creditorName(crName)
                .creditorWalletAddress(crWalletAddress)
                .debtorName(drName)
                .debtorWalletAddress(drWalletAddress)
                .loanAmount(loanAmount)
                .interestRate(interestRate.doubleValue() / 100.0) // 백분율로 변환 (예: 500 -> 5.0%)
                .loanTerm(loanTerm.intValue())
                .repaymentType(repaymentType)
                .maturityDate(maturityDate)
                .monthlyPaymentDay(monthlyPaymentDay.intValue())
                .defaultInterestRate(defaultInterestRate.doubleValue() / 100.0)
                .contractDate(contractDate)
                .earlyPaymentAllowed(earlyPaymentFlag)
                .earlyPaymentFee(earlyPaymentFee.doubleValue() / 100.0)
                .build();
    }


}
