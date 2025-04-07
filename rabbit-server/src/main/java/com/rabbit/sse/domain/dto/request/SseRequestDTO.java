package com.rabbit.sse.domain.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SseRequestDTO {
    private String type;
    private String id;

    public String toKey() {
        return type + "-" + id;
    }
}
