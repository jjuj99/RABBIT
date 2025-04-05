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

contract PromissoryNote is ERC721, Ownable, IPromissoryNote, EIP712 {
    uint256 public tokenIdCounter;
    address public schedulerAddress;

    // tokenId, metadata
    mapping(uint256 => PromissoryMetadata) private tokenMetadata;
    mapping(address => bool) public burnAuthorizedAddresses; 
    mapping(address => uint256) private nonces;

    mapping(uint256 => uint256[]) private tokenIdToAppendixIds;  // tokenId, 부속 tokenId 배열
    mapping(uint256 => AppendixMetadata) private appendixTokenMetadata;  // 부속 tokenId, 메타데이터
    mapping(uint256 => bool) private isAppendixToken;  // 해당 tokenId가 부속 NFT인지 아닌지 여부

    bytes32 public constant PERMIT_TYPEHASH = keccak256("Permit(address owner,address spender,uint256 tokenId,uint256 nonce,uint256 deadline)");

    constructor() ERC721("PromissoryNote", "PNFT") EIP712("PromissoryNote", "1") Ownable(msg.sender) {
        tokenIdCounter = 1;
    }

    function setSchedulerAddress(address _schedulerAddress) external onlyOwner {
        schedulerAddress = _schedulerAddress;
    }

    function addBurnAuthorization(address authorized) external onlyOwner {
        burnAuthorizedAddresses[authorized] = true;
    }

    function removeBurnAuthorization(address authorized) external onlyOwner {
        burnAuthorizedAddresses[authorized] = false;
    }

    // ========== 차용증 NFT ==========

    function _tokenExists(uint256 tokenId) internal view returns (bool) {
        return _ownerOf(tokenId) != address(0);
    }

    function getNonce(address user) public view returns (uint256) {
        return nonces[user];
    }

    // NFT 발행 함수
    function mint(
        PromissoryMetadata memory metadata,
        address to
    ) external onlyOwner returns (uint256) {
        require(to != address(0), "Cannot mint to zero address");
        
        // NFT 발행
        uint256 newTokenId = tokenIdCounter;
        _safeMint(to, newTokenId);
        tokenMetadata[newTokenId] = metadata;

        // 상환정보 등록
        IRepaymentScheduler scheduler = IRepaymentScheduler(schedulerAddress);
        scheduler.registerRepaymentSchedule(newTokenId);

        emit PromissoryNoteMinted(newTokenId, to, metadata);
        tokenIdCounter++;
        return newTokenId;
    }

    // permit용 메시지 해시 생성
    function getPermitMessageHash(
        address owner,
        address spender,
        uint256 tokenId,
        uint256 nonce,
        uint256 deadline
    ) public view returns (bytes32) {
        bytes32 structHash = keccak256(abi.encode(PERMIT_TYPEHASH, owner, spender, tokenId, nonce, deadline));
        return _hashTypedDataV4(structHash);
    }

    // 서명을 통해 NFT 이체 승인 - 경매 시 NFT 예치에 사용
    function permit(
        address owner,
        address spender,
        uint256 tokenId,
        uint256 deadline,
        bytes memory signature
    ) external {
        require(block.timestamp <= deadline, "Signature expired");
        require(_tokenExists(tokenId), "Token does not exist");
        require(ownerOf(tokenId) == owner, "Owner mismatch");
        
        // 소유자의 서명 검증
        bytes32 permitMessageHash = getPermitMessageHash(
            owner,
            spender,
            tokenId,
            nonces[owner],
            deadline
        );
        
        address signer = ECDSA.recover(permitMessageHash, signature);
        require(signer == owner, "Invalid signature");
        
        // 논스 증가 (서명 재사용 방지)
        nonces[owner]++;
        
        // 경매 컨트랙트만 승인
        _approve(spender, tokenId, owner);
        
        emit Approval(owner, spender, tokenId);
    }

    // 원본 NFT와 부속 NFT 함께 소각하는 함수
    function burn(uint256 tokenId) external {
        // 부속 NFT는 직접 소각 불가
        require(!isAppendixToken[tokenId], "Cannot burn appendix NFT directly");

        // 본 NFT의 경우 권한 검증
        require(
            _msgSender() == owner() ||  // 컨트랙트 소유자 
            burnAuthorizedAddresses[_msgSender()],  // 권한이 부여된 컨트랙트
            "Not authorized to burn"
        );
        
        // 자식 NFT들 소각 (부모와 함께 소각)
        uint256[] memory childIds = tokenIdToAppendixIds[tokenId];
        for (uint256 i = 0; i < childIds.length; i++) {
            _burn(childIds[i]);
            delete appendixTokenMetadata[childIds[i]];
            delete isAppendixToken[childIds[i]];
        }
        
        // 부모 NFT 소각
        _burn(tokenId);
        delete tokenMetadata[tokenId];
        delete tokenIdToAppendixIds[tokenId];
        
        emit PromissoryNoteBurned(tokenId);
    }

    // 차용증 메타데이터 원본 구조체 반환 - 프로그래밍 방식 처리용
    function getPromissoryMetadata(uint256 tokenId) external view override returns (PromissoryMetadata memory) {
        require(_tokenExists(tokenId), "Token does not exist");
        return tokenMetadata[tokenId];
    }

    // ERC-721 표준 메타데이터 URI 반환 - NFT 마켓플레이스 및 지갑 표시용
    function tokenURI(uint256 tokenId) public view override returns (string memory) {
        require(_tokenExists(tokenId), "Token does not exist");

        // 부속 NFT인 경우 부속 NFT 메타데이터 반환
        if (isAppendixToken[tokenId]) {
            return _buildAppendixTokenURI(tokenId);
        }

        // 본 NFT인 경우 기존 메타데이터 반환
        PromissoryMetadata memory m = tokenMetadata[tokenId];

        string memory part1 = _buildCebtorInfo(m); // 채권자 정보
        string memory part2 = _buildDebtorInfo(m); // 채무자 정보
        string memory part3 = _buildContractTerms(tokenId, m); // 계약 조건
        
        string memory json = string(abi.encodePacked(part1, part2, part3));

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
            '"name":"Promissory Note #', Strings.toString(tokenId), '",',
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

    // ========== 부속 NFT ==========

    // 부속 NFT 발행 함수
    function mintAppendixNFT(
        uint256 originalTokenId,
        AppendixMetadata memory metadata,
        address recipient
    ) external returns (uint256) {
        require(_tokenExists(originalTokenId), "Original token does not exist");
        require(msg.sender == owner() || burnAuthorizedAddresses[msg.sender], "Not authorized to mint appendix");
        
        // 새 토큰 ID 부여
        uint256 newTokenId = tokenIdCounter;
        tokenIdCounter++;
        
        // 부속 NFT 발행 - 컨트랙트 소유 (논리적 연결)
        _mint(address(this), newTokenId);
        
        // 메타데이터 설정
        appendixTokenMetadata[newTokenId] = metadata;
        isAppendixToken[newTokenId] = true;
        
        // 본 차용증 NFT와 번들링
        tokenIdToAppendixIds[originalTokenId].push(newTokenId);
        
        emit AppendixNFTMinted(newTokenId, originalTokenId, recipient);
        emit AppendixNFTBundled(originalTokenId, newTokenId);
        
        return newTokenId;
    }

    // 부속 NFT 메타데이터 조회 함수
    function getAppendixMetadata(uint256 appendixTokenId) external view returns (AppendixMetadata memory) {
        require(_tokenExists(appendixTokenId), "Token does not exist");
        require(isAppendixToken[appendixTokenId], "Not an appendix token");
        return appendixTokenMetadata[appendixTokenId];
    }

    // 원본 NFT의 모든 부속 NFT ID 조회 함수
    function getAppendixTokenIds(uint256 originalTokenId) external view returns (uint256[] memory) {
        require(_tokenExists(originalTokenId), "Token does not exist");
        require(!isAppendixToken[originalTokenId], "Token is an appendix");
        return tokenIdToAppendixIds[originalTokenId];
    }

    // 최신 부속 NFT 조회 함수
    function getLatestAppendixTokenId(uint256 originalTokenId) external view returns (uint256) {
        require(_tokenExists(originalTokenId), "Token does not exist");
        uint256[] memory appendixIds = tokenIdToAppendixIds[originalTokenId];
        require(appendixIds.length > 0, "No appendix tokens found");
        return appendixIds[appendixIds.length - 1];
    }

    // 최신 채권자 주소 조회 함수
    function getLatestCreditorAddress(uint256 originalTokenId) external view returns (address) {
        require(_tokenExists(originalTokenId), "Token does not exist");
        uint256[] memory appendixIds = tokenIdToAppendixIds[originalTokenId];
        
        // 부속 NFT가 없으면 원본 NFT의 채권자 정보 반환
        if (appendixIds.length == 0) {
            return tokenMetadata[originalTokenId].crInfo.crWalletAddress;
        }
        // 최신 부속 NFT의 양수인(현재 채권자) 주소 반환
        else {
            uint256 latestAppendixId = appendixIds[appendixIds.length - 1];
            return appendixTokenMetadata[latestAppendixId].granteeWalletAddress;
        }
    }

    // 부속 NFT 메타데이터 URI 생성 함수
    function _buildAppendixTokenURI(uint256 tokenId) private view returns (string memory) {
        AppendixMetadata memory m = appendixTokenMetadata[tokenId];
        
        string memory json = string(abi.encodePacked(
            '{',
            '"name":"Appendix-', Strings.toString(tokenId), '",',
            '"description":"Appendix Document for Promissory Note #', Strings.toString(m.tokenId), '",',
            '"originalTokenId":"', Strings.toString(m.tokenId), '",',
            '"grantorSign":"', m.grantorSign, '",',
            '"grantorName":"', m.grantorName, '",',
            '"grantorWalletAddress":"', Strings.toHexString(uint256(uint160(m.grantorWalletAddress)), 20), '",',
            '"grantorInfoHash":"', m.grantorInfoHash, '",',
            '"granteeSign":"', m.granteeSign, '",',
            '"granteeName":"', m.granteeName, '",',
            '"granteeWalletAddress":"', Strings.toHexString(uint256(uint160(m.granteeWalletAddress)), 20), '",',
            '"granteeInfoHash":"', m.granteeInfoHash, '",',
            '"la":', Strings.toString(m.la), ',',
            '"contractDate":"', m.contractDate, '",',
            '"originalText":"', m.originalText, '"',
            '}'
        ));
        
        return string(abi.encodePacked(
            "data:application/json;base64,", Base64.encode(bytes(json))
        ));
    }
}