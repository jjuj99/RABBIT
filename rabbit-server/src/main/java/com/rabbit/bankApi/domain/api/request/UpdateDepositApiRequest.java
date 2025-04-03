package com.rabbit.bankApi.domain.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rabbit.bankApi.domain.api.Header.ApiRequestHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepositApiRequest {

    @JsonProperty("Header")
    private ApiRequestHeader header;
    private String userKey;
    private String accountNo;
    private String transactionBalance;
    private String transactionSummary;
}
