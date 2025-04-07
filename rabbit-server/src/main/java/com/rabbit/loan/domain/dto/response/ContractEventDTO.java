package com.rabbit.loan.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractEventDTO {

    private String eventType;
    private Long intAmt;
    private String from;
    private String to;
    private String timestamp;
}
