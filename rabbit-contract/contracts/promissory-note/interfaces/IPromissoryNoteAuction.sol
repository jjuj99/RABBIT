// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title IPromissoryNoteAuction
 * @dev 차용증 NFT 경매 및 부속 NFT 관리를 위한 인터페이스
 */
interface IPromissoryNoteAuction {

    struct AppendixMetadata {
        uint256 tokenId;                  // 원본 NFT 토큰 ID
        string grantorSign;               // 양도인 서명
        string grantorName;               // 양도인 이름  
        address grantorWalletAddress;     // 양도인 메타마스크 계좌
        string grantorInfoHash;           // 양도인 정보 해시

        string granteeSign;               // 양수인 서명
        string granteeName;               // 양수인 이름  
        address granteeWalletAddress;     // 양수인 메타마스크 계좌
        string granteeInfoHash;           // 양수인 정보 해시

        uint256 la;                       // 차용 금액 (남은 원금)
        string contractDate;              // 계약일
        string originalText;              // 계약서 원문 해시
    }

    // NFT 예치 이벤트
    event NFTDeposited(uint256 indexed tokenId, address indexed depositor, uint256 timestamp);

    // RAB 예치 이벤트
    event RABDeposited(uint256 indexed tokenId, address indexed bidder, uint256 amount, uint256 timestamp);

    // 경매 낙찰 이벤트
    event AuctionFinalized(uint256 indexed tokenId, address indexed seller, address indexed buyer, uint256 amount, uint256 timestamp);

    // 경매 취소 이벤트
    event AuctionCancelled(uint256 indexed tokenId, address indexed seller, uint256 timestamp);

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

    // ========== 경매 낙찰 ==========

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
        AppendixMetadata memory metadata
    ) external;

    /**
    * @dev 경매 취소 및 자산 반환 함수 (입찰이 없을 때만 가능)
    * @param tokenId 경매 중인 NFT의 토큰 ID
    */
    function cancelAuction(uint256 tokenId) external;
}