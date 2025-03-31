// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title IRepaymentScheduler
 * @dev 차용증 NFT의 자동 이자 상환을 관리하는 컨트랙트의 인터페이스
 */
interface IRepaymentScheduler {
    enum RepaymentType { EPIP, EPP, BP } // 원리금균등상환, 원금균등상환, 만기일시상환

    struct RepaymentInfo {
        uint256 tokenId;            // 차용증 NFT 토큰 ID
        uint256 initialPrincipal;   // 초기 원금
        uint256 remainingPrincipal; // 남은 원금
        uint256 ir;                 // 연이자율 (1% = 100)
        uint256 dir;                // 연체 이자율 (1% = 100)  
        uint256 mpDt;               // 월 납부일
        uint256 nextMpDt;           // 다음 납부일
        uint256 totalPayments;      // 총 납부 횟수
        uint256 remainingPayments;  // 남은 납부 횟수
        uint256 fixedPaymentAmount; // EPIP 시 고정 납부액
        RepaymentType repayType;    // 상환 방식
        address drWalletAddress;    // 채무자 주소
        bool activeFlag;            // 활성 상태 여부

        // 연체 관리 관련 필드
        bool overdueFlag;            // 연체 상태
        uint256 overdueStartDate;    // 연체 시작일 (타임스탬프)
        uint256 overdueDays;         // 연체 일수
        uint256 aoi;                 // 누적 연체 이자
        uint256 defCnt;              // 현재 연체 횟수
        uint256 accel;               // 기한이익상실 횟수
        uint256 currentIr;           // 현재 적용 이자율
        uint256 totalDefCnt;         // 총 연체 횟수
    }

    // 상환 관련 이벤트
    event RepaymentScheduleCreated(uint256 tokenId, uint256 remainingPrincipal, uint256 nextMpDt); // 상환 일정 생성
    event RepaymentProcessed(uint256 tokenId, uint256 amount, uint256 remainingPrincipal, uint256 nextMpDt); 
    event RepaymentCompleted(uint256 tokenId); // 상환 완료

    // IRepaymentScheduler.sol에 추가할 이벤트
    event EarlyRepaymentPrincipal(uint256 indexed tokenId, uint256 principalAmount, uint256 remainingPrincipal, bool isFullRepayment);
    event EarlyRepaymentFee(uint256 indexed tokenId, uint256 feeAmount);

    // 채무자 잔액 부족 이벤트
    event InsufficientBalance(uint256 tokenId, address debtor, uint256 requiredAmount, uint256 currentBalance);

    // 연체 관련 이벤트
    event RepaymentOverdue(uint256 indexed tokenId, uint256 overdueStartDate, uint256 totalDefaultCount);
    event OverdueInterestAccumulated(uint256 indexed tokenId, uint256 newInterest, uint256 totalAccumulatedInterest);
    event OverdueResolved(uint256 indexed tokenId, uint256 paidOverdueAmount);
    event AccelReached(uint256 indexed tokenId, uint256 maxInterestRate);
    event OverdueInfoUpdated(uint256 indexed tokenId, bool isOverdue, uint256 accumulatedOverdueInterest);

    // NFT 소각 실패 이벤트
    event BurnFailed(uint256 tokenId, string reason);

    /**
     * @dev 차용증 NFT 생성 후 상환 일정 등록
     * @param tokenId 차용증 NFT 토큰 ID
     */
    function registerRepaymentSchedule(uint256 tokenId) external;

    /**
     * @dev 상환 처리 함수
     * @param tokenId 처리할 차용증 NFT 토큰 ID
     */
    function processRepayment(uint256 tokenId) external;

    /**
     * @dev 상환 관련 데이터 정리 함수 (채권자가 직접 NFT를 소각한 경우)
     * @param tokenId 정리할 차용증 NFT 토큰 ID
     */
    function cleanupRepaymentData(uint256 tokenId) external;

    /**
     * @dev 상환 정보 조회 함수
     * @param tokenId 조회할 차용증 NFT 토큰 ID
     * @return 상환 정보
     */
    function getRepaymentInfo(uint256 tokenId) external view returns (RepaymentInfo memory);

    /**
     * @dev 활성화된 상환 목록 조회 함수
     * @return 활성화된 상환 목록 (차용증 NFT 토큰 ID 배열)
     */
    function getActiveRepayments() external view returns (uint256[] memory);

    /**
    * @dev 중도 상환 수수료 계산 함수
    * @param tokenId 조회할 차용증 NFT 토큰 ID
    * @param paymentAmount 중도 상환할 금액액
    * @return feeAmount 중도 상환 수수료
    */
    function getEarlyRepaymentFee(uint256 tokenId, uint256 paymentAmount) external view returns (uint256 feeAmount);

    /**
    * @dev 중도 상환 처리 함수
    * @param tokenId 처리할 차용증 NFT 토큰 ID
    * @param paymentAmount 중도 상환할 금액
    * @param feeAmount 중도 상환 수수료
    */
    function processEarlyRepayment(uint256 tokenId, uint256 paymentAmount, uint256 feeAmount) external;

    /**
     * @dev 상환 정보 수동 업데이트 함수 (관리자 전용)
     * @param tokenId 업데이트할 차용증 NFT 토큰 ID
     * @param remainingPrincipal 변경한 남은 원금
     * @param remainingPayments 변경한 남은 납부 횟수
     * @param nextPaymentDate 변경한 다음 납부일
     */
    function updateRepaymentInfo(
        uint256 tokenId, 
        uint256 remainingPrincipal, 
        uint256 remainingPayments, 
        uint256 nextPaymentDate
    ) external;

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
    ) external;

    /**
     * @dev 컨트랙트 주소 업데이트 함수 (관리자 전용)
     * @param _promissoryNoteAddress 새 차용증 NFT 컨트랙트 주소
     * @param _rabbitCoinAddress 새 RABBIT 코인 컨트랙트 주소
     */
    function updateContractAddresses(address _promissoryNoteAddress, address _rabbitCoinAddress) external;
}