package com.rabbit.contract.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rabbit.contract.domain.dto.request.ContractSearchRequestDTO;
import com.rabbit.contract.domain.entity.Contract;

public interface ContractRepositoryCustom {

    /**
     * 계약 목록을 검색 조건에 따라 조회합니다.
     * @param searchRequest 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 계약 목록
     */
    Page<Contract> findBySearchCondition(ContractSearchRequestDTO searchRequest, Pageable pageable);

    /**
     * 사용자와 관련된 계약 목록을 타입에 따라 조회합니다.
     * @param userId 사용자 ID
     * @param type 계약 유형 (sent: 보낸 계약, received: 받은 계약)
     * @param pageable 페이징 정보
     * @return 사용자와 관련된 계약 목록
     */
    Page<Contract> findContractsByUserIdAndType(Integer userId, String type, Pageable pageable);
}