package com.rabbit.blockchain.domain.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class AppendixMetadataDTO {

    private BigInteger tokenId;

    private String grantorSign;
    private String grantorName;
    private String grantorWalletAddress;
    private String grantorInfoHash;

    private String granteeSign;
    private String granteeName;
    private String granteeWalletAddress;
    private String granteeInfoHash;

    private BigInteger la; // 남은 원금
    private String contractDate;
    private String originalText; // 계약서 원문 해시

}

