package com.rabbit.loan.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Schema(description = "채권 목록 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LentListResponseDTO {

    private int contractId;
    private BigInteger tokenId; // 토큰 아이디
    private String nftImage; // 토큰 이미지
    private String drName; // 채권자 이름
    private String drWallet; // 채권자 지갑 주소
    private Long la; // 총 대출 금액
    private Double ir; // 이자율
    private String matDt; // 만기일
    private int remainTerms; // 만기까지 남은 일수
    private String pnStatus; // 상태
    private String nextMpDt; // 다음 납부일
    private Long nextAmount; // 다음 상환 금액
    private Long aoi; // 연체 금액
    private int aoiDays;// 연체 일수
}
