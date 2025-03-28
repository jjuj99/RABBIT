// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/IERC721.sol";

/**
 * @title IPromissoryNote
 * @dev 차용증 NFT 인터페이스
 */
interface IPromissoryNote is IERC721 {

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
        uint256 lt;              // 대출 기간
        string repayType;        // 상환 방식
        string matDt;            // 만기일
        uint256 mpDt;            // 이자 납부일 (매월 n일)
        uint256 dir;             // 연체 이자율
        string contractDate;     // 계약일
        bool earlyPayFlag;       // 중도상환 가능 여부
        uint256 earlyPayFee;     // 중도상환 수수료
        uint256 defCnt;          // 기한이익상실 횟수
        AddTerms addTerms;       // 추가 조항
    }
    
    // NFT 발행 이벤트
    event PromissoryNoteMinted(
        uint256 indexed tokenId,
        address indexed to,
        PromissoryMetadata metadata
    );

    // NFT 소각 이벤트
    event PromissoryNoteBurned(
        uint256 indexed tokenId
    );
    
    /**
    * @dev 메시지 서명으로 차용증 발행 및 코인 이체
    * @param metadata 차용증 메타데이터
    * @param creditorSignature 채권자의 서명
    * @param deadline 서명의 유효 기간
    * @return 발행된 NFT의 토큰 ID
    */
    function mintPromissoryNoteWithSignature(
        PromissoryMetadata memory metadata,
        bytes memory creditorSignature,
        uint256 deadline
    ) external returns (uint256);
    
    /**
     * @dev 차용증 메타데이터 조회
     * @param tokenId 토큰 ID
     * @return 차용증 메타데이터
     */
    function getPromissoryMetadata(uint256 tokenId) external view returns (PromissoryMetadata memory);

    /**
    * @dev NFT 소각 함수 - 시스템에서 상환 완료 시 호출
    * @param tokenId 소각할 토큰 ID
    */
    function burn(uint256 tokenId) external;

    /**
    * @dev NFT 소각 함수 - 채권자(NFT 소유자)가 직접 호출
    * @param tokenId 소각할 토큰 ID
    */
    function burnByOwner(uint256 tokenId) external;
    
    /**
    * @dev 소각 권한 부여 함수
    * @param _address 소각 권한을 부여할 주소
    */
    function addBurnAuthorizedAddress(address _address) external;
    
    /**
    * @dev 소각 권한 제거 함수
    * @param _address 소각 권한을 제거할 주소
    */
    function removeBurnAuthorizedAddress(address _address) external;
    
    /**
    * @dev 소각 권한 확인 함수
    * @param _address 확인할 주소
    * @return 소각 권한 여부
    */
    function isBurnAuthorized(address _address) external view returns (bool);
}