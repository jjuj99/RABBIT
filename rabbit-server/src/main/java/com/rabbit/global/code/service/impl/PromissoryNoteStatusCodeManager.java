package com.rabbit.global.code.service.impl;

import com.rabbit.global.code.domain.enums.SysCommonCodes;
import com.rabbit.global.code.service.SysCommonCodeEnumManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 차용증 상태 코드 관리자
 */
@Component
public class PromissoryNoteStatusCodeManager extends SysCommonCodeEnumManager<SysCommonCodes.PromissoryNote> {
    @Override
    protected List<SysCommonCodes.PromissoryNote> retrieveEnumValues() {
        return Arrays.asList(SysCommonCodes.PromissoryNote.values());
    }
}