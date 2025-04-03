package com.rabbit.bankApi.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "User Key를 사용한 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKeyRequestDTO {

    @Schema(example = "a9b501b9-dcec-4eaf-9f98-3b9bf5393ef0")
    private String userKey;
}
