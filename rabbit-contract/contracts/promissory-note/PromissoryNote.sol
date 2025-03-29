// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/utils/cryptography/EIP712.sol";
import "@openzeppelin/contracts/utils/cryptography/ECDSA.sol";
import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Strings.sol";
import "@openzeppelin/contracts/utils/Base64.sol";
import "./interfaces/IPromissoryNote.sol";
import "./interfaces/IRepaymentScheduler.sol";
import "../rabbit-coin/interfaces/ICustomERC20.sol";

contract PromissoryNote is ERC721, Ownable, IPromissoryNote, EIP712 {
    uint256 public tokenIdCounter;

    mapping(uint256 => PromissoryMetadata) private tokenIdToMetadata; // IPromissoryNote의의 PromissoryMetadata 구조체 사용
    mapping(address => uint256) private nonces; // 트랜잭션 재사용 방지를 위한 논스
    mapping(address => bool) private burnAuthorizedAddresses; // 소각 권한이 있는 주소 저장

    address public rabbitCoinAddress;
    address public repaymentSchedulerAddress;

    bytes32 public constant TRANSFER_TYPEHASH = keccak256("Transfer(address from,address to,uint256 amount,uint256 nonce,uint256 deadline)");

    constructor(address _rabbitCoinAddress) ERC721("PromissoryNote", "PNFT") EIP712("PromissoryNote", "1") Ownable(msg.sender) {
        tokenIdCounter = 1;
        rabbitCoinAddress = _rabbitCoinAddress;
    }

    // RAB 토큰 주소 업데이트 함수
    function setRabbitCoinAddress(address _rabbitCoinAddress) external onlyOwner {
        require(_rabbitCoinAddress != address(0), "Invalid token address");
        rabbitCoinAddress = _rabbitCoinAddress;
    }

    // RepaymentScheduler 주소 업데이트 함수 (RepaymentScheduler 생성 시 호출)
    function setRepaymentSchedulerAddress(address _repaymentSchedulerAddress) external onlyOwner {
        require(_repaymentSchedulerAddress != address(0), "Invalid address");
        repaymentSchedulerAddress = _repaymentSchedulerAddress;
    }

    function _tokenExists(uint256 tokenId) internal view returns (bool) {
        return _ownerOf(tokenId) != address(0);
    }

    // 사용자의 현재 논스 조회
    function getNonce(address user) public view returns (uint256) {
        return nonces[user];
    }

    // 메시지 해시 생성 (이체용)
    function getTransferMessageHash(
        address from,      // 채권자 (송금자)
        address to,        // 채무자 (수신자)
        uint256 amount,    // 이체 금액
        uint256 nonce,     // 논스
        uint256 deadline   // 유효기간
    ) public view returns (bytes32) {
        bytes32 structHash = keccak256(abi.encode(TRANSFER_TYPEHASH, from, to, amount, nonce, deadline));
        return _hashTypedDataV4(structHash);
    }

    // NFT 발행 함수
    function mintPromissoryNoteWithSignature(
        PromissoryMetadata memory metadata,
        bytes memory creditorSignature,
        uint256 deadline
    ) external onlyOwner returns (uint256) {
        require(block.timestamp <= deadline, "Signature expired");
        
        // 채권자의 서명 검증
        bytes32 transferMessageHash = getTransferMessageHash(
            metadata.crInfo.crWalletAddress, // 채권자 지갑 주소 (송금자)
            metadata.drInfo.drWalletAddress, // 채무자 지갑 주소 (수신자)
            metadata.la, // 차용 금액
            nonces[metadata.crInfo.crWalletAddress],
            deadline
        );
        
        address creditor = ECDSA.recover(transferMessageHash, creditorSignature);
        require(creditor == metadata.crInfo.crWalletAddress, "Invalid creditor signature");
        
        // 논스 증가 (서명 재사용 방지)
        nonces[metadata.crInfo.crWalletAddress]++;
        
        // RAB 토큰 이체 (채권자 -> 채무자)
        require(rabbitCoinAddress != address(0), "RAB token address not set");
        ICustomERC20 rabToken = ICustomERC20(rabbitCoinAddress);
        
        bool transferSuccess = rabToken.transferFrom(
            metadata.crInfo.crWalletAddress,  // 채권자 (송금자)
            metadata.drInfo.drWalletAddress,  // 채무자 (수신자)
            metadata.la                // 차용 금액
        );
        require(transferSuccess, "RAB token transfer failed");

        // NFT 발행 (채권자 지갑으로)
        uint256 newTokenId = tokenIdCounter;
        _safeMint(metadata.crInfo.crWalletAddress, newTokenId);
        tokenIdToMetadata[newTokenId] = metadata;

        emit PromissoryNoteMinted(newTokenId, metadata.crInfo.crWalletAddress, metadata);
        tokenIdCounter++;
        return newTokenId;
    }

    // 소각 권한이 있는 주소 추가 (RepaymentScheduler 등록용)
    function addBurnAuthorizedAddress(address _address) external onlyOwner {
        require(_address != address(0), "Invalid address");
        burnAuthorizedAddresses[_address] = true;
    }

    // 소각 권한이 있는 주소 제거
    function removeBurnAuthorizedAddress(address _address) external onlyOwner {
        burnAuthorizedAddresses[_address] = false;
    }

    // 소각 권한 확인 함수
    function isBurnAuthorized(address _address) external view returns (bool) {
        return burnAuthorizedAddresses[_address];
    }

    // NFT 소각 함수 - 채권자(NFT 소유자)가 직접 소각하는 경우
    function burnByOwner(uint256 tokenId) external {
        require(_tokenExists(tokenId), "Token does not exist");
        require(ownerOf(tokenId) == msg.sender, "Only token owner can burn");
        
        _burn(tokenId);
        delete tokenIdToMetadata[tokenId];

        if (repaymentSchedulerAddress != address(0)) {
            IRepaymentScheduler(repaymentSchedulerAddress).cleanupRepaymentData(tokenId);
        }

        emit PromissoryNoteBurned(tokenId);
    }

    // NFT 소각 함수 - 상환 완료 시 시스템(RepaymentScheduler)에서 소각하는 경우
    function burn(uint256 tokenId) external {
        require(_tokenExists(tokenId), "Token does not exist");
        
        // 컨트랙트 오너 또는 권한이 부여된 주소(RepaymentScheduler)만 소각 가능
        require(
            owner() == msg.sender || 
            burnAuthorizedAddresses[msg.sender], 
            "Not authorized to burn this token"
        );
        
        _burn(tokenId);
        delete tokenIdToMetadata[tokenId];
        emit PromissoryNoteBurned(tokenId);
    }

    // 차용증 메타데이터 원본 구조체 반환 - 프로그래밍 방식 처리용
    function getPromissoryMetadata(uint256 tokenId) external view returns (PromissoryMetadata memory) {
        require(_tokenExists(tokenId), "Token does not exist");
        return tokenIdToMetadata[tokenId];
    }

    // ERC-721 표준 메타데이터 URI 반환 - NFT 마켓플레이스 및 지갑 표시용
    function tokenURI(uint256 tokenId) public view override returns (string memory) {
        require(_tokenExists(tokenId), "Token does not exist");

        // 온체인에 있는 해당 토큰 ID에 연결된 차용증 메타데이터 가져옴
        PromissoryMetadata memory m = tokenIdToMetadata[tokenId];

        string memory part1 = _buildCebtorInfo(m); // 채권자 정보
        string memory part2 = _buildDebtorInfo(m); // 채무자 정보
        string memory part3 = _buildContractTerms(tokenId, m); // 계약 조건
        
        string memory json = string(abi.encodePacked(part1, part2, part3));

        // 메타데이터 자체를 Base64로 인코딩하여 URI에 직접 포함
        return string(abi.encodePacked(
            "data:application/json;base64,", Base64.encode(bytes(json))
        ));
    }

    // 채권자 정보 구성
    function _buildCebtorInfo(PromissoryMetadata memory m) private pure returns (string memory) {
        return string(abi.encodePacked(
            '{',
            '"crSign":"', m.crInfo.crSign, '",',
            '"crName":"', m.crInfo.crName, '",',
            '"crWalletAddress":"', Strings.toHexString(uint256(uint160(m.crInfo.crWalletAddress)), 20), '",',
            '"crInfoHash":"', m.crInfo.crInfoHash, '",'
        ));
    }
    
    // 채무자 정보 구성
    function _buildDebtorInfo(PromissoryMetadata memory m) private pure returns (string memory) {
        return string(abi.encodePacked(
            '"drSign":"', m.drInfo.drSign, '",',
            '"drName":"', m.drInfo.drName, '",',
            '"drWalletAddress":"', Strings.toHexString(uint256(uint160(m.drInfo.drWalletAddress)), 20), '",',
            '"drInfoHash":"', m.drInfo.drInfoHash, '",'
        ));
    }
    
    // 계약 조건 구성
    function _buildContractTerms(uint256 tokenId, PromissoryMetadata memory m) private pure returns (string memory) {
        return string(abi.encodePacked(
            '"name":"', Strings.toString(tokenId), '",',
            '"image":"', m.nftImage, '",',
            '"la":', Strings.toString(m.la), ',',
            '"ir":', Strings.toString(m.ir), ',',
            '"lt":', Strings.toString(m.lt), ',',
            '"repayType":"', m.repayType, '",',
            '"matDt":"', m.matDt, '",',
            '"mpDt":', Strings.toString(m.mpDt), ',',
            '"dir":', Strings.toString(m.dir), ',',
            '"contractDate":"', m.contractDate, '",',
            '"earlyPayFlag":', m.earlyPayFlag ? "true" : "false", ',',
            '"earlyPayFee":', Strings.toString(m.earlyPayFee), ',',
            '"accel":', Strings.toString(m.accel), ',',
            '"addTerms":"', m.addTerms.addTerms, '",',
            '"addTermsHash":"', m.addTerms.addTermsHash, '"}'
        ));
    }
}