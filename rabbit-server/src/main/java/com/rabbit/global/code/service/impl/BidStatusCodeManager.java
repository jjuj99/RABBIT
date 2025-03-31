package com.rabbit.global.code.service.impl;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeEnumManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 입찰 상태 코드 관리자
 */
@Component
public class BidStatusCodeManager extends SysCommonCodeEnumManager<SysCommonCodes.Bid> {
    @Override
    protected List<SysCommonCodes.Bid> retrieveEnumValues() {
        return Arrays.asList(SysCommonCodes.Bid.values());
    }
}