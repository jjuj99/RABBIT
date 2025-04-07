package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class PromissoryNoteAuctionService {
    private final PromissoryNoteAuction contract;

    public TransactionReceipt depositNFTWithPermit(BigInteger tokenId, String owner, BigInteger deadline, byte[] signature) {
        try {
            return contract.depositNFTWithPermit(tokenId, owner, deadline, signature).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "NFT 예치 중 오류가 발생했습니다.", e);
        }
    }

    public TransactionReceipt cancelAuction(BigInteger tokenId) {
        try {
            return contract.cancelAuction(tokenId).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "경매 취소 중 오류가 발생했습니다.", e);
        }
    }

    public TransactionReceipt finalizeAuction(BigInteger tokenId, String buyer, BigInteger bidAmount,
                                              PromissoryNoteAuction.AppendixMetadata metadata) {
        try {
            return contract.finalizeAuction(tokenId, buyer, bidAmount, metadata).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "경매 낙찰 처리 중 오류가 발생했습니다.", e);
        }
    }

    public String getCurrentBidder(BigInteger tokenId) {
        try {
            return contract.getCurrentBidder(tokenId).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "입찰자 조회 중 오류가 발생했습니다.", e);
        }
    }

    public BigInteger getBiddingAmount(BigInteger tokenId) {
        try {
            return contract.getBiddingAmount(tokenId).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "입찰 금액 조회 중 오류가 발생했습니다.", e);
        }
    }

    public String getDepositor(BigInteger tokenId) {
        try {
            return contract.getDepositor(tokenId).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "예치자 정보 조회 중 오류가 발생했습니다.", e);
        }
    }

    public TransactionReceipt depositRAB(BigInteger tokenId, BigInteger amount, String bidderAddress) {
        try {
            return contract.depositRAB(tokenId, amount, bidderAddress).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "RAB 예치 중 오류가 발생했습니다.", e);
        }
    }
}
