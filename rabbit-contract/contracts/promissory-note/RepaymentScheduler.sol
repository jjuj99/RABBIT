// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@chainlink/contracts/src/v0.8/automation/interfaces/AutomationCompatibleInterface.sol";
import "@openzeppelin/contracts/utils/math/Math.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "../libs/BokkyPooBahsDateTimeLibrary.sol";
import "./interfaces/IPromissoryNote.sol";
import "./interfaces/IRepaymentScheduler.sol";
import "../rabbit-coin/interfaces/IRabbitCoin.sol";

/**
 * @title RepaymentScheduler
 * @dev 차용증 NFT의 자동 이자 상환을 관리하는 컨트랙트
 * Chainlink Keeper와 통합하여 정해진 시간에 자동으로 상환을 처리
 * 연체 관리 기능 추가
 */
contract RepaymentScheduler is IRepaymentScheduler, Ownable, AutomationCompatibleInterface {

    address public promissoryNoteAddress;
    address public rabbitCoinAddress;

    // 최대 연체 이자율 (20%)
    uint256 public constant MAX_OVERDUE_INTEREST_RATE = 2000;

    // 상환 정보 매핑 (토큰 ID => 상환 정보)
    mapping(uint256 => RepaymentInfo) public repaymentSchedules;
    
    // 활성화된 상환 목록 (Chainlink Keeper가 확인할 목록)
    uint256[] public activeRepayments;
    
    constructor(address _promissoryNoteAddress, address _rabbitCoinAddress) Ownable(msg.sender) {
        promissoryNoteAddress = _promissoryNoteAddress;
        rabbitCoinAddress = _rabbitCoinAddress;
    }

    function setRabbitCoinAddress(address _rabbitCoinAddress) external onlyOwner {
        rabbitCoinAddress = _rabbitCoinAddress;
    }

    function setPromissoryNoteAuctionAddress(address _promissoryNoteAddress) external onlyOwner {
        promissoryNoteAddress = _promissoryNoteAddress;
    }

    // 차용증 NFT 생성 후 상환 일정 등록
    function registerRepaymentSchedule(uint256 tokenId) external {
        // 차용증 NFT 컨트랙트에서 메타데이터 조회
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        IPromissoryNote.PromissoryMetadata memory metadata = promissoryNote.getPromissoryMetadata(tokenId);
        
        uint256 nextPaymentDate = calculateNextPaymentDate(metadata.mpDt);
        
        // EPIP인 경우 고정 납부액 계산
        uint256 fixedPaymentAmount = 0;
       if (keccak256(bytes(metadata.repayType)) == keccak256(bytes("EPIP"))) {
            fixedPaymentAmount = calculateFixedPaymentForEPIP(metadata.la, metadata.ir, metadata.lt);
        }

        // 상환 정보 등록
        repaymentSchedules[tokenId] = RepaymentInfo({
            tokenId: tokenId,
            initialPrincipal: metadata.la,      // 초기 원금
            remainingPrincipal: metadata.la,    // 남은 원금
            ir: metadata.ir,                    // 연이자율 (1% = 100)
            dir: metadata.dir,                  // 연체 이자율 (1% = 100)
            mpDt: metadata.mpDt,                // 월 납부일
            nextMpDt: nextPaymentDate,          // 다음 납부일
            totalPayments: metadata.lt,         // 총 납부 횟수
            remainingPayments: metadata.lt,     // 남은 납부 횟수
            fixedPaymentAmount: fixedPaymentAmount, // EPIP 시 고정 납부액
            repayType: metadata.repayType,      // 상환 방식
            drWalletAddress: metadata.drInfo.drWalletAddress,
            activeFlag: true,                   // 활성 상태
            
            overdueInfo: OverdueInfo({
                overdueFlag: false,                 // 연체 상태
                overdueStartDate: 0,                // 연체 시작일 (타임스탬프)
                overdueDays: 0,                     // 연체 일수
                aoi: 0,                             // 누적 연체 이자
                defCnt: 0,                          // 현재 연체 횟수
                accel: metadata.accel,              // 기한이익상실 횟수
                currentIr: metadata.ir,             // 현재 적용 이자율
                totalDefCnt: 0                      // 총 누적 연체 횟수 (상환 후에도 유지)
            })
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
            if (info.activeFlag && (block.timestamp >= info.nextMpDt || info.overdueInfo.overdueFlag)) {
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
            uint256 tokenId = tokensToProcess[i];
            updateOverdueStatus(tokenId); // 연체 상태 업데이트
            processRepayment(tokenId); // 상환 처리
        }
    }

    // 연체 상태 업데이트 함수
    function updateOverdueStatus(uint256 tokenId) internal {
        RepaymentInfo storage info = repaymentSchedules[tokenId];
        
        if (!info.activeFlag) return;
        
        // 현재 시간이 다음 납부일을 지났고 아직 연체 상태가 아닌 경우
        if (block.timestamp > info.nextMpDt && !info.overdueInfo.overdueFlag) {
            // 연체 상태로 변경
            info.overdueInfo.overdueFlag = true;
            info.overdueInfo.overdueStartDate = info.nextMpDt;
            
            // 연체 횟수 증가
            info.overdueInfo.defCnt++;
            info.overdueInfo.totalDefCnt++;

            emit RepaymentOverdue(tokenId, info.nextMpDt, info.overdueInfo.totalDefCnt, info.overdueInfo.aoi);
            
            // 기한이익상실 횟수 초과 시 최대 이자율 적용
            if (info.overdueInfo.defCnt >= info.overdueInfo.accel) {
                info.overdueInfo.currentIr = MAX_OVERDUE_INTEREST_RATE;
                emit AccelReached(tokenId, MAX_OVERDUE_INTEREST_RATE);
            } else {
                // 일반 연체 시 연체 이자율 적용
                info.overdueInfo.currentIr = info.dir;
            }
        }
        
        // 연체 중인 경우 연체 일수 및 누적 연체 이자 계산
        if (info.overdueInfo.overdueFlag) {
            // 연체 일수 계산
            uint256 previousOverdueDays = info.overdueInfo.overdueDays;
            info.overdueInfo.overdueDays = (block.timestamp - info.overdueInfo.overdueStartDate) / 86400; // 일 단위로 계산 (86400초 = 1일)
            
            // 새로 추가된 연체 일수에 대한 연체 이자 계산
            if (info.overdueInfo.overdueDays > previousOverdueDays) {
                uint256 newOverdueDays = info.overdueInfo.overdueDays - previousOverdueDays;
                
                // 일별 연체 이자 계산 및 누적
                uint256 dailyOverdueInterest = (info.remainingPrincipal * info.overdueInfo.currentIr) / (365 * 10000);
                info.overdueInfo.aoi += dailyOverdueInterest * newOverdueDays;
                
                emit OverdueInterestAccumulated(tokenId, dailyOverdueInterest * newOverdueDays, info.overdueInfo.aoi);
            }
        }
    }
    
    // 상환 처리 함수
    function processRepayment(uint256 tokenId) public {
        RepaymentInfo storage info = repaymentSchedules[tokenId];
        require(info.activeFlag, "Repayment is not active");
        
        // 현재 NFT 소유자 (채권자) 확인
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        address currentOwner = promissoryNote.getLatestCreditorAddress(tokenId);
        
        // 상환 금액 계산
        uint256 paymentAmount;
        uint256 regularPaymentAmount = 0;
        uint256 overdueAmount = 0;

        // 정규 납부액 계산
        if (info.remainingPayments == 1) { // 마지막 납부일인 경우
            uint256 interestAmount = calculateInterestAmount(info);
            regularPaymentAmount = info.remainingPrincipal + interestAmount;
        } else {
            regularPaymentAmount = calculatePaymentAmount(info);
        }

        // 연체 상태인 경우 누적 연체 이자 추가
        if (info.overdueInfo.overdueFlag) {
            overdueAmount = info.overdueInfo.aoi;
            paymentAmount = regularPaymentAmount + overdueAmount;
        } else {
            paymentAmount = regularPaymentAmount;
        }

        IRabbitCoin rabCoin = IRabbitCoin(rabbitCoinAddress);
        
        // 채무자 잔액 부족 시 이벤트 발행
        uint256 drBalance = rabCoin.balanceOf(info.drWalletAddress);
        if (drBalance < paymentAmount) {
            emit InsufficientBalance(tokenId, info.drWalletAddress, paymentAmount, drBalance);
            return; // 함수 종료, 트랜잭션은 성공으로 처리됨
        }

        // 자금 이체 (채무자 -> 채권자)
        bool transferSuccess = rabCoin.transferFrom(
            info.drWalletAddress,
            currentOwner,
            paymentAmount
        );
        
        require(transferSuccess, "RAB token transfer failed");

        // 연체 상태 해제
        if (info.overdueInfo.overdueFlag) {
            info.overdueInfo.overdueFlag = false;
            info.overdueInfo.overdueStartDate = 0;
            info.overdueInfo.overdueDays = 0;
            info.overdueInfo.aoi = 0;
            info.overdueInfo.currentIr = info.ir;
            info.overdueInfo.defCnt = 0;
            
            emit OverdueResolved(tokenId, overdueAmount, info.drWalletAddress, currentOwner);
        }

        // 남은 원금 업데이트
        // 마지막 납부일인 경우 원금을 0으로 설정
        if (info.remainingPayments == 1) { 
            info.remainingPrincipal = 0;
        }
        // 만기 일시 상환이 아닌 경우에만 원금 차감
        else if (keccak256(bytes(info.repayType)) != keccak256(bytes("BP"))) {
            uint256 interestAmount = calculateInterestAmount(info);

            // 방어: 고정 납부금보다 이자가 크면 → 이자만 납부, 원금 상환 없음
            uint256 principalAmount = 0;
            if (paymentAmount > interestAmount) {
                principalAmount = paymentAmount - interestAmount;

                // 남은 원금보다 클 경우 절단
                if (principalAmount > info.remainingPrincipal) {
                    principalAmount = info.remainingPrincipal;
                }

                info.remainingPrincipal -= principalAmount;
            } else {
                interestAmount = paymentAmount;
            }
        }
        
        // 남은 납부 횟수 감소
        info.remainingPayments--;

        // 마지막 납부가 아닌 경우에만 다음 납부일 업데이트
        if (info.remainingPayments > 0) {
            info.nextMpDt = calculateNextPaymentDateFromCurrent(info.nextMpDt, info.mpDt);
        }
        
        emit RepaymentProcessed(tokenId, paymentAmount, info.remainingPrincipal, info.nextMpDt, info.drWalletAddress, currentOwner);
        
        // 상환 완료 NFT 처리
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

        // NFT 소각 전 burn 권한 확인 및 부여
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        promissoryNote.addBurnAuthorization(address(this));
        
        // NFT 소각
        IPromissoryNote(promissoryNoteAddress).burn(tokenId);
        emit RepaymentCompleted(tokenId);
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
        if (keccak256(bytes(info.repayType)) == keccak256(bytes("EPIP"))) {
            return info.fixedPaymentAmount;
        }

        // 원금 균등 상환
        else if (keccak256(bytes(info.repayType)) == keccak256(bytes("EPP"))) {
            uint256 principalAmount = info.initialPrincipal / info.totalPayments;
            uint256 interestAmount = calculateInterestAmount(info);
            return principalAmount + interestAmount;
        }

        // 만기 일시 상환
        else if (keccak256(bytes(info.repayType)) == keccak256(bytes("BP"))) { 
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
        // 기본 예외 처리: 이자율이 0이거나 개월 수가 0인 경우
        if (annualRate == 0 || months == 0) return months == 0 ? principal : principal / months;
        
        // 최소 납부액 보장 (원금을 총 납부 횟수로 나눈 값보다 커야 함)
        uint256 minPayment = (principal / months) + 1;
        
        // 더 높은 정밀도 사용
        uint256 precision = 1e18;
        
        // 월 이자율 계산 (연이자율 / 12 / 100) annualRate가 500이면 5%를 의미
        uint256 monthlyRate = (annualRate * precision) / 1200 / 100;
        
        // 월이자율이 너무 작아 0이 되는 것 방지
        if (monthlyRate == 0) monthlyRate = 1; // 최소값 설정
        
        // (1 + r)^n 계산
        uint256 onePlusRateToN = calculatePower(precision + monthlyRate, months, precision);
        
        // 분모가 0에 가까워지는 것 방지
        uint256 denominator = onePlusRateToN - precision;
        if (denominator < precision / 1000) denominator = precision / 1000; // 최소 0.1% 보장
        
        // PMT = P × r × (1 + r)^n / [(1 + r)^n - 1]
        uint256 numerator = (principal * monthlyRate * onePlusRateToN) / precision;
        uint256 payment = numerator / denominator;
        
        // 최소 납부액 보장
        return payment > minPayment ? payment : minPayment;
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
    
     // 이자 금액 계산 함수 (연체X)
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

    // 중도 상환 수수료 계산 함수
    function getEarlyRepaymentFee(
        uint256 tokenId,
        uint256 paymentAmount
    ) external view returns (uint256 feeAmount) {
        RepaymentInfo memory info = repaymentSchedules[tokenId];
        require(info.activeFlag, "Repayment is not active");
        
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        IPromissoryNote.PromissoryMetadata memory metadata = promissoryNote.getPromissoryMetadata(tokenId);
        
        // 입력된 금액이 남은 원금보다 크면 남은 원금에 대해서만 수수료 계산
        uint256 actualPrincipal = paymentAmount;
        if (actualPrincipal > info.remainingPrincipal) {
            actualPrincipal = info.remainingPrincipal;
        }
        
        // 중도 상환 수수료 계산 (전액/부분 상환 모두 동일하게 적용)
        return (actualPrincipal * metadata.earlyPayFee) / 10000;
    }

    // 중도 상환 처리 함수
    function processEarlyRepayment(
        uint256 tokenId,
        uint256 paymentAmount,
        uint256 feeAmount
    ) external {
        RepaymentInfo storage info = repaymentSchedules[tokenId];
        require(info.activeFlag, "Repayment is not active");

        // 연체 상태 확인 - 연체 중인 경우 중도 상환 불가
        require(!info.overdueInfo.overdueFlag, "Cannot process early repayment while loan is overdue");
        
        // NFT 메타데이터에서 중도 상환 가능 여부 확인
        IPromissoryNote promissoryNote = IPromissoryNote(promissoryNoteAddress);
        IPromissoryNote.PromissoryMetadata memory metadata = promissoryNote.getPromissoryMetadata(tokenId);
        require(metadata.earlyPayFlag, "Early repayment not allowed for this loan");
        
        // 원금 검증 및 조정
        bool isFullRepayment = false;
        uint256 actualPrincipal = paymentAmount;
        if (actualPrincipal >= info.remainingPrincipal) {
            actualPrincipal = info.remainingPrincipal;
            isFullRepayment = true;
        }
        
        // 수수료 검증
        uint256 expectedFee = (actualPrincipal * metadata.earlyPayFee) / 10000;
        require(feeAmount == expectedFee, "Fee amount mismatch");
        
        // 현재 NFT 채권자 확인
        address currentOwner = promissoryNote.getLatestCreditorAddress(tokenId);
        
        // 상환 처리를 위한 총 금액 (원금 + 수수료)
        uint256 totalAmount = actualPrincipal + feeAmount;
        
        // 채무자 잔액 확인
        IRabbitCoin rabCoin = IRabbitCoin(rabbitCoinAddress);
        uint256 drBalance = rabCoin.balanceOf(info.drWalletAddress);

        if (drBalance < totalAmount) {
            emit InsufficientBalance(
                tokenId, 
                info.drWalletAddress, 
                totalAmount, 
                drBalance
            );
            return; // 잔액 부족 시 종료
        }
        
        // 자금 이체 (채무자 -> 채권자)
        bool transferSuccess = rabCoin.transferFrom(
            info.drWalletAddress,
            currentOwner,
            totalAmount // 원금 + 수수료
        );
        require(transferSuccess, "Transfer failed");
        
        // 상환 정보 업데이트
        if (isFullRepayment) {
            // 전액 상환 시 원금 0,납부 횟수 0 설정
            info.remainingPrincipal = 0;
            info.remainingPayments = 0;
        } else {
            // 부분 상환 시 상환 금액만큼 원금 감소
            info.remainingPrincipal -= actualPrincipal;
            
            // EPIP(원리금 균등 상환)인 경우 새로운 고정 납부액 계산
            if (keccak256(bytes(info.repayType)) == keccak256(bytes("EPIP")) && info.remainingPayments > 0) {
                info.fixedPaymentAmount = calculateFixedPaymentForEPIP(
                    info.remainingPrincipal,
                    info.ir,
                    info.remainingPayments
                );
            }
        }
        
        // 원금 상환 이벤트 발행
        emit EarlyRepaymentPrincipal(tokenId, actualPrincipal, info.remainingPrincipal, isFullRepayment);
        
        // 수수료 이벤트 발행
        emit EarlyRepaymentFee(tokenId, feeAmount);
        
        // 전액 상환 시 상환 완료 처리
        if (isFullRepayment) {
            promissoryNote.addBurnAuthorization(address(this));
            completeRepayment(tokenId);
        }
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
    
    // 연체 정보 수동 업데이트 함수 (관리자 전용)
    function updateOverdueInfo(
        uint256 tokenId,
        bool overdueFlag,
        uint256 overdueStartDate,
        uint256 overdueDays,
        uint256 aoi,
        uint256 defCnt,
        uint256 currentIr,
        uint256 totalDefCnt
    ) external onlyOwner {
        require(repaymentSchedules[tokenId].activeFlag, "Repayment is not active");

        // UTC 시간을 KST(UTC+9)로 변환 (9시간 = 32400초 추가)
        uint256 currentKstTimestamp = block.timestamp + 32400;

        // 날짜 유효성 검사 - 미래 불가능
        require(overdueStartDate <= currentKstTimestamp, "Overdue start date cannot be in the future");
        
        // 이자율 검사 - 최대 연체 이자율 이하하
        require(currentIr <= MAX_OVERDUE_INTEREST_RATE, "Interest rate exceeds maximum allowed rate");
        
        // 연체 일수와 시작일 일관성 검사
        if (overdueFlag && overdueStartDate > 0) {
            // KST 기준으로 정확한 일수 계산
            uint256 calculatedDays = (currentKstTimestamp - overdueStartDate) / 86400;
            require(overdueDays == calculatedDays, "Overdue days must match exactly with calculated days");
        }

        RepaymentInfo storage info = repaymentSchedules[tokenId];
        info.overdueInfo.overdueFlag = overdueFlag;
        info.overdueInfo.overdueStartDate = overdueStartDate;
        info.overdueInfo.overdueDays = overdueDays;
        info.overdueInfo.aoi = aoi;
        info.overdueInfo.defCnt = defCnt;
        info.overdueInfo.currentIr = currentIr;
        info.overdueInfo.totalDefCnt = totalDefCnt;
        
        emit OverdueInfoUpdated(tokenId, overdueFlag, aoi);
    }

    // 컨트랙트 주소 업데이트 함수 (관리자 전용)
    function updateContractAddresses(address _promissoryNoteAddress, address _rabbitCoinAddress) external onlyOwner {
        promissoryNoteAddress = _promissoryNoteAddress;
        rabbitCoinAddress = _rabbitCoinAddress;
    }
}