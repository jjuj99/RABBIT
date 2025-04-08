// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./IPromissoryNote.sol";

/**
 * @title IPromissoryNoteAuction
 * @dev 차용증 NFT 경매 및 부속 NFT 관리를 위한 인터페이스
 */
interface IPromissoryNoteAuction {

    // NFT 예치 이벤트
    event NFTDeposited(uint256 indexed tokenId, address indexed depositor, uint256 timestamp);

    // RAB 예치 이벤트
    event RABDeposited(uint256 indexed tokenId, address indexed bidder, uint256 amount, uint256 timestamp);

    // 경매 낙찰 이벤트
    event AuctionFinalized(uint256 indexed tokenId, address indexed seller, address indexed buyer, uint256 amount, uint256 timestamp);

    // 경매 취소 이벤트
    event AuctionCancelled(uint256 indexed tokenId, address indexed seller, uint256 timestamp);

    /**
    * @dev 컨트랙트 주소 업데이트 함수 (관리자 전용)
    * @param _rabbitCoinAddress 새 RABBIT 코인 컨트랙트 주소
    */
    function updateRabbitCoinAddress(address _rabbitCoinAddress) external;

    /**
    * @dev PromissoryNote 주소 업데이트 함수 (관리자 전용)
    * @param _promissoryNoteAddress 새 PromissoryNote 컨트랙트 주소
    */
    function updatePromissoryNoteAddress(address _promissoryNoteAddress) external;

    // ========== NFT 예치 ==========

    /**
    * @dev NFT 예치 기록
    * @param tokenId 차용증 NFT 토큰 ID
    * @param depositor 예치한 사용자 주소
    */
    function recordDepositor(uint256 tokenId, address depositor) external;

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

    // ========== 경매 종료 ==========

    /**
    * @dev 경매 낙찰 처리 함수
    * @param tokenId 경매 중인 NFT의 토큰 ID
    * @param buyer 낙찰자 주소
    * @param bidAmount 낙찰 금액
    * @param metadata 부속 NFT에 포함될 메타데이터
    */
    function finalizeAuction(
        uint256 tokenId,
        address buyer,
        uint256 bidAmount,
        IPromissoryNote.AppendixMetadata memory metadata
    ) external;

    /**
    * @dev 경매 취소 및 자산 반환 함수 (입찰이 없을 때만 가능)
    * @param tokenId 경매 중인 NFT의 토큰 ID
    */
    function cancelAuction(uint256 tokenId) external;
}