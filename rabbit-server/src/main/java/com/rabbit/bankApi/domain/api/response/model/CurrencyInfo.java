package com.rabbit.bankApi.domain.api.response.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyInfo {

    private String currency;
    private String currencyName;
}
