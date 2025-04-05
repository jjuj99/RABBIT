// scripts/mint_promissory_note.js
const { ethers } = require("hardhat");
require("dotenv").config();

// PromissoryNote 컨트랙트 ABI (인터페이스 정의)
const PROMISSORY_NOTE_ABI = [
  "function mint(tuple(string nftImage, tuple(string crSign, string crName, address crWalletAddress, string crInfoHash) crInfo, tuple(string drSign, string drName, address drWalletAddress, string drInfoHash) drInfo, uint256 la, uint256 ir, uint256 lt, string repayType, string matDt, uint256 mpDt, uint256 dir, string contractDate, bool earlyPayFlag, uint256 earlyPayFee, uint256 accel, tuple(string addTerms, string addTermsHash) addTerms) metadata, address to) external returns (uint256)",
  "function tokenIdCounter() external view returns (uint256)"
];

// Sepolia 테스트넷에 배포된 컨트랙트 주소
const CONTRACT_ADDRESS = process.env.PROMISSORY_NOTE_ADDRESS;

async function main() {
  // 컨트랙트 연결
  const privateKey = process.env.PRIVATE_KEY;
  const provider = new ethers.JsonRpcProvider(`https://sepolia.infura.io/v3/${process.env.INFURA_API_KEY}`);
  const wallet = new ethers.Wallet(privateKey, provider);

  console.log("스크립트 실행 계정:", wallet.address);
  
  const promissoryNoteContract = new ethers.Contract(
    CONTRACT_ADDRESS,
    PROMISSORY_NOTE_ABI,
    wallet
  );
  
  // ============ 여기에 데이터를 입력하세요 ============
  // 채권자 정보
  const creditorInfo = {
    crSign: "채권자 서명: 암호화해서 저장 - String",
    crName: "채권자 이름",
    crWalletAddress: "계좌",
    crInfoHash: ethers.keccak256(ethers.toUtf8Bytes("채권자 개인정보 해시")),
  };
  
  // 채무자 정보
  const debtorInfo = {
    drSign: "채무자 서명: 암호화해서 저장 - String",
    drName: "채무자 이름",
    drWalletAddress: "계좌",
    drInfoHash: ethers.keccak256(ethers.toUtf8Bytes("채무자 개인정보 해시")),
  };
  
  // 계약 정보
  const nftImage = "https://cdn.pixabay.com/photo/2022/02/24/03/10/nft-7031679_1280.jpg";
  const loanAmount = 10000000;
  const interestRate = 800;
  const loanTerm = 15;
  const repaymentType = "BP";
  
  // 현재 날짜 기준 계약일 설정
  const currentDate = new Date();
  const contractDate = currentDate.toISOString().split('T')[0];
  
  // 만기일 설정 (현재 날짜로부터 loanTerm 개월 후)
  const maturityDate = new Date();
  maturityDate.setMonth(maturityDate.getMonth() + loanTerm);
  const maturityDateStr = maturityDate.toISOString().split('T')[0];
  
  const monthlyPaymentDay = 6;                          // 이자 납부일 (매월 6일)
  const defaultInterestRate = 1500;                     // 연체이자율 (15.00% = 1500)
  const allowEarlyPayment = true;                       // 조기 상환 가능 여부
  const earlyPaymentFee = 300;                          // 조기 상환 수수료 (3.00% = 300)
  const accelerationAfterMissedPayments = 2;            // 연속 미납 횟수
  
  // 추가 약관
  const additionalTerms = {
    addTerms: "안갚으면 캥거루랑 스파링",
    addTermsHash: ethers.keccak256(ethers.toUtf8Bytes("안갚으면 캥거루랑 스파링")),
  };
  
  // NFT를 받을 주소 (채권자 주소)
  const recipient = creditorInfo.crWalletAddress;
  // ============ 데이터 입력 끝 ============
  
  // 데이터 구성
  const metadata = {
    nftImage: nftImage,
    crInfo: creditorInfo,
    drInfo: debtorInfo,
    la: BigInt(loanAmount),
    ir: BigInt(interestRate),
    lt: BigInt(loanTerm),
    repayType: repaymentType,
    matDt: maturityDateStr,
    mpDt: BigInt(monthlyPaymentDay),
    dir: BigInt(defaultInterestRate),
    contractDate: contractDate,
    earlyPayFlag: allowEarlyPayment,
    earlyPayFee: BigInt(earlyPaymentFee),
    accel: BigInt(accelerationAfterMissedPayments),
    addTerms: additionalTerms
  };
  
  console.log("차용증 NFT 발행 준비 완료:");
  console.log(`- 채권자: ${creditorInfo.crName}`);
  console.log(`- 채무자: ${debtorInfo.drName}`);
  console.log(`- 원금: ${loanAmount} RABBIT`);
  console.log(`- 이자율: ${interestRate/100}%`);
  console.log(`- 대출 기간: ${loanTerm}개월`);
  console.log(`- 상환 방식: ${repaymentType}`);
  console.log(`- 계약일: ${contractDate}`);
  console.log(`- 만기일: ${maturityDateStr}`);
  
  try {
    // mint 함수 호출
    console.log("NFT 발행 중...");
    const tx = await promissoryNoteContract.mint(metadata, recipient);
    const receipt = await tx.wait();
    
    // 발행된 토큰 ID 확인
    const tokenId = (await promissoryNoteContract.tokenIdCounter()) - 1n;
    
    console.log(`성공: 토큰 ID ${tokenId} 발행 완료`);
    console.log(`트랜잭션 해시: ${receipt.hash}`);
    console.log(`Etherscan에서 확인: https://sepolia.etherscan.io/tx/${receipt.hash}`);
  } catch (error) {
    console.error(`NFT 발행 실패:`, error);
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });