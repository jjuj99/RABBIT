// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/extensions/IERC20Metadata.sol";

interface ICustomERC20 is IERC20Metadata {
    // 토큰의 총 공급량을 반환
    function totalSupply() external view returns (uint256);

    // 특정 계정의 토큰 잔액을 반환
    function balanceOf(address account) external view returns (uint256);

    // 특정 주소로 토큰을 전송하고 성공 여부를 반환
    function transfer(address recipient, uint256 amount) external returns (bool);

    // 토큰 소유자가 다른 주소에 허용한 토큰 양을 반환
    function allowance(address owner, address spender) external view returns (uint256);

    // 다른 주소가 귀하의 계정에서 사용할 수 있는 토큰 양을 승인
    function approve(address spender, uint256 amount) external returns (bool);

    // 승인된 양만큼 한 주소에서 다른 주소로 토큰을 전송
    function transferFrom(address sender, address recipient, uint256 amount) external returns (bool);

    // 토큰 전송 시 발생하는 이벤트
    // event Transfer(address indexed from, address indexed to, uint256 value);

    // 토큰 승인 시 발생하는 이벤트
    // event Approval(address indexed owner, address indexed spender, uint256 value);
}