package com.rabbit.promissorynote.domain.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 계약 정보를 담는 DTO
 */
@Getter
@Builder
public class UserContractInfoDto {
    private final String creditorWalletAddress;
    private final String debtorWalletAddress;
    private final String creditorSign;
    private final String debtorSign;
    private final String creditorInfoHash;
    private final String debtorInfoHash;

    // 빌더 패턴 사용으로 모든 필드를 초기화하는 생성자는 자동 생성됨
}