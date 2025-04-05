// scripts/register_repayment_schedule.js
const { ethers } = require("hardhat");
require("dotenv").config();

// RepaymentScheduler 컨트랙트 ABI (필요한 함수만 포함)
const REPAYMENT_SCHEDULER_ABI = [
    "function registerRepaymentSchedule(uint256 tokenId) external",
    "function getRepaymentInfo(uint256 tokenId) external view returns (uint256 tokenId, uint256 initialPrincipal, uint256 remainingPrincipal, uint256 ir, uint256 dir, uint256 mpDt, uint256 nextMpDt, uint256 totalPayments, uint256 remainingPayments, uint256 fixedPaymentAmount, string repayType, address drWalletAddress, bool activeFlag, bool overdueFlag, uint256 overdueStartDate, uint256 overdueDays, uint256 aoi, uint256 defCnt, uint256 accel, uint256 currentIr, uint256 totalDefCnt)"
];

// Sepolia 테스트넷에 배포된 컨트랙트 주소
const REPAYMENT_SCHEDULER_ADDRESS = process.env.REPAYMENT_SCHEDULER_ADDRESS;

async function main() {
    // 상환 정보를 등록하려는 차용증 NFT의 ID
    const tokenId = 3;

    // 컨트랙트 연결
    const privateKey = process.env.PRIVATE_KEY;
    const provider = new ethers.JsonRpcProvider(`https://sepolia.infura.io/v3/${process.env.INFURA_API_KEY}`);
    const wallet = new ethers.Wallet(privateKey, provider);

    console.log("스크립트 실행 계정:", wallet.address);

    const repaymentSchedulerContract = new ethers.Contract(
        REPAYMENT_SCHEDULER_ADDRESS,
        REPAYMENT_SCHEDULER_ABI,
        wallet
    );

    console.log(`tokenId ${tokenId}에 대한 상환 일정 등록 시작...`);

    try {
        // registerRepaymentSchedule 함수 호출
        const tx = await repaymentSchedulerContract.registerRepaymentSchedule(tokenId);
        console.log(`트랜잭션 전송됨. 해시: ${tx.hash}`);

        // 트랜잭션 확인 대기
        const receipt = await tx.wait();
        console.log(`성공: tokenId ${tokenId}의 상환 일정이 등록되었습니다.`);
        console.log(`트랜잭션 해시: ${receipt.hash}`);
        console.log(`가스 사용량: ${receipt.gasUsed}`);
        console.log(`Etherscan에서 확인: https://sepolia.etherscan.io/tx/${receipt.hash}`);

        // 상환 정보 확인
        try {
            // getRepaymentInfo는 여러 값을 포함하는 배열을 반환
            const repaymentInfo = await repaymentSchedulerContract.getRepaymentInfo(tokenId);

            // 결과 로깅 (원시 데이터)
            console.log("등록된 상환 정보 (raw):", repaymentInfo);

            // 구조화된 형태로 데이터 출력
            console.log("등록된 상환 정보:");
            console.log(`- 토큰 ID: ${repaymentInfo[0]}`);
            console.log(`- 초기 원금: ${repaymentInfo[1]}`);
            console.log(`- 남은 원금: ${repaymentInfo[2]}`);
            console.log(`- 이자율: ${repaymentInfo[3] / 100}%`);
            console.log(`- 연체이자율: ${repaymentInfo[4] / 100}%`);
            console.log(`- 납부일: 매월 ${repaymentInfo[5]}일`);
            console.log(`- 다음 납부일: ${new Date(Number(repaymentInfo[6]) * 1000).toISOString().split('T')[0]}`);
            console.log(`- 총 납부 횟수: ${repaymentInfo[7]}`);
            console.log(`- 남은 납부 횟수: ${repaymentInfo[8]}`);
            console.log(`- 상환 방식: ${repaymentInfo[10]}`);
            console.log(`- 채무자 주소: ${repaymentInfo[11]}`);
            console.log(`- 활성 상태: ${repaymentInfo[12] ? '활성' : '비활성'}`);
            console.log(`- 연체 상태: ${repaymentInfo[13] ? '연체중' : '정상'}`);
        } catch (error) {
            console.log("상환 정보 조회 실패:", error);
            // 전체 에러 출력
            console.log("에러 상세:", error);
        }
    } catch (error) {
        console.error(`상환 일정 등록 실패:`, error);

        // 에러 세부 정보 출력
        if (error.data) {
            console.error(`에러 데이터:`, error.data);
        }

        if (error.reason) {
            console.error(`에러 이유:`, error.reason);
        }
    }
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });