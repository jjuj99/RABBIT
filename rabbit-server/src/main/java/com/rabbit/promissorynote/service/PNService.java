package com.rabbit.promissorynote.service;

import com.rabbit.blockchain.service.RepaymentSchedulerService;
import com.rabbit.contract.domain.entity.Contract;
import com.rabbit.contract.repository.ContractRepository;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import com.rabbit.promissorynote.domain.dto.request.PrepaymentRequestDTO;
import com.rabbit.promissorynote.domain.entity.PromissoryNoteEntity;
import com.rabbit.promissorynote.repository.PromissoryNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class PNService {

    private final RepaymentSchedulerService repaymentSchedulerService;

    private final ContractRepository contractRepository;
    private final PromissoryNoteRepository promissoryNoteRepository;

    public void prepayment(PrepaymentRequestDTO request, Integer contractId, Integer userId) {
        // 1. 차용증 정보 불러오기
        Contract contract = contractRepository.findByContractIdAndDeletedFlagFalse(contractId)
                 .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 차용증 ID입니다."));

        // 2. 채무자 정보가 현재 userId 정보와 같은지 확인 -> 다르면 오류
        if (!contract.getDebtor().getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "접근 권한이 없는 차용증입니다.");
        }

        // 3. 중도 상환 처리
        repaymentSchedulerService.processEarlyRepayment(contract.getTokenId(), BigInteger.valueOf(request.getPrepaymentAmount()));
    }
}
