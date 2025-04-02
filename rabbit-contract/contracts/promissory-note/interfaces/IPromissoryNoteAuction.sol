// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title IPromissoryNoteAuction
 * @dev 차용증 NFT 경매 및 부속 NFT 관리를 위한 인터페이스
 */
interface IPromissoryNoteAuction {

    // NFT 예치 시 발생 이벤트
    event NFTDeposited(uint256 indexed tokenId, address indexed depositor, uint256 timestamp);

    // 차용증 NFT 예치 함수
    function depositNFTWithPermit(
        uint256 tokenId,
        address owner,
        uint256 deadline,
        bytes memory signature
    ) external;
}