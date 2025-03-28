package com.rabbit.coin.domain.dto.request;

import lombok.Data;

@Data
public class TossConfirmRequestDTO {
    private String paymentKey;
    private String orderId;
    private Long amount;
}
