package com.rabbit.global.config;

import com.rabbit.blockchain.wrapper.PromissoryNote;
import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import com.rabbit.blockchain.wrapper.RabbitCoin;
import com.rabbit.blockchain.wrapper.RepaymentScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

@Slf4j
@Configuration
public class Web3jConfig {

    @Value("${blockchain.rpcUrl}")
    private String rpcUrl;

    @Value("${blockchain.privateKey}")
    private String privateKey;

    @Value("${blockchain.promissoryNote.address}")
    private String promissoryNoteAddress;

    @Value("${blockchain.promissoryNoteAuction.address}")
    private String promissoryNoteAuctionAddress;

    @Value("${blockchain.repaymentScheduler.address}")
    private String repaymentSchedulerAddress;

    @Value("${blockchain.rabbitCoin.address}")
    private String rabbitCoinAddress;

    @Bean
    public Web3j web3j() { // Spring 프로젝트 어디서든 블록체인과 연결
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));

        // 연결 테스트 (선택사항)
        try {
            String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.info("Connected to Ethereum client version: {}", clientVersion);
        } catch (Exception e) {
            log.error("Error connecting to Ethereum node", e);
        }

        return web3j;
    }

    @Bean
    public Credentials credentials() { // 지갑의 개인키를 이용해서 트랜잭션을 서명할 수 있게 함
        return Credentials.create(privateKey);
    }

    @Bean
    public PromissoryNote promissoryNote(Web3j web3j, Credentials credentials) {
        return PromissoryNote.load(promissoryNoteAddress, web3j, credentials, new DefaultGasProvider());
    }

    @Bean
    public PromissoryNoteAuction promissoryNoteAuction(Web3j web3j, Credentials credentials) {
        return PromissoryNoteAuction.load(promissoryNoteAuctionAddress, web3j, credentials, new DefaultGasProvider());
    }

    @Bean
    public RepaymentScheduler repaymentScheduler(Web3j web3j, Credentials credentials) {
        return RepaymentScheduler.load(repaymentSchedulerAddress, web3j, credentials, new DefaultGasProvider());
    }

    @Bean
    public RabbitCoin rabbitCoin(Web3j web3j, Credentials credentials) {
        return RabbitCoin.load(rabbitCoinAddress, web3j, credentials, new DefaultGasProvider());
    }
}
