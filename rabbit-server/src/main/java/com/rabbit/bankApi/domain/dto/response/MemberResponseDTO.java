package com.rabbit.bankApi.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "유저 관련 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDTO {

    private String userId;
    private String userName;
    private String institutionCode;
    private String userKey;
    private String created;
    private String modified;
}
