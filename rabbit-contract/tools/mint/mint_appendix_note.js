// scripts/mint_appendix_note.js
const { ethers } = require("hardhat");
require("dotenv").config();

const PROMISSORY_NOTE_ABI = [
  "function mintAppendixNFT(uint256 originalTokenId, tuple(uint256 tokenId, string grantorSign, string grantorName, address grantorWalletAddress, string grantorInfoHash, string granteeSign, string granteeName, address granteeWalletAddress, string granteeInfoHash, uint256 la, string contractDate, string originalText) metadata, address recipient) external returns (uint256)",
  "function tokenIdCounter() external view returns (uint256)"
];

const CONTRACT_ADDRESS = process.env.PROMISSORY_NOTE_ADDRESS;

async function main() {
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

  const originalTokenId = 3;

  // 양도인 정보
  const grantorInfo = {
    grantorSign: "양도인 서명: 암호화된 서명",
    grantorName: "이름",
    grantorWalletAddress: "계좌",
    grantorInfoHash: ethers.keccak256(ethers.toUtf8Bytes("양도인 개인정보 해시")),
  };

  // 양수인 정보
  const granteeInfo = {
    granteeSign: "양수인 서명: 암호화된 서명",
    granteeName: "이름",
    granteeWalletAddress: "계좌",
    granteeInfoHash: ethers.keccak256(ethers.toUtf8Bytes("양수인 개인정보 해시")),
  };

  const remainingLoanAmount = 10000000; // 남은 원금
  const contractDate = new Date().toISOString().split('T')[0];
  const originalTextHash = ethers.keccak256(ethers.toUtf8Bytes("계약서 원문 내용"));

  const metadata = {
    tokenId: originalTokenId,
    grantorSign: grantorInfo.grantorSign,
    grantorName: grantorInfo.grantorName,
    grantorWalletAddress: grantorInfo.grantorWalletAddress,
    grantorInfoHash: grantorInfo.grantorInfoHash,
    granteeSign: granteeInfo.granteeSign,
    granteeName: granteeInfo.granteeName,
    granteeWalletAddress: granteeInfo.granteeWalletAddress,
    granteeInfoHash: granteeInfo.granteeInfoHash,
    la: BigInt(remainingLoanAmount),
    contractDate: contractDate,
    originalText: originalTextHash,
  };

  const recipient = granteeInfo.granteeWalletAddress;

  console.log("부속 NFT 발행 준비 완료:");
  console.log(`- 원본 토큰 ID: ${originalTokenId}`);
  console.log(`- 양도인: ${grantorInfo.grantorName}`);
  console.log(`- 양수인: ${granteeInfo.granteeName}`);
  console.log(`- 남은 원금: ${remainingLoanAmount} RAB`);
  console.log(`- 계약일: ${contractDate}`);

  try {
    console.log("부속 NFT 발행 중...");
    const tx = await promissoryNoteContract.mintAppendixNFT(originalTokenId, metadata, recipient);
    const receipt = await tx.wait();

    const tokenId = (await promissoryNoteContract.tokenIdCounter()) - 1n;

    console.log(`성공: 부속 NFT 토큰 ID ${tokenId} 발행 완료`);
    console.log(`트랜잭션 해시: ${receipt.hash}`);
    console.log(`Etherscan에서 확인: https://sepolia.etherscan.io/tx/${receipt.hash}`);
  } catch (error) {
    console.error("부속 NFT 발행 실패:", error);
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });