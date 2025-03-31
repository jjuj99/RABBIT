package com.rabbit.global.code.service.impl;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeEnumManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 이메일 로그 상태 코드 관리자
 */
@Component
public class EmailLogStatusCodeManager extends SysCommonCodeEnumManager<SysCommonCodes.EmailLog> {
    @Override
    protected List<SysCommonCodes.EmailLog> retrieveEnumValues() {
        return Arrays.asList(SysCommonCodes.EmailLog.values());
    }
}