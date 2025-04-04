package com.rabbit.contract.domain.dto.request;


import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.user.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 차용증 계약 요청 DTO
 */
@Schema(description = "차용증 계약 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequestDTO {

    @Schema(description = "원본 계약 ID (수정 요청인 경우에만 사용)", example = "1")
    private Integer contractId;

    // 채무자 정보
    @Schema(description = "채무자 휴대폰 번호", example = "01012345678", required = true)
    @NotBlank(message = "휴대폰 번호를 입력해주세요")
    @Pattern(regexp = "^\\d{10,11}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String drPhone;

    @Schema(description = "채무자 이름", example = "홍길동", required = true)
    @NotBlank(message = "채무자 이름을 입력해주세요")
    @Size(max = 50, message = "이름은 50자 이내로 입력해주세요")
    private String drName;

    @Schema(description = "채무자 지갑 정보", example = "0x1234567890abcdef", required = true)
    @NotBlank(message = "채무자 지갑 정보를 입력해주세요")
    private String drWallet;

    // 채권자 정보
    @Schema(description = "채권자 이메일", example = "creditor@example.com", required = true)
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String crEmail;

    @Schema(description = "채권자 이름", example = "김채권", required = true)
    @NotBlank(message = "채권자 이름을 입력해주세요")
    @Size(max = 50, message = "이름은 50자 이내로 입력해주세요")
    private String crName;

    @Schema(description = "채권자 지갑 정보", example = "0xabcdef1234567890", required = true)
    @NotBlank(message = "채권자 지갑 정보를 입력해주세요")
    private String crWallet;

    // 기본 정보
    @Schema(description = "대출 금액 (RAB)", example = "1000000", required = true)
    @NotNull(message = "대출 금액은 필수입니다")
    @DecimalMin(value = "100000", message = "대출 금액은 100,000 RAB 이상이어야 합니다")
    private BigDecimal la;

    @Schema(description = "연 이자율 (%)", example = "5.0", required = true)
    @NotNull(message = "이자율은 필수입니다")
    @DecimalMin(value = "0.0", message = "이자율은 0 이상이어야 합니다")
    @DecimalMax(value = "20.0", message = "이자율은 20% 이하여야 합니다 (법정 최고)")
    private BigDecimal ir;

    @Schema(description = "대출 기간 (개월)", example = "12", required = true)
    @NotNull(message = "대출 기간은 필수입니다")
    @Positive(message = "대출 기간은 양수여야 합니다")
    private Integer lt;

    @Schema(description = "상환방식", example = "EPIP", required = true, allowableValues = {"EPIP", "EPP", "BP"})
    @NotBlank(message = "상환방식을 선택해주세요")
    private String repayType; // EPIP: 원리금 균등 상환, EPP: 원금 균등 상환, BP: 만기 일시 상환

    @Schema(description = "상환일", example = "15", required = true)
    @NotNull(message = "상환일은 필수입니다")
    @Min(value = 1, message = "상환일은 1일 이상이어야 합니다")
    @Max(value = 31, message = "상환일은 31일 이하여야 합니다")
    private Integer mpDt;

    @Schema(description = "연체 이자율 (%)", example = "15.0", required = true)
    @NotNull(message = "연체 이자율은 필수입니다")
    @DecimalMin(value = "0.0", message = "연체 이자율은 0 이상이어야 합니다")
    @DecimalMax(value = "20.0", message = "연체 이자율은 20% 이하여야 합니다 (법정 최고)")
    private BigDecimal dir;

    // 대출 시작일과 만기일
    @Schema(description = "대출 시작일", example = "2025-04-01", required = true)
    @NotNull(message = "대출 시작일은 필수입니다")
    @Future(message = "대출 시작일은 현재 시점 이후여야 합니다")
    private ZonedDateTime contractDt;

    // 기한이익상실
    @Schema(description = "연체 횟수 기준", example = "3", required = true)
    @NotNull(message = "연체 횟수는 필수입니다")
    @PositiveOrZero(message = "연체 횟수는 0 이상이어야 합니다")
    private Integer defCnt;

    // Pass 인증
    @Schema(description = "패스 인증 토큰", example = "token123")
    private String passAuthToken;

    @Schema(description = "트랜잭션 ID", example = "tx_abc123")
    private String txId;

    @Schema(description = "인증 결과 코드", example = "SUCCESS")
    private String authResultCode;

    // 기존 DTO에서 가져온 필드 - 현재 폼에 없음
    /*
    @Schema(description = "납부 유예 일수", example = "7", required = true)
    @NotNull(message = "납부 유예 일수는 필수입니다")
    @Min(value = 0, message = "납부 유예 일수는 0 이상이어야 합니다")
    private Integer graceLineDays;
    */

    // 선택항목
    @Schema(description = "차용증 양도 가능 여부", example = "true")
    private Boolean pnTransFlag;

    @Schema(description = "중도 상환 가능 여부", example = "true")
    private Boolean earlypay;

    @Schema(description = "중도상환 이자율 (%)", example = "2.0")
    @DecimalMin(value = "0.0", message = "중도상환 이자율은 0 이상이어야 합니다")
    private BigDecimal earlypayFee;

    // 추가항목
    @Schema(description = "추가조항", example = "계약에 관한 추가 조항...")
    private String addTerms;

    @Schema(description = "채권자에게 전달할 메시지", example = "잘 부탁드립니다.")
    private String message;

    // 현재 폼에는 추가조항(addTerms)으로 존재
    /*
    @Schema(description = "계약 조항", example = "이 계약의 조항은...", required = true)
    @NotBlank(message = "계약 조항은 필수입니다")
    private String contractTerms;
    */

    /**
     * 대출 시작일과 대출 기간을 기반으로 만기일 계산
     * @return 만기일 (ZonedDateTime)
     */
    public ZonedDateTime calculateMaturityDate() {
        // 윤년 고려하여 정확한 날짜 계산
        return contractDt.plusMonths(lt).minusDays(1);
    }
}