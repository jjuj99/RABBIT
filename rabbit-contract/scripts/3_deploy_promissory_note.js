// scripts/3_deploy_promissory_note.js
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("Deploying PromissoryNote...");

  const [deployer] = await ethers.getSigners();
  console.log("Deploying with account:", deployer.address);
  console.log("Account balance:", (await ethers.provider.getBalance(deployer.address)).toString());

  // PromissoryNote 컨트랙트 팩토리 가져오기
  const PromissoryNote = await ethers.getContractFactory("PromissoryNote");
  
  // 컨트랙트 배포
  const promissoryNote = await PromissoryNote.deploy();
  
  // 트랜잭션 마이닝 대기
  await promissoryNote.waitForDeployment();

  // 배포된 컨트랙트 주소 출력
  const promissoryNoteAddress = await promissoryNote.getAddress();
  console.log("PromissoryNote contract deployed to:", promissoryNoteAddress);

  // 배포 주소 파일 업데이트
  try {
    let deploymentData = {};
    try {
      deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    } catch (error) {
      // 파일이 없는 경우 새로 생성
    }
    
    deploymentData.promissoryNoteAddress = promissoryNoteAddress;
    
    fs.writeFileSync(
      path.join(__dirname, "../deployment-addresses.json"),
      JSON.stringify(deploymentData, null, 2)
    );
    console.log("Updated deployment addresses in deployment-addresses.json");
  } catch (error) {
    console.error("Failed to update deployment addresses:", error.message);
  }
  
  console.log("To verify the contract on Etherscan, run the following command:");
  console.log(`npx hardhat verify --network sepolia ${promissoryNoteAddress}`);
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });