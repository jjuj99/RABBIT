package com.rabbit.bankApi.domain.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAccountApiResponse {

    private String status;
    private String transactionUniqueNo;
    private String accountNo;
}
