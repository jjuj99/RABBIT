// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/utils/cryptography/ECDSA.sol";
import "./interfaces/IRabbitCoin.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title RabbitCoin
 * @dev ERC-20 표준을 구현한 RABBIT 코인
 */
contract RabbitCoin is IRabbitCoin, Ownable {
    using ECDSA for bytes32;

    string public constant name = "RABBIT";
    string public constant symbol = "RAB";
    uint8 public constant decimals = 0;
    uint256 private _totalSupply;

    // 각 주소별 잔액을 저장하는 매핑
    mapping(address => uint256) private _balances;
    
    // 특정 주소가 다른 주소에게 허용한 RAB 양을 저장하는 매핑
    mapping(address => mapping(address => uint256)) private _allowances;

    // 각 주소별 nonce 값을 저장하는 매핑
    mapping(address => uint256) private _nonces;

    // EIP-2612 permit을 위한 추가 변수들
    bytes32 public constant DOMAIN_TYPEHASH = keccak256("EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)");
    bytes32 public constant PERMIT_TYPEHASH = keccak256("Permit(address owner,address spender,uint256 value,uint256 nonce,uint256 deadline)");
    string public constant version = "1";
    
    // 생성자. 초기 RAB 공급량을 설정하고 배포자에게 할당
    constructor(uint256 initialSupply) Ownable(msg.sender) {
        _totalSupply = initialSupply;
        _balances[msg.sender] = _totalSupply;
        emit Transfer(address(0), msg.sender, _totalSupply);
    }

    // 현재 주소의 nonce 값을 반환
    function nonces(address owner) external view returns (uint256) {
        return _nonces[owner];
    }

    // EIP-712 도메인 분리자 해시 반환
    function DOMAIN_SEPARATOR() public view returns (bytes32) {
        return keccak256(
            abi.encode(
                DOMAIN_TYPEHASH,
                keccak256(bytes(name)),
                keccak256(bytes(version)),
                block.chainid,
                address(this)
            )
        );
    }

    // ============== ERC-20 기본 함수 ==============

    // RAB의 총 공급량을 반환
    function totalSupply() external view override returns (uint256) {
        return _totalSupply;
    }
    
    // 특정 계좌의 RAB 잔액을 반환
    function balanceOf(address account) external view override returns (uint256) {
        return _balances[account];
    }
    
    // 특정 주소로 RAB를 전송
    function transfer(address recipient, uint256 amount) external override returns (bool) {
        _transfer(msg.sender, recipient, amount);
        return true;
    }
    
    // RAB 소유자가 다른 주소에 허용한 RAB 양을 반환
    function allowance(address owner, address spender) external view override returns (uint256) {
        return _allowances[owner][spender];
    }
    
    // 다른 주소가 사용자의 계좌에서 사용할 수 있는 RAB 양을 승인
    function approve(address spender, uint256 amount) external override returns (bool) {
        _approve(msg.sender, spender, amount);
        return true;
    }
    
    // RAB 양을 늘림 (Mint)
    function mint(address account, uint256 amount) external override onlyOwner {
        require(account != address(0), "ERC20: mint to the zero address");
        
        unchecked {
            _totalSupply += amount;
            _balances[account] += amount;
        }
        emit Transfer(address(0), account, amount);
    }
    
    // RAB 양을 소각 (Burn)
    function burn(uint256 amount) external {
        require(_balances[msg.sender] >= amount, "ERC20: burn amount exceeds balance");
        
        unchecked {
            _balances[msg.sender] -= amount;
            _totalSupply -= amount;
        }
        emit Transfer(msg.sender, address(0), amount);
    }
    
    // 내부 전송 함수
    function _transfer(address sender, address recipient, uint256 amount) internal {
        require(sender != address(0), "ERC20: transfer from the zero address");
        require(recipient != address(0), "ERC20: transfer to the zero address");
        require(_balances[sender] >= amount, "ERC20: transfer amount exceeds balance");
        
        unchecked {
            _balances[sender] -= amount;
            _balances[recipient] += amount;
        }
        emit Transfer(sender, recipient, amount);
    }
    
    // 내부 승인 함수
    function _approve(address owner, address spender, uint256 amount) internal {
        require(owner != address(0), "ERC20: approve from the zero address");
        require(spender != address(0), "ERC20: approve to the zero address");
        
        _allowances[owner][spender] = amount;
        emit Approval(owner, spender, amount);
    }

    // ============== 확장 함수 ==============

    // 승인된 양만큼 한 주소에서 다른 주소로 RAB를 전송
    function transferFrom(address sender, address recipient, uint256 amount) external override returns (bool) {
        uint256 currentAllowance = _allowances[sender][msg.sender];
        require(currentAllowance >= amount, "ERC20: transfer amount exceeds allowance");
        require(_balances[sender] >= amount, "ERC20: transfer amount exceeds balance");

        _transfer(sender, recipient, amount);

        return true;
    }

    // 특정 주소의 RAB를 소각 (Burn)
    function burnFrom(address account, uint256 amount) external {
        uint256 currentAllowance = _allowances[account][msg.sender];
        require(currentAllowance >= amount, "ERC20: burn amount exceeds allowance");
        require(_balances[account] >= amount, "ERC20: burn amount exceeds balance");
        
        unchecked {
            _balances[account] -= amount;
            _totalSupply -= amount;
        }
        
        emit Transfer(account, address(0), amount);
    }

    // EIP-712 서명을 통한 승인
    function permit(
        address owner,
        address spender,
        uint256 value,
        uint256 deadline,
        bytes memory signature
    ) external {
        require(block.timestamp <= deadline, "RABBIT: expired deadline");
        
        bytes32 structHash = keccak256(
            abi.encode(
                PERMIT_TYPEHASH,
                owner,
                spender,
                value,
                _nonces[owner],
                deadline
            )
        );
        
        bytes32 digest = keccak256(
            abi.encodePacked(
                "\x19\x01",
                DOMAIN_SEPARATOR(),
                structHash
            )
        );
        
        address recoveredAddress = ECDSA.recover(digest, signature);
        require(recoveredAddress != address(0) && recoveredAddress == owner, "RABBIT: invalid signature");
        
        _nonces[owner]++;
        _approve(owner, spender, value);
    }
}