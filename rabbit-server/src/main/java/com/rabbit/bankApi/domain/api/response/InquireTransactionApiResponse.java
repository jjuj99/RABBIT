package com.rabbit.bankApi.domain.api.response;

import com.rabbit.bankApi.domain.api.response.model.TransactionInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquireTransactionApiResponse {

    private String totalCount;
    private List<TransactionInfo> list;
}
