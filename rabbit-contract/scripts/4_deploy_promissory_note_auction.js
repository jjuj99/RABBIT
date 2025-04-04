// scripts/4_deploy_promissory_note_auction.js
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("Deploying PromissoryNoteAuction...");

  const [deployer] = await ethers.getSigners();
  console.log("Deploying with account:", deployer.address);
  console.log("Account balance:", (await ethers.provider.getBalance(deployer.address)).toString());

  // 필요한 주소들 가져오기
  let rabbitCoinAddress, promissoryNoteAddress;
  
  // 배포 주소 파일에서 읽어오기
  try {
    const deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    rabbitCoinAddress = deploymentData.rabbitCoinAddress;
    promissoryNoteAddress = deploymentData.promissoryNoteAddress;
  } catch (error) {
    console.error("Failed to read deployment addresses:", error.message);
  }

  if (!rabbitCoinAddress) {
    throw new Error("RABBIT_COIN_ADDRESS is required. Please deploy RabbitCoin first.");
  }

  if (!promissoryNoteAddress) {
    throw new Error("PROMISSORY_NOTE_ADDRESS is required. Please deploy PromissoryNote first.");
  }

  console.log("Using RabbitCoin address:", rabbitCoinAddress);
  console.log("Using PromissoryNote address:", promissoryNoteAddress);

  // PromissoryNoteAuction 컨트랙트 팩토리 가져오기
  const PromissoryNoteAuction = await ethers.getContractFactory("PromissoryNoteAuction");
  
  // 컨트랙트 배포 (RabbitCoin과 PromissoryNote 주소 전달)
  const promissoryNoteAuction = await PromissoryNoteAuction.deploy(
    rabbitCoinAddress,
    promissoryNoteAddress
  );
  
  // 트랜잭션 마이닝 대기
  await promissoryNoteAuction.waitForDeployment();

  // 배포된 컨트랙트 주소 출력
  const promissoryNoteAuctionAddress = await promissoryNoteAuction.getAddress();
  console.log("PromissoryNoteAuction contract deployed to:", promissoryNoteAuctionAddress);
  
  // 배포 주소 파일 업데이트
  try {
    let deploymentData = {};
    try {
      deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    } catch (error) {
      // 파일이 없는 경우 새로 생성
    }
    
    deploymentData.promissoryNoteAuctionAddress = promissoryNoteAuctionAddress;
    
    fs.writeFileSync(
      path.join(__dirname, "../deployment-addresses.json"),
      JSON.stringify(deploymentData, null, 2)
    );
    console.log("Updated deployment addresses in deployment-addresses.json");
  } catch (error) {
    console.error("Failed to update deployment addresses:", error.message);
  }
  
  console.log("To verify the contract on Etherscan, run the following command:");
  console.log(`npx hardhat verify --network sepolia ${promissoryNoteAuctionAddress} ${rabbitCoinAddress} ${promissoryNoteAddress}`);

  // 승인 설정: PromissoryNote가 PromissoryNoteAuction에게 소각 권한 부여
  console.log("Setting burn authorization for auction contract...");
  try {
    const promissoryNoteContract = await ethers.getContractAt("PromissoryNote", promissoryNoteAddress);
    const authTx = await promissoryNoteContract.addBurnAuthorization(promissoryNoteAuctionAddress);
    await authTx.wait();
    console.log("Burn authorization set successfully!");
  } catch (error) {
    console.error("Failed to set burn authorization:", error.message);
  }
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });