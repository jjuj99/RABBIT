package com.rabbit.global.code.service.impl;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeEnumManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ContractStatusCodeManager extends SysCommonCodeEnumManager<SysCommonCodes.Contract> {
    @Override
    protected List<SysCommonCodes.Contract> retrieveEnumValues() {
        return Arrays.asList(SysCommonCodes.Contract.values());
    }
}
