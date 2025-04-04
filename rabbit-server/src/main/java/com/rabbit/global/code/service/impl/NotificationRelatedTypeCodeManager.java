package com.rabbit.global.code.service.impl;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeEnumManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 경매 상태 코드 관리자
 */
@Component
public class NotificationRelatedTypeCodeManager extends SysCommonCodeEnumManager<SysCommonCodes.NotificationRelatedType> {
    @Override
    protected List<SysCommonCodes.NotificationRelatedType> retrieveEnumValues() {  // 메서드명 수정
        return Arrays.asList(SysCommonCodes.NotificationRelatedType.values());
    }
}