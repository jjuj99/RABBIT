// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title IPromissoryNoteAuction
 * @dev 차용증 NFT 경매 및 부속 NFT 관리를 위한 인터페이스
 */
interface IPromissoryNoteAuction {

    // NFT 예치 시 발생 이벤트
    event NFTDeposited(uint256 indexed tokenId, address indexed depositor, uint256 timestamp);
    event RABDeposited(uint256 indexed tokenId, address indexed bidder, uint256 amount, uint256 timestamp);

    // ========== NFT 예치 ==========

    /**
    * @dev 예치된 NFT의 소유자 확인
    * @param tokenId 차용증 NFT 토큰 ID
    * @return 예치전 소유자 주소
    */
    function getDepositor(uint256 tokenId) external view returns (address);

    // 차용증 NFT 예치 함수
    function depositNFTWithPermit(
        uint256 tokenId,
        address owner,
        uint256 deadline,
        bytes memory signature
    ) external;

    // ========== RAB 예치 ==========
    
    /**
    * @dev 현재 입찰자 확인
    * @param tokenId 차용증 NFT 토큰 ID
    * @return 현재 입찰자 주소
    */
    function getCurrentBidder(uint256 tokenId) external view returns (address);

    /**
    * @dev 현재 입찰 금액 확인
    * @param tokenId 차용증 NFT 토큰 ID
    * @return 현재 입찰 금액
    */
    function getBiddingAmount(uint256 tokenId) external view returns (uint256);

     /**
    * @dev RAB 코인을 예치하는 입찰 함수
    * @param tokenId 입찰할 차용증 NFT 토큰 ID
    * @param amount 입찰 금액 (RAB)
    * @param bidder 입찰자
    */
    function depositRAB(uint256 tokenId, uint256 amount, address bidder) external;
}