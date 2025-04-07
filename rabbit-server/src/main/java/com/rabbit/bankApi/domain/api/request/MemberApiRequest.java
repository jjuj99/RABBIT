package com.rabbit.bankApi.domain.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberApiRequest {

    private String userId;
    private String apiKey;
}
