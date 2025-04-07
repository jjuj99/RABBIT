package com.rabbit.contract.service;

import java.math.BigInteger;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rabbit.contract.domain.dto.request.ContractRejectRequestDTO;
import com.rabbit.contract.domain.dto.request.ContractRequestDTO;
import com.rabbit.contract.domain.dto.request.ContractSearchRequestDTO;
import com.rabbit.contract.domain.dto.response.ContractConfigResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractDetailResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractListResponseDTO;
import com.rabbit.contract.domain.dto.response.ContractResponseDTO;
import com.rabbit.global.response.PageResponseDTO;
import com.rabbit.user.domain.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 계약 서비스 파사드 클래스
 * 분리된 서비스 클래스들을 통합하여 컨트롤러에 단일 인터페이스 제공
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractQueryService contractQueryService;
    private final ContractCommandService contractCommandService;

    /**
     * 계약 설정 정보 조회
     */
    public ContractConfigResponseDTO getContractConfig() {
        return contractQueryService.getContractConfig();
    }

    /**
     * 계약 목록 조회
     */
    public PageResponseDTO<ContractListResponseDTO> getContracts(Integer userId, String type, Pageable pageable) {
        return contractQueryService.getContracts(userId, type, pageable);
    }

    /**
     * 계약 검색
     */
    public PageResponseDTO<ContractResponseDTO> searchContracts(ContractSearchRequestDTO searchRequest) {
        return contractQueryService.searchContracts(searchRequest);
    }

    /**
     * 계약 상세 조회
     */
    public ContractDetailResponseDTO getContractDetail(Integer userId, Integer contractId) {
        return contractQueryService.getContractDetail(userId, contractId);
    }

    /**
     * 계약 생성
     */
    public ContractResponseDTO createContract(Integer userId, ContractRequestDTO requestDTO) {
        return contractCommandService.createContract(userId, requestDTO);
    }

    /**
     * 계약 취소
     */
    public ContractResponseDTO cancelContract(Integer contractId, Integer userId) {
        return contractCommandService.cancelContract(contractId, userId);
    }

    /**
     * 계약 완료 처리
     */
    public ContractResponseDTO completeContract(Integer contractId, Integer userId) {
        return contractCommandService.completeContract(contractId, userId);
    }

    /**
     * 계약 반려
     */
    public ContractResponseDTO rejectContract(Integer contractId, Integer userId, ContractRejectRequestDTO requestDTO) {
        return contractCommandService.rejectContract(contractId, userId, requestDTO);
    }

    /**
     * 계약 삭제 (논리적 삭제)
     */
    public void deleteContract(Integer contractId, Integer userId) {
        contractCommandService.deleteContract(contractId, userId);
    }

    /**
     * NFT 토큰 전송 후 채권자 변경
     */
    public void changeCreditorByTokenId(BigInteger tokenId, Integer newCreditorId) {
        contractCommandService.changeCreditorByTokenId(tokenId, newCreditorId);
    }

    /**
     * 토큰 ID로 채무자 조회
     */
    public User getDebtorByTokenId(BigInteger tokenId) {
        return contractQueryService.getDebtorByTokenId(tokenId);
    }
}