package com.rabbit.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Slf4j
@Configuration
public class Web3jConfig {

    @Value("${blockchain.rpcUrl}")
    private String rpcUrl;

    @Value("${blockchain.privateKey}")
    private String privateKey;

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

}
