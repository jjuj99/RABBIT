package com.rabbit.bankApi.domain.api.response;

import com.rabbit.bankApi.domain.api.response.model.CurrencyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDemandAccountApiResponse {

    private String bankCode;
    private String accountNo;
    private CurrencyInfo currency;
}
