// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "../rabbit-coin/interfaces/IRabbitCoin.sol";
import "./interfaces/IPromissoryNote.sol";
import "./interfaces/IPromissoryNoteAuction.sol";

/**
 * @title PromissoryNoteAuction
 * @dev 차용증 NFT 경매 관리 및 부속 NFT 발행을 위한 통합 컨트랙트
 */
contract PromissoryNoteAuction is IPromissoryNoteAuction, ERC721, Ownable {
    address public rabbitCoinAddress;
    address public promissoryNoteAddress;
    
    // NFT 토큰ID, 판매자
    mapping(uint256 => address) private nftDepositors;

    // NFT 토큰ID, 입찰자, 입찰 금액 
    mapping(uint256 => address) private currentBidders;
    mapping(uint256 => uint256) private biddingAmounts;

    constructor(
        address _rabbitCoinAddress,
        address _promissoryNoteAddress
    ) ERC721("AppendixNFT", "ANFT") Ownable(msg.sender) {
        rabbitCoinAddress = _rabbitCoinAddress;
        promissoryNoteAddress = _promissoryNoteAddress;
    }

    // ========== NFT 예치 ==========

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

    // ========== RAB 예치 ==========

    // 현재 입찰자 확인
    function getCurrentBidder(uint256 tokenId) external view returns (address) {
        return currentBidders[tokenId];
    }

    // 현재 입찰 금액 확인
    function getBiddingAmount(uint256 tokenId) external view returns (uint256) {
        return biddingAmounts[tokenId];
    }

    // RAB 코인을 예치하는 입찰 함수
    function depositRAB(uint256 tokenId, uint256 amount, address bidder) external {
        // 해당 NFT가 예치되어 있는지 확인
        require(nftDepositors[tokenId] != address(0), "NFT not deposited");
        
        // 예치자와 입찰자가 동일인물인지 확인
        require(nftDepositors[tokenId] != bidder, "Depositor cannot bid on their own NFT");
        
        // 이전 입찰 기록 가져오기
        address previousBidder = currentBidders[tokenId];
        uint256 previousAmount = biddingAmounts[tokenId];

        // 이전 입찰 금액보다 높은 경우만 처리
        require(amount > previousAmount, "New bid amount must be higher than previous bid");
        
        IRabbitCoin rabbitCoin = IRabbitCoin(rabbitCoinAddress);
        
        // 같은 입찰자인 경우 차액만 전송
        if (previousBidder == bidder) {
            uint256 additionalAmount = amount - previousAmount;
            require(rabbitCoin.transferFrom(bidder, address(this), additionalAmount), "Additional bid transfer failed");
        } 
        // 다른 입찰자인 경우
        else {
              // 이전 입찰자에게 코인 환불
            if (previousBidder != address(0) && previousAmount > 0) {
                require(rabbitCoin.transfer(previousBidder, previousAmount), "Return previous bid failed");
            }
            
            // 새 입찰자의 코인 예치
            require(rabbitCoin.transferFrom(bidder, address(this), amount), "Bidding transfer failed");
        }
        
        // 상태 업데이트
        currentBidders[tokenId] = bidder;
        biddingAmounts[tokenId] = amount;
        
        // 이벤트 발생
        emit RABDeposited(tokenId, bidder, amount, block.timestamp);
    }

    // ========== 경매 종료 ==========

    // 경매 낙찰 처리 함수
    function finalizeAuction(
        uint256 tokenId,
        address buyer,
        uint256 bidAmount,
        IPromissoryNote.AppendixMetadata memory metadata
    ) external onlyOwner {
        address seller = nftDepositors[tokenId];
        require(seller != address(0), "Auction does not exist");
        require(tokenId == metadata.tokenId, "Metadata tokenId mismatch");
        require(currentBidders[tokenId] == buyer, "Buyer is not the current bidder");
        require(biddingAmounts[tokenId] == bidAmount, "Bid amount does not match");
        
        // 부속 NFT 발행 및 본 차용증 NFT에 번들로 등록
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        promissoryNote.mintAppendixNFT(tokenId, metadata, address(this));
        
        // 차용증 NFT를 낙찰자에게 전송
        promissoryNote.transferFrom(address(this), buyer, tokenId);
        
        // 입찰 금액을 경매 컨트랙트에서 판매자에게 전송
        IRabbitCoin rabbitCoin = IRabbitCoin(rabbitCoinAddress);
        require(rabbitCoin.transfer(seller, bidAmount), "Token transfer to seller failed");

        // 예치 정보 삭제
        delete nftDepositors[tokenId];
        delete currentBidders[tokenId];
        delete biddingAmounts[tokenId];
        
        emit AuctionFinalized(tokenId, seller, buyer, bidAmount, block.timestamp);
    }

    // 경매 취소 함수 (입찰이 없을 때만 가능)
    function cancelAuction(uint256 tokenId) external onlyOwner {
        address seller = nftDepositors[tokenId];
        require(seller != address(0), "Auction does not exist");
        
        // 해당 토큰에 대한 입찰 정보가 없는지 확인
        require(currentBidders[tokenId] == address(0), "Auction has active bids");
        require(biddingAmounts[tokenId] == 0, "Auction has active bids");
        
        // NFT를 원래 소유자에게 반환
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        promissoryNote.transferFrom(address(this), seller, tokenId);
        
        // 예치 정보 삭제
        delete nftDepositors[tokenId];
        
        emit AuctionCancelled(tokenId, seller, block.timestamp);
    }

}