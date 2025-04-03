package com.rabbit.bankApi.domain.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCreditApiResponse {

    private String ratingName;
    private String demandDepositAssetValue;
    private String depositSavingsAssetValue;
    private String totalAssetValue;
}
