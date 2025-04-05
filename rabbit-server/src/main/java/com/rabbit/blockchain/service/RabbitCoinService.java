package com.rabbit.blockchain.service;

import com.rabbit.blockchain.wrapper.RabbitCoin;
import com.rabbit.global.exception.BusinessException;
import com.rabbit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class RabbitCoinService {
    private final RabbitCoin rabbitCoin;

    public TransactionReceipt transfer(String to, BigInteger amount) throws Exception {
        return rabbitCoin.transfer(to, amount).send();
    }

    public TransactionReceipt transferFrom(String from, String to, BigInteger amount) throws Exception {
        return rabbitCoin.transferFrom(from, to, amount).send();
    }

    public TransactionReceipt approve(String spender, BigInteger amount) throws Exception {
        return rabbitCoin.approve(spender, amount).send();
    }

    public TransactionReceipt approveInfiniteForSystem(String account) throws Exception {
        return rabbitCoin.approveInfiniteForSystem(account).send();
    }

    public BigInteger balanceOf(String address) {
        try {
            return rabbitCoin.balanceOf(address).send();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BLOCKCHAIN_ERROR, "블록체인 잔액 조회 중 오류가 발생했습니다.", e);
        }
    }

    public TransactionReceipt mint(String account, BigInteger amount) throws Exception {
        return rabbitCoin.mint(account, amount).send();
    }

    public TransactionReceipt burn(BigInteger amount) throws Exception {
        return rabbitCoin.burn(amount).send();
    }

    public TransactionReceipt charge(String account, BigInteger amount) throws Exception {
        return rabbitCoin.charge(account, amount).send();
    }

    public TransactionReceipt refund(String account, BigInteger amount) throws Exception {
        return rabbitCoin.refund(account, amount).send();
    }

    public BigInteger totalSupply() throws Exception {
        return rabbitCoin.totalSupply().send();
    }

    public BigInteger decimals() throws Exception {
        return rabbitCoin.decimals().send();
    }

    public String name() throws Exception {
        return rabbitCoin.name().send();
    }

    public String symbol() throws Exception {
        return rabbitCoin.symbol().send();
    }

    public String owner() throws Exception {
        return rabbitCoin.owner().send();
    }
}
