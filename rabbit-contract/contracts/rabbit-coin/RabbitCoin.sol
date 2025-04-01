// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./interfaces/IRabbitCoin.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title RabbitCoin
 * @dev ERC-20 표준을 구현한 RABBIT 코인
 */
contract RabbitCoin is IRabbitCoin, Ownable {
    string public constant name = "RABBIT";
    string public constant symbol = "RAB";
    uint8 public constant decimals = 0;
    uint256 private _totalSupply;
    address private _systemContract; // 시스템 컨트랙트 주소

    // 각 주소별 잔액을 저장하는 매핑
    mapping(address => uint256) private _balances;
    
    // 특정 주소가 다른 주소에게 허용한 RAB 양을 저장하는 매핑
    mapping(address => mapping(address => uint256)) private _allowances;
    
    /**
     * @dev 생성자. 초기 RAB 공급량을 설정하고 배포자에게 할당
     * @param initialSupply 초기 RAB 공급량
     */
    constructor(uint256 initialSupply) Ownable(msg.sender) {
        _totalSupply = initialSupply * 10**uint256(decimals);
        _balances[msg.sender] = _totalSupply;
        emit Transfer(address(0), msg.sender, _totalSupply);
    }
    
    /**
     * @dev 시스템 컨트랙트 주소 설정
     * @param systemContract 시스템 컨트랙트 주소
     */
    function setSystemContract(address systemContract) external onlyOwner {
        require(_systemContract == address(0), "System contract already set");
        require(systemContract != address(0), "Invalid system contract address");
        _systemContract = systemContract;
    }
    
    // ============== ERC-20 기본 함수 ==============

    /**
     * @dev RAB의 총 공급량을 반환
     */
    function totalSupply() external view override returns (uint256) {
        return _totalSupply;
    }
    
    /**
     * @dev 특정 계좌의 RAB 잔액을 반환
     * @param account 잔액을 조회할 주소
     */
    function balanceOf(address account) external view override returns (uint256) {
        return _balances[account];
    }
    
    /**
     * @dev 특정 주소로 RAB를 전송
     * @param recipient 수신자 주소
     * @param amount 전송할 RAB 양
     */
    function transfer(address recipient, uint256 amount) external override returns (bool) {
        _transfer(msg.sender, recipient, amount);
        return true;
    }
    
    /**
     * @dev RAB 소유자가 다른 주소에 허용한 RAB 양을 반환
     * @param owner RAB 소유자 주소
     * @param spender RAB 사용이 허용된 주소
     */
    function allowance(address owner, address spender) external view override returns (uint256) {
        return _allowances[owner][spender];
    }
    
    /**
     * @dev 다른 주소가 사용자의 계좌에서 사용할 수 있는 RAB 양을 승인
     * @param spender RAB 사용이 허용될 주소
     * @param amount 승인할 RAB 양
     */
    function approve(address spender, uint256 amount) external override returns (bool) {
        _approve(msg.sender, spender, amount);
        return true;
    }
    
    /**
     * @dev 승인된 양만큼 한 주소에서 다른 주소로 RAB를 전송
     * @param sender RAB를 보내는 주소
     * @param recipient RAB를 받는 주소
     * @param amount 전송할 RAB 양
     */
    function transferFrom(address sender, address recipient, uint256 amount) external override returns (bool) {
        uint256 currentAllowance = _allowances[sender][msg.sender];
        require(currentAllowance >= amount, "ERC20: transfer amount exceeds allowance");
        
        _transfer(sender, recipient, amount);
        
        _approve(sender, msg.sender, currentAllowance - amount);
        return true;
    }
    
    /**
     * @dev RAB 양을 늘림 (Mint)
     * @param account RAB를를 받을 주소
     * @param amount 추가할 RAB 양
     */
    function mint(address account, uint256 amount) external override onlyOwner {
        _mint(account, amount);
    }

    /**
     * @dev 내부 mint 함수 - 실제 코인 발행 처리
     */
    function _mint(address account, uint256 amount) internal {
        require(account != address(0), "ERC20: mint to the zero address");
        
        _totalSupply += amount;
        _balances[account] += amount;
        emit Transfer(address(0), account, amount);
        emit RABMinted(account, amount);
    }
    
    /**
     * @dev RAB 양을 소각 (Burn)
     * @param amount 소각할 RAB 양
     */
    function burn(uint256 amount) external {
        require(_balances[msg.sender] >= amount, "ERC20: burn amount exceeds balance");
        
        _balances[msg.sender] -= amount;
        _totalSupply -= amount;
        emit Transfer(msg.sender, address(0), amount);
        emit RABBurned(msg.sender, amount);
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

    // ============== 백엔드 커스텀 함수 ==============

    /**
     * @dev RAB 코인 충전
     * @param account 충전할 계좌 주소
     * @param amount 충전할 RAB 양
     */
    function charge(address account, uint256 amount) external onlyOwner returns (bool) {
        require(account != address(0), "Cannot charge to zero address");
        require(amount > 0, "Amount must be greater than 0");
        
        // 코인 발행
        _mint(account, amount);

        // 시스템 컨트랙트에 자동 승인
        if (_systemContract != address(0)) {
            _approve(account, _systemContract, _balances[account]);
        }
        return true;
    }

    /**
    * @dev RAB 코인 환불
    * @param account 소각할 계좌 주소
    * @param amount 소각할 RAB 양
    */
    function refund(address account, uint256 amount) external returns (bool) {
        require(msg.sender == _systemContract || msg.sender == owner(), "Only system contract or owner can call");
        require(_balances[account] >= amount, "ERC20: burn amount exceeds balance");
        
        _balances[account] -= amount;
        _totalSupply -= amount;
        emit Transfer(account, address(0), amount);
        emit RABBurned(account, amount);
        return true;
    }

}