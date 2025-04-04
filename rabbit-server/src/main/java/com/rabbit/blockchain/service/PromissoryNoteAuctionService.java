package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class PromissoryNoteAuctionService {
    private final PromissoryNoteAuction contract;

    public TransactionReceipt depositNFTWithPermit(BigInteger tokenId, String owner, BigInteger deadline, byte[] signature) throws Exception {
        return contract.depositNFTWithPermit(tokenId, owner, deadline, signature).send();
    }

    public TransactionReceipt cancelAuction(BigInteger tokenId) throws Exception {
        return contract.cancelAuction(tokenId).send();
    }

    public TransactionReceipt finalizeAuction(BigInteger tokenId, String buyer, BigInteger bidAmount,
                                              PromissoryNoteAuction.AppendixMetadata metadata) throws Exception {
        return contract.finalizeAuction(tokenId, buyer, bidAmount, metadata).send();
    }

    public String getCurrentBidder(BigInteger tokenId) throws Exception {
        return contract.getCurrentBidder(tokenId).send();
    }

    public BigInteger getBiddingAmount(BigInteger tokenId) throws Exception {
        return contract.getBiddingAmount(tokenId).send();
    }

    public String getDepositor(BigInteger tokenId) throws Exception {
        return contract.getDepositor(tokenId).send();
    }

    public TransactionReceipt depositRAB(BigInteger tokenId, BigInteger amount, String bidderAddress) throws Exception {
        return contract.depositRAB(tokenId, amount, bidderAddress).send();
    }
}
