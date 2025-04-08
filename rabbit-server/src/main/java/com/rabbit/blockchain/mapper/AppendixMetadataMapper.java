package com.rabbit.blockchain.mapper;

import com.rabbit.blockchain.domain.dto.response.AppendixMetadataDTO;
import com.rabbit.blockchain.wrapper.PromissoryNoteAuction;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

public class AppendixMetadataMapper {
    public static PromissoryNoteAuction.AppendixMetadata toWeb3(AppendixMetadataDTO dto) {
        return new PromissoryNoteAuction.AppendixMetadata(
                new Uint256(dto.getTokenId()),
                new Utf8String(dto.getGrantorSign()),
                new Utf8String(dto.getGrantorName()),
                new Address(dto.getGrantorWalletAddress()),
                new Utf8String(dto.getGrantorInfoHash()),

                new Utf8String(dto.getGranteeSign()),
                new Utf8String(dto.getGranteeName()),
                new Address(dto.getGranteeWalletAddress()),
                new Utf8String(dto.getGranteeInfoHash()),

                new Uint256(dto.getLa()),
                new Utf8String(dto.getContractDate()),
                new Utf8String(dto.getOriginalText())
        );
    }
}
