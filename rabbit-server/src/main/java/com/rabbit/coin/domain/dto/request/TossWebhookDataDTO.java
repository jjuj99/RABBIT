package com.rabbit.coin.domain.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TossWebhookDataDTO {
    private String status;          // 결제 상태: DONE, CANCELED 등
    private String orderId;         // 상점에서 설정한 주문 ID
    private String paymentKey;      // 결제 키 (확정에 사용)
    private String lastTransactionKey; // 마지막 거래 키 (환불 등에서 사용)
    private String method;          // 결제 수단 (예: ACCOUNT_TRANSFER)
    private String requestedAt;     // 결제 요청 시간
    private String approvedAt;      // 결제 승인 시간
    private Integer totalAmount;    // 결제된 총 금액
    private String currency;        // 통화 (예: KRW)
}
