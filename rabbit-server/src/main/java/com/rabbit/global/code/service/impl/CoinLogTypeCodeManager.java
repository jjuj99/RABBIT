package com.rabbit.global.code.service.impl;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeEnumManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 코인 로그 타입 코드 관리자
 */
@Component
public class CoinLogTypeCodeManager extends SysCommonCodeEnumManager<SysCommonCodes.CoinLog> {
    @Override
    protected List<SysCommonCodes.CoinLog> retrieveEnumValues() {
        return Arrays.asList(SysCommonCodes.CoinLog.values());
    }
}