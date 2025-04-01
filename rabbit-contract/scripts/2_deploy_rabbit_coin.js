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
  const rabbitCoinAddress = await rabbitCoin.getAddress();
  console.log("RabbitCoin contract address:", rabbitCoinAddress);
  
  // 환경 변수에서 시스템 컨트랙트 주소 가져오기
  const systemContractAddress = process.env.SYSTEM_CONTRACT_ADDRESS;
  
  if (systemContractAddress) {
    console.log("Setting system contract address:", systemContractAddress);
    try {
      const setTx = await rabbitCoin.setSystemContract(systemContractAddress, {
        gasLimit: 100000
      });
      await setTx.wait();
      console.log("System contract address set successfully!");
    } catch (error) {
      console.error("Failed to set system contract address:", error.message);
    }
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