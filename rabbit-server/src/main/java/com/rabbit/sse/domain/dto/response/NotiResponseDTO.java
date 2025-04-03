package com.rabbit.sse.domain.dto.response;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiResponseDTO {
    private String type;
    private String message;
    private String tokenId;
}
