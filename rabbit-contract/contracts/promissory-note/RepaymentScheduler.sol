// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@chainlink/contracts/src/v0.8/automation/interfaces/AutomationCompatibleInterface.sol";
import "@openzeppelin/contracts/utils/math/Math.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "../libs/BokkyPooBahsDateTimeLibrary.sol";
import "./interfaces/IPromissoryNote.sol";
import "./interfaces/IRepaymentScheduler.sol";
import "../rabbit-coin/interfaces/ICustomERC20.sol";

/**
 * @title RepaymentScheduler
 * @dev 차용증 NFT의 자동 이자 상환을 관리하는 컨트랙트
 * Chainlink Keeper와 통합하여 정해진 시간에 자동으로 상환을 처리
 */
contract RepaymentScheduler is IRepaymentScheduler, Ownable, AutomationCompatibleInterface {

    address public promissoryNoteAddress;
    address public rabbitCoinAddress;

    // 상환 정보 매핑 (토큰 ID => 상환 정보)
    mapping(uint256 => RepaymentInfo) public repaymentSchedules;
    
    // 활성화된 상환 목록 (Chainlink Keeper가 확인할 목록)
    uint256[] public activeRepayments;
    
    constructor(address _promissoryNoteAddress, address _rabbitCoinAddress) Ownable(msg.sender) {
        promissoryNoteAddress = _promissoryNoteAddress;
        rabbitCoinAddress = _rabbitCoinAddress;
    }

    // 차용증 NFT 생성 후 상환 일정 등록
    function registerRepaymentSchedule(uint256 tokenId) external onlyOwner {
        // 차용증 NFT 컨트랙트에서 메타데이터 조회
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        IPromissoryNote.PromissoryMetadata memory metadata = promissoryNote.getPromissoryMetadata(tokenId);
        
        RepaymentType repayType;
        if (keccak256(bytes(metadata.repayType)) == keccak256(bytes("EPIP"))) {
            repayType = RepaymentType.EPIP;
        } else if (keccak256(bytes(metadata.repayType)) == keccak256(bytes("EPP"))) {
            repayType = RepaymentType.EPP;
        } else if (keccak256(bytes(metadata.repayType)) == keccak256(bytes("BP"))){
            repayType = RepaymentType.BP;
        }
        
        uint256 nextPaymentDate = calculateNextPaymentDate(metadata.mpDt);
        
        // EPIP(원리금 균등 상환)인 경우 고정 납부액 계산
        uint256 fixedPaymentAmount = 0;
        if (repayType == RepaymentType.EPIP) {
            fixedPaymentAmount = calculateFixedPaymentForEPIP(metadata.la, metadata.ir, metadata.lt);
        }

        // 상환 정보 등록
        repaymentSchedules[tokenId] = RepaymentInfo({
            tokenId: tokenId,
            initialPrincipal: metadata.la,    // 초기 원금은 차용 금액과 동일
            remainingPrincipal: metadata.la,  // 남은 원금 초기화
            ir: metadata.ir,                  // 이자율
            mpDt: metadata.mpDt,              // 월 납부일
            nextMpDt: nextPaymentDate,        // 다음 납부일
            totalPayments: metadata.lt,        // 총 납부 횟수 (대출 기간)
            remainingPayments: metadata.lt,    // 남은 납부 횟수 (초기엔 총 납부 횟수와 같음)
            fixedPaymentAmount: fixedPaymentAmount, // EPIP 시 고정 납부액
            repayType: repayType,             // 상환 방식
            drWalletAddress: metadata.drInfo.drWalletAddress, // 채무자 주소
            activeFlag: true                  // 활성화 상태
        });
        
        // 활성 상환 목록에 추가
        activeRepayments.push(tokenId);
        
        emit RepaymentScheduleCreated(tokenId, metadata.la, nextPaymentDate);
    }
    
    // Chainlink Keeper checkUpkeep 함수 - 납부일이 도래한 상환건 확인
    function checkUpkeep(bytes calldata) 
        external 
        view 
        override 
        returns (bool upkeepNeeded, bytes memory performData) 
    {
        // 납부일이 도래한 토큰 ID 목록 생성
        uint256[] memory tokensToProcess = new uint256[](activeRepayments.length);
        uint256 count = 0;
        
        for (uint256 i = 0; i < activeRepayments.length; i++) {
            uint256 tokenId = activeRepayments[i];
            RepaymentInfo memory info = repaymentSchedules[tokenId];
            
            // 상환일이 도래했고 활성 상태인 경우만 처리
            if (info.activeFlag && block.timestamp >= info.nextMpDt) {
                tokensToProcess[count] = tokenId;
                count++;
            }
        }
        
        // 처리할 토큰 ID 목록 반환
        if (count > 0) {
            uint256[] memory result = new uint256[](count);
            for (uint256 i = 0; i < count; i++) {
                result[i] = tokensToProcess[i];
            }
            performData = abi.encode(result);
        }
        
        return (count > 0, performData);
    }
    
    // Chainlink Keeper performUpkeep 함수 - 상환 처리 실행
    function performUpkeep(bytes calldata performData) external override {
        uint256[] memory tokensToProcess = abi.decode(performData, (uint256[]));
        
        for (uint256 i = 0; i < tokensToProcess.length; i++) {
            processRepayment(tokensToProcess[i]);
        }
    }
    
    // 상환 처리 함수
    function processRepayment(uint256 tokenId) public {
        RepaymentInfo storage info = repaymentSchedules[tokenId];
        require(info.activeFlag, "Repayment is not active");
        
        // 현재 NFT 소유자 (채권자) 확인
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        address currentOwner = promissoryNote.ownerOf(tokenId);
        
        // 상환 금액 계산
        uint256 paymentAmount;

        // 마지막 납부일인 경우
        if (info.remainingPayments == 1) {
            // 만기 일시 상환인 경우 원금 + 이자
            if (info.repayType == RepaymentType.BP) {
                uint256 interestAmount = calculateInterestAmount(info);
                paymentAmount = info.remainingPrincipal + interestAmount;
            }
            // EPIP 또는 EPP인 경우 남은 원금 + 이자로 정확히 계산
            else { 
                uint256 interestAmount = calculateInterestAmount(info);
                paymentAmount = info.remainingPrincipal + interestAmount;
            }
        }
        else {
            paymentAmount = calculatePaymentAmount(info);
        }

        
        // 자금 이체 (채무자 -> 채권자)
        ICustomERC20 rabCoin = ICustomERC20(rabbitCoinAddress);
        
        bool transferSuccess = rabCoin.transferFrom(
            info.drWalletAddress,
            currentOwner,
            paymentAmount
        );
        
        require(transferSuccess, "RAB token transfer failed");
        
        // 남은 원금 업데이트
        // 마지막 납부일인 경우 원금을 0으로 설정
        if (info.remainingPayments == 1) { 
            info.remainingPrincipal = 0;
        }
        // 만기 일시 상환이 아닌 경우에만 원금 차감
        else if (info.repayType != RepaymentType.BP) {
            uint256 interestAmount = calculateInterestAmount(info);
            
            require(paymentAmount >= interestAmount, "Underflow in principal calculation");
            uint256 principalAmount = paymentAmount - interestAmount;
            
            info.remainingPrincipal -= principalAmount;
        }
        
        // 남은 납부 횟수 감소
        info.remainingPayments--;
        
        // 다음 납부일 업데이트
        info.nextMpDt = calculateNextPaymentDateFromCurrent(info.nextMpDt, info.mpDt);
        
        emit RepaymentProcessed(tokenId, paymentAmount, info.remainingPrincipal, info.nextMpDt);
        
        // 상환 완료 여부 확인
        if (info.remainingPrincipal == 0 || info.remainingPayments == 0) {
            completeRepayment(tokenId);
        }
    }
    
    // 상환 완료 처리 함수
    function completeRepayment(uint256 tokenId) internal {
        // 상환 정보 비활성화
        repaymentSchedules[tokenId].activeFlag = false;
        
        // 활성 상환 목록에서 제거
        removeFromActiveRepayments(tokenId);
        
        // NFT 소각 (실패하면 오류 발생 - 권한이 없거나 NFT가 이미 소각된 경우)
        try IPromissoryNote(promissoryNoteAddress).burn(tokenId) {
            emit RepaymentCompleted(tokenId);
        } catch Error(string memory reason) {
            emit RepaymentCompleted(tokenId);
            emit BurnFailed(tokenId, reason);
        } catch {
            emit RepaymentCompleted(tokenId);
            emit BurnFailed(tokenId, "Unknown error");
        }
    }

    // 상환 관련 데이터 정리 함수 (채권자가 직접 NFT를 소각한 경우)
    function cleanupRepaymentData(uint256 tokenId) external {
        // NFT가 이미 소각되었는지 확인
        try IPromissoryNote(promissoryNoteAddress).ownerOf(tokenId) returns (address) {
            revert("NFT still exists");
        } catch {
            // NFT가 소각된 경우 상환 관련 데이터 정리
            if (repaymentSchedules[tokenId].activeFlag) {
                repaymentSchedules[tokenId].activeFlag = false;
                removeFromActiveRepayments(tokenId);
                emit RepaymentCompleted(tokenId);
            }
        }
    }
    
    // 활성 상환 목록에서 토큰 ID 제거
    function removeFromActiveRepayments(uint256 tokenId) internal {
        uint256 length = activeRepayments.length;
        uint256 index = length;
        
        // 제거할 항목의 인덱스 찾기
        for (uint256 i = 0; i < length; i++) {
            if (activeRepayments[i] == tokenId) {
                index = i;
                break;
            }
        }
        
        // 항목이 존재하면 제거
        if (index < length) {
            // 마지막 항목으로 현재 항목 대체 후 마지막 항목 제거 (가스 효율적)
            activeRepayments[index] = activeRepayments[length - 1];
            activeRepayments.pop();
        }
    }
    
    // 상환 금액 계산 함수
    function calculatePaymentAmount(RepaymentInfo memory info) internal pure returns (uint256) {
        // 원리금 균등 상환 (PMT 공식)
        if (info.repayType == RepaymentType.EPIP) {
            return info.fixedPaymentAmount;
        }

        // 원금 균등 상환
        else if (info.repayType == RepaymentType.EPP) {
            uint256 principalAmount = info.initialPrincipal / info.totalPayments;
            uint256 interestAmount = calculateInterestAmount(info);
            return principalAmount + interestAmount;
        }

        // 만기 일시 상환
        else if (info.repayType == RepaymentType.BP) { 
            uint256 interestAmount = calculateInterestAmount(info);
            return (info.remainingPayments == 1) ? interestAmount + info.remainingPrincipal : interestAmount;
        }
        return 0;
    }

    // PMT 계산 함수
    function calculateFixedPaymentForEPIP(
        uint256 principal, 
        uint256 annualRate, 
        uint256 months
    ) internal pure returns (uint256) {
        if (annualRate == 0) return principal / months;
        uint256 precision = 1e12;
        uint256 monthlyRate = (annualRate * precision) / 12 / 10000;
        uint256 onePlusRateToN = calculatePower(precision + monthlyRate, months, precision);
        uint256 numerator = (principal * monthlyRate * onePlusRateToN) / (precision * precision);
        uint256 denominator = onePlusRateToN - precision;
        return numerator / denominator;
    }

    // 지수 계산 함수
    function calculatePower(uint256 base, uint256 exponent, uint256 precision) internal pure returns (uint256) {
        if (exponent == 0) {
            return precision;
        }

        uint256 result = precision;
        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = (result * base) / precision;
            }
            base = (base * base) / precision;
            exponent /= 2;
        }
        return result;
    }
    
     // 이자 금액 계산 함수
    function calculateInterestAmount(RepaymentInfo memory info) internal pure returns (uint256) {
        return (info.remainingPrincipal * info.ir) / (12 * 10000);
    }
    
    // 다음 납부일 계산 함수 (초기 등록용)
    function calculateNextPaymentDate(uint256 dayOfMonth) internal view returns (uint256) {
        require(dayOfMonth > 0 && dayOfMonth <= 31, "Invalid day of month");
        
        // 현재 타임스탬프에서 연, 월, 일 추출
        uint256 currentYear;
        uint256 currentMonth;
        uint256 currentDay;
        (currentYear, currentMonth, currentDay) = BokkyPooBahsDateTimeLibrary.timestampToDate(block.timestamp);
        
        // 다음 납부일 계산
        uint256 nextYear = currentYear;
        uint256 nextMonth = currentMonth;
        
        // 현재 일이 납부일 이후라면 다음 달로 설정
        if (currentDay > dayOfMonth) {
            if (currentMonth == 12) {
                nextMonth = 1;
                nextYear++;
            } else {
                nextMonth++;
            }
        }
        
        // 해당 월에 유효한 날짜 계산
        uint256 targetTimestamp = BokkyPooBahsDateTimeLibrary.timestampFromDate(nextYear, nextMonth, 1);
        uint256 daysInMonth = BokkyPooBahsDateTimeLibrary.getDaysInMonth(targetTimestamp);
        uint256 validDay = dayOfMonth <= daysInMonth ? dayOfMonth : daysInMonth;
        
        // 타임스탬프로 변환
        return BokkyPooBahsDateTimeLibrary.timestampFromDate(nextYear, nextMonth, validDay);
    }

    // 현재 납부일로부터 다음 납부일 계산 함수
    function calculateNextPaymentDateFromCurrent(uint256 currentPaymentDate, uint256 dayOfMonth) internal pure returns (uint256) {
        require(dayOfMonth > 0 && dayOfMonth <= 31, "Invalid day of month");
        
        // 현재 타임스탬프에서 연, 월 추출
        uint256 currentYear;
        uint256 currentMonth;
        uint256 currentDay;
        (currentYear, currentMonth, currentDay) = BokkyPooBahsDateTimeLibrary.timestampToDate(currentPaymentDate);
        
        // 다음 납부일 계산 (한 달 후)
        uint256 nextYear = currentYear;
        uint256 nextMonth = currentMonth + 1;
        
        if (nextMonth > 12) {
            nextMonth = 1;
            nextYear++;
        }
        
        // 해당 월에 유효한 날짜 계산
        uint256 targetTimestamp = BokkyPooBahsDateTimeLibrary.timestampFromDate(nextYear, nextMonth, 1);
        uint256 daysInMonth = BokkyPooBahsDateTimeLibrary.getDaysInMonth(targetTimestamp);
        uint256 validDay = dayOfMonth <= daysInMonth ? dayOfMonth : daysInMonth;
        
        // 타임스탬프로 변환
        return BokkyPooBahsDateTimeLibrary.timestampFromDate(nextYear, nextMonth, validDay);
    }
    
    // 상환 정보 조회 함수
    function getRepaymentInfo(uint256 tokenId) external view returns (RepaymentInfo memory) {
        return repaymentSchedules[tokenId];
    }
    
    // 활성화된 상환 목록 조회 함수
    function getActiveRepayments() external view returns (uint256[] memory) {
        return activeRepayments;
    }
    
    // 상환 정보 수동 업데이트 함수 (관리자 전용)
    function updateRepaymentInfo(
        uint256 tokenId, 
        uint256 remainingPrincipal, 
        uint256 remainingPayments, 
        uint256 nextPaymentDate
    ) external onlyOwner {
        require(repaymentSchedules[tokenId].activeFlag, "Repayment is not active");
        
        RepaymentInfo storage info = repaymentSchedules[tokenId];
        info.remainingPrincipal = remainingPrincipal;
        info.remainingPayments = remainingPayments;
        info.nextMpDt = nextPaymentDate;
        
        emit RepaymentScheduleCreated(tokenId, remainingPrincipal, nextPaymentDate);
    }
    
    // 컨트랙트 주소 업데이트 함수 (관리자 전용)
    function updateContractAddresses(address _promissoryNoteAddress, address _rabbitCoinAddress) external onlyOwner {
        promissoryNoteAddress = _promissoryNoteAddress;
        rabbitCoinAddress = _rabbitCoinAddress;
    }
}