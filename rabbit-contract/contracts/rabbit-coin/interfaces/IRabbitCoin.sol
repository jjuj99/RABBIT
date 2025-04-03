// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/extensions/IERC20Metadata.sol";

/**
 * @title IRabbitCoin
 * @dev IERC20Metadata를 확장하여 추가 기능을 정의하는 인터페이스
 */
interface IRabbitCoin is IERC20Metadata {

    /**
    * @dev EIP-2612 서명을 통한 승인
    * @param owner 토큰 소유자 주소
    * @param spender 토큰 사용이 허용될 주소
    * @param value 승인할 토큰 양
    * @param deadline 서명 유효기간
    * @param signature 서명 데이터
    */
    function permit(
        address owner,
        address spender,
        uint256 value,
        uint256 deadline,
        bytes memory signature
    ) external;

    /**
     * @dev 토큰 양을 늘림 (Mint)
     * @param account 토큰을 받을 주소
     * @param amount 추가할 토큰 양
     */
    function mint(address account, uint256 amount) external;
    
    /**
     * @dev 토큰 양을 소각 (Burn)
     * @param amount 소각할 토큰 양
     */
    function burn(uint256 amount) external;
}