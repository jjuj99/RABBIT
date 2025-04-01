// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/extensions/IERC20Metadata.sol";

/**
 * @title IRabbitCoin
 * @dev IERC20Metadata를 확장하여 추가 기능을 정의하는 인터페이스
 */
interface IRabbitCoin is IERC20Metadata {

    // 발행, 소각 이벤트
    event RABMinted(address indexed to, uint256 amount);
    event RABBurned(address indexed from, uint256 amount);

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

    /**
     * @dev 시스템 컨트랙트 주소 설정
     * @param systemContract 시스템 컨트랙트 주소
     */
    function setSystemContract(address systemContract) external;
    
    /**
     * @dev RAB 코인 충전
     * @param account 충전할 계좌 주소
     * @param amount 충전할 RAB 양
     * @return 성공 여부
     */
    function charge(address account, uint256 amount) external returns (bool);
    
    /**
     * @dev RAB 코인 환불
     * @param account 소각할 계좌 주소
     * @param amount 소각할 RAB 양
     * @return 성공 여부
     */
    function refund(address account, uint256 amount) external returns (bool);
}