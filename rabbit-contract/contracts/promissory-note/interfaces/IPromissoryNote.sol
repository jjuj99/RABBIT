// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/IERC721.sol";

/**
 * @title IPromissoryNote
 * @dev 차용증 NFT 인터페이스
 */
interface IPromissoryNote is IERC721 {

    // 차용증 NFT 구조체
    struct CrInfo {
        string crSign;           // 채권자 서명
        string crName;           // 채권자 이름
        address crWalletAddress; // 채권자 메타마스크 계좌
        string crInfoHash;       // 채권자 정보들 해싱한 값 (오프체인에 있는 값 무결성 인증)
    }

    struct DrInfo {
        string drSign;           // 채무자 서명
        string drName;           // 채무자 이름
        address drWalletAddress; // 채무자 메타마스크 계좌
        string drInfoHash;       // 채무자 정보들 해싱한 값 (오프체인에 있는 값 무결성 인증)
    }

    struct AddTerms {
        string addTerms;         // 계약의 특별 조건이나 추가 조항
        string addTermsHash;     // 계약서 원문 내용 해싱값 (원문 내용 IPFS 저장)
    }

    struct PromissoryMetadata {
        string nftImage;         // NFT 이미지 (IPFS에 저장된 이미지 주소)
        CrInfo crInfo;           // 채권자 정보
        DrInfo drInfo;           // 채무자 정보
        uint256 la;              // 차용 금액
        uint256 ir;              // 이자율
        uint256 lt;              // 대출 기간 (계약 개월 수)
        string repayType;        // 상환 방식
        string matDt;            // 만기일
        uint256 mpDt;            // 이자 납부일 (매월 n일)
        uint256 dir;             // 연체 이자율
        string contractDate;     // 계약일
        bool earlyPayFlag;       // 중도상환 가능 여부
        uint256 earlyPayFee;     // 중도상환 수수료
        uint256 accel;           // 기한이익상실 횟수
        AddTerms addTerms;       // 추가 조항
    }

    // 부속 NFT 구조체
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

    // 차용증 NFT 발행 이벤트
    event PromissoryNoteMinted(
        uint256 indexed tokenId,
        address indexed to,
        PromissoryMetadata metadata
    );

    event PromissoryNoteBurned(uint256 indexed tokenId);

    // 부속 NFT 발행 이벤트
    event AppendixNFTMinted(
        uint256 indexed appendixTokenId,
        uint256 indexed originalTokenId,
        address indexed newOwner
    );

    event AppendixNFTBundled(
        uint256 indexed originalTokenId,
        uint256 indexed appendixTokenId
    );

    function addBurnAuthorization(address authorized) external;
    function removeBurnAuthorization(address authorized) external;

    // ========== 차용증 NFT ==========
    /**
     * @dev 차용증 발행 함수
     * @param metadata 차용증 메타데이터
     * @param to 발행될 NFT의 소유자 주소
     * @return 발행된 NFT의 토큰 ID
     */
    function mint(
        PromissoryMetadata memory metadata,
        address to
    ) external returns (uint256);

    /**
     * @dev 차용증 메타데이터 조회
     * @param tokenId 토큰 ID
     * @return 차용증 메타데이터
     */
    function getPromissoryMetadata(
        uint256 tokenId
    ) external view returns (PromissoryMetadata memory);

    /**
    * @dev 차용증 NFT 소각 함수
    * @param tokenId 소각할 토큰 ID
    */
    function burn(uint256 tokenId) external;

    // ========== 부속 NFT ==========

    // permit용 메시지 해시 생성
    function getPermitMessageHash(
        address owner,
        address spender,
        uint256 tokenId,
        uint256 nonce,
        uint256 deadline
    ) external view returns (bytes32);

    // 서명을 통한 NFT 승인
    function permit(
        address owner,
        address spender,
        uint256 tokenId,
        uint256 deadline,
        bytes memory signature
    ) external;

    // ========== 부속 NFT ==========
    
    // 부속 NFT 발행 함수
    function mintAppendixNFT(
        uint256 originalTokenId,
        IPromissoryNote.AppendixMetadata memory metadata,
        address recipient
    ) external returns (uint256);

    // 부속 NFT 메타데이터 조회 함수
    function getAppendixMetadata(uint256 appendixTokenId) external view returns (AppendixMetadata memory);

    // 원본 NFT의 모든 부속 NFT ID 조회 함수
    function getAppendixTokenIds(uint256 originalTokenId) external view returns (uint256[] memory);

    // 최신 부속 NFT 조회 함수
    function getLatestAppendixTokenId(uint256 originalTokenId) external view returns (uint256);

    // 최신 채권자 주소 조회 함수
    function getLatestCreditorAddress(uint256 originalTokenId) external view returns (address);
}
