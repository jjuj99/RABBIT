// scripts/2_deploy_rabbit_token.js
const hre = require("hardhat");

async function main() {
  console.log("Deploying RabbitToken...");
  
  // 배포 계정 가져오기
  const [deployer] = await ethers.getSigners();
  console.log("Deploying with account:", deployer.address);
  
  // RabbitToken 컨트랙트 팩토리 가져오기
  const RabbitToken = await ethers.getContractFactory("RabbitToken");
  
  // 컨트랙트 배포 (생성자 인자가 있다면 여기에 추가)
  const rabbitToken = await RabbitToken.deploy();
  
  // 배포 완료 대기
  await rabbitToken.deployed();
  
  console.log("RabbitToken deployed to:", rabbitToken.address);
}

// 스크립트 실행 및 에러 처리
main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });