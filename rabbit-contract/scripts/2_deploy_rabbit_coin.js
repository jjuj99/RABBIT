// scripts/2_deploy_rabbit_coin.js
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("Deploying RABBIT...");

  const [deployer] = await ethers.getSigners();
  console.log("Deploying with account:", deployer.address);
  console.log("Account balance:", (await ethers.provider.getBalance(deployer.address)).toString());

  // RabbitCoin 컨트랙트 팩토리 가져오기
  const RabbitCoin = await ethers.getContractFactory("RabbitCoin");
  
  // 초기 공급량 설정 (예: 1,000,000 RAB)
  const initialSupply = 1000000;

  // 컨트랙트 배포 (생성자에 초기 공급량 전달)
  console.log("Deploying contract with initial supply:", initialSupply);
  const rabbitCoin = await RabbitCoin.deploy(initialSupply);

  // 트랜잭션 마이닝 대기
  await rabbitCoin.waitForDeployment();

  // 배포된 컨트랙트 주소 출력
  console.log("Contract deployed successfully!");
  const rabbitCoinAddress = await rabbitCoin.getAddress();
  console.log("RabbitCoin contract address:", rabbitCoinAddress);

  // 배포 주소 파일 저장
  try {
    let deploymentData = {};
    try {
      deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    } catch (error) {
      // 파일이 없는 경우 새로 생성
    }
    
    deploymentData.rabbitCoinAddress = rabbitCoinAddress;
    
    fs.writeFileSync(
      path.join(__dirname, "../deployment-addresses.json"),
      JSON.stringify(deploymentData, null, 2)
    );
    console.log("Updated deployment addresses in deployment-addresses.json");
  } catch (error) {
    console.error("Failed to update deployment addresses:", error.message);
  }
  
  console.log("To verify the contract on Etherscan, run the following command:");
  console.log(`npx hardhat verify --network sepolia ${rabbitCoinAddress} ${initialSupply}`);
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });