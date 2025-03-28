package com.rabbit.coin.domain.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TossWebhookDTO {
    private String createdAt;
    private String eventType;
    private TossWebhookDataDTO data;
}
