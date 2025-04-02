// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "./interfaces/IPromissoryNote.sol";
import "./interfaces/IPromissoryNoteAuction.sol";

/**
 * @title PromissoryNoteAuction
 * @dev 차용증 NFT 경매 관리 및 부속 NFT 발행을 위한 통합 컨트랙트
 */
contract PromissoryNoteAuction is IPromissoryNoteAuction, ERC721, Ownable {
    address public promissoryNoteAddress;
    
    // NFT 토큰 ID : 판매자 메타마스크 계좌
    mapping(uint256 => address) private nftDepositors;

    constructor(
        address _promissoryNoteAddress
    ) ERC721("AppendixNFT", "ANFT") Ownable(msg.sender) {
        promissoryNoteAddress = _promissoryNoteAddress;
    }

    // 예치된 NFT의 소유자 확인
    function getDepositor(uint256 tokenId) external view returns (address) {
        return nftDepositors[tokenId];
    }

    // NFT 예치 함수
    function depositNFTWithPermit(
        uint256 tokenId,
        address owner,
        uint256 deadline,
        bytes memory signature
    ) external {
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        
        // permit 함수 호출하여 승인 처리
        promissoryNote.permit(
            owner,
            address(this),
            tokenId,
            deadline,
            signature
        );
        
        // 승인 후 NFT 예치 처리
        promissoryNote.transferFrom(owner, address(this), tokenId);
        nftDepositors[tokenId] = owner; // 실제 소유자(예치자) 정보 저장
        
        emit NFTDeposited(tokenId, owner, block.timestamp);
    }
}