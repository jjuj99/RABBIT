// scripts/1_initial_deployment.js
const hre = require("hardhat");

async function main() {
  console.log("Starting initial deployment...");
  
  // 배포 계정 가져오기
  const [deployer] = await ethers.getSigners();
  console.log("Deploying with account:", deployer.address);
  console.log("Account balance:", (await ethers.provider.getBalance(deployer.address)).toString());

  // 여기에 배포 로직 추가
  
  console.log("Contract deployed successfully!");
}

// 스크립트 실행 및 에러 처리
main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });