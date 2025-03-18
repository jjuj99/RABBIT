// scripts/1_initial_deployment.js
const hre = require("hardhat");

async function main() {
  console.log("Starting initial deployment...");
  
  // 배포 계정 가져오기
  const [deployer] = await ethers.getSigners();
  console.log("Deploying contracts with the account:", deployer.address);
  
  // 여기에 초기 배포 로직 추가
  // 예시: Migrations 대신 다른 초기 컨트랙트가 필요하면 여기서 배포
  
  console.log("Initial deployment completed");
}

// 스크립트 실행 및 에러 처리
main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });