package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Schema(description = "채권 상세 정보 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LentDetailResponseDTO {

    private int contractId;
    private BigInteger tokenId; // 토큰 아이디
    private String nftImage; // 토큰 이미지
    private String drName; // 채권자 이름
    private String drWallet; // 채권자 지갑
    private Long la; // 총 대출 금액
    private Long totalAmount; // 만기수취액
    private String repayType; // 상환 방식
    private double ir; // 이자율
    private double dir; // 연체 이자율
    private int defCnt; // 지금까지의 연체 횟수
    private String contractDt; // 계약일
    private String matDt; // 만기일
    private int remainTerms; // 만기까지 남은 일수
    private Double progressRate; // 진행률
    private String pnStatus; // 연체 상태
    private String nextMpDt; // 다음 납부일
    private Long nextAmount; // 다음 상환 금액
    private Long aoi; // 연체 금액
    private int aoiDays; // 연체 일수
    private boolean earlypayFlag; // 중도 상환 가능 여부
    private double earlypayFee; // 중도 상환 수수료
    private int accel; // 기한이익상실
    private double accelDir; // 기한이익상실 연체이자율
    private String addTerms; // 추가 약정 사항
    private String addTermsHash; // 차용증 계약서 pdf, nftPdfUrl
//    private List<ContractEventDTO> eventList; // 이벤트 내역
}
