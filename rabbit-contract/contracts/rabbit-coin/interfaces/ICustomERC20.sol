// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/extensions/IERC20Metadata.sol";

/**
 * @title ICustomERC20
 * @dev IERC20Metadata를 확장하여 추가 기능(mint, burn)을 정의하는 인터페이스
 */
interface ICustomERC20 is IERC20Metadata {

    // 충전 이벤트
    event Charged(address indexed account, uint256 amount);

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