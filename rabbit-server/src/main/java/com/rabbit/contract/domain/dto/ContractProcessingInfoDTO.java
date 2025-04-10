package com.rabbit.contract.domain.dto;

import com.rabbit.promissorynote.domain.dto.UserContractInfoDto;
import lombok.Builder;
import lombok.Getter;

/**
 * 계약 처리 준비 정보 DTO
 * 트랜잭션 외부에서 준비한 정보를 트랜잭션 내부로 전달할 때 사용
 */
@Getter
@Builder
public class ContractProcessingInfoDTO {
    private final UserContractInfoDto userInfo;
    private final String pdfUrl;
    private final String imgUrl;

    // 롬복의 @Builder로 생성자 자동 생성
}