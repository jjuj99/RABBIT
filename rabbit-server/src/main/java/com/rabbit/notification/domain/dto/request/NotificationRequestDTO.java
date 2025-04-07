package com.rabbit.notification.domain.dto.request;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationRequestDTO {
    private Integer userId;
    private SysCommonCodes.NotificationType type;
    private SysCommonCodes.NotificationRelatedType relatedType;
    private Integer relatedId;
}
