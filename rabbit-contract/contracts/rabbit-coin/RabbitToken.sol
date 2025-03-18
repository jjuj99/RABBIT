// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./interfaces/ICustomERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title RabbitToken
 * @dev ERC-20 표준을 구현한 RABBIT 토큰
 */
contract RabbitToken is ICustomERC20, Ownable {
    string public name = "Rabbit Token";
    string public symbol = "RABBIT";
    uint8 public decimals = 18;
    uint256 private _totalSupply;
    
    // 각 주소별 잔액을 저장하는 매핑
    mapping(address => uint256) private _balances;
    
    // 특정 주소가 다른 주소에게 허용한 토큰 양을 저장하는 매핑
    mapping(address => mapping(address => uint256)) private _allowances;
    
    /**
     * @dev 생성자. 초기 토큰 공급량을 설정하고 배포자에게 할당
     * @param initialSupply 초기 토큰 공급량
     */
    constructor(uint256 initialSupply) Ownable(msg.sender) {
        _totalSupply = initialSupply * 10**uint256(decimals);
        _balances[msg.sender] = _totalSupply;
        emit Transfer(address(0), msg.sender, _totalSupply);
    }
    
    /**
     * @dev 토큰의 총 공급량을 반환
     */
    function totalSupply() external view override returns (uint256) {
        return _totalSupply;
    }
    
    /**
     * @dev 특정 계정의 토큰 잔액을 반환
     * @param account 잔액을 조회할 주소
     */
    function balanceOf(address account) external view override returns (uint256) {
        return _balances[account];
    }
    
    /**
     * @dev 특정 주소로 토큰을 전송
     * @param recipient 수신자 주소
     * @param amount 전송할 토큰 양
     */
    function transfer(address recipient, uint256 amount) external override returns (bool) {
        _transfer(msg.sender, recipient, amount);
        return true;
    }
    
    /**
     * @dev 토큰 소유자가 다른 주소에 허용한 토큰 양을 반환
     * @param owner 토큰 소유자 주소
     * @param spender 토큰 사용이 허용된 주소
     */
    function allowance(address owner, address spender) external view override returns (uint256) {
        return _allowances[owner][spender];
    }
    
    /**
     * @dev 다른 주소가 귀하의 계정에서 사용할 수 있는 토큰 양을 승인
     * @param spender 토큰 사용이 허용될 주소
     * @param amount 승인할 토큰 양
     */
    function approve(address spender, uint256 amount) external override returns (bool) {
        _approve(msg.sender, spender, amount);
        return true;
    }
    
    /**
     * @dev 승인된 양만큼 한 주소에서 다른 주소로 토큰을 전송
     * @param sender 토큰을 보내는 주소
     * @param recipient 토큰을 받는 주소
     * @param amount 전송할 토큰 양
     */
    function transferFrom(address sender, address recipient, uint256 amount) external override returns (bool) {
        uint256 currentAllowance = _allowances[sender][msg.sender];
        require(currentAllowance >= amount, "ERC20: transfer amount exceeds allowance");
        
        _transfer(sender, recipient, amount);
        
        _approve(sender, msg.sender, currentAllowance - amount);
        return true;
    }
    
    /**
     * @dev 토큰 양을 늘림 (Mint)
     * @param account 토큰을 받을 주소
     * @param amount 추가할 토큰 양
     */
    function mint(address account, uint256 amount) external override onlyOwner {
        require(account != address(0), "ERC20: mint to the zero address");
        
        _totalSupply += amount;
        _balances[account] += amount;
        emit Transfer(address(0), account, amount);
    }
    
    /**
     * @dev 토큰 양을 소각 (Burn)
     * @param amount 소각할 토큰 양
     */
    function burn(uint256 amount) external {
        require(_balances[msg.sender] >= amount, "ERC20: burn amount exceeds balance");
        
        _balances[msg.sender] -= amount;
        _totalSupply -= amount;
        emit Transfer(msg.sender, address(0), amount);
    }
    
    /**
     * @dev 내부 전송 함수
     */
    function _transfer(address sender, address recipient, uint256 amount) internal {
        require(sender != address(0), "ERC20: transfer from the zero address");
        require(recipient != address(0), "ERC20: transfer to the zero address");
        require(_balances[sender] >= amount, "ERC20: transfer amount exceeds balance");
        
        _balances[sender] -= amount;
        _balances[recipient] += amount;
        emit Transfer(sender, recipient, amount);
    }
    
    /**
     * @dev 내부 승인 함수
     */
    function _approve(address owner, address spender, uint256 amount) internal {
        require(owner != address(0), "ERC20: approve from the zero address");
        require(spender != address(0), "ERC20: approve to the zero address");
        
        _allowances[owner][spender] = amount;
        emit Approval(owner, spender, amount);
    }
}