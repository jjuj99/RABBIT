// scripts/2_deploy_rabbit_coin.js
const hre = require("hardhat");

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
  
  // 배포된 컨트랙트 주소 출력
  console.log("Contract deployed successfully!");
  console.log("RabbitCoin contract address:", await rabbitCoin.getAddress());

  console.log("To verify the contract on Etherscan, run the following command:");
  console.log(`npx hardhat verify --network sepolia ${await rabbitCoin.getAddress()} ${initialSupply}`);
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });