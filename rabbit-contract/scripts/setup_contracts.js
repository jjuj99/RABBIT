// scripts/setup_contracts.js
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("Setting up contract configurations...");

  const [deployer] = await ethers.getSigners();
  console.log("Account:", deployer.address);
  console.log("Account balance:", (await ethers.provider.getBalance(deployer.address)).toString());

  // 배포된 주소 가져오기
  let promissoryNoteAddress, repaymentSchedulerAddress, promissoryNoteAuctionAddress;
  
  try {
    const deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    promissoryNoteAddress = deploymentData.promissoryNoteAddress;
    repaymentSchedulerAddress = deploymentData.repaymentSchedulerAddress;
    promissoryNoteAuctionAddress = deploymentData.promissoryNoteAuctionAddress;
    
    console.log("Loaded deployment addresses:");
    console.log("- PromissoryNote:", promissoryNoteAddress);
    console.log("- RepaymentScheduler:", repaymentSchedulerAddress);
    console.log("- PromissoryNoteAuction:", promissoryNoteAuctionAddress);
  } catch (error) {
    console.error("Failed to read deployment addresses:", error.message);
    process.exit(1);
  }

  // PromissoryNote 인스턴스 가져오기
  const promissoryNote = await ethers.getContractAt("PromissoryNote", promissoryNoteAddress);
  
  // 1. PromissoryNote의 schedulerAddress 설정
  console.log("\nSetting scheduler address in PromissoryNote...");
  try {
    const tx = await promissoryNote.setSchedulerAddress(repaymentSchedulerAddress);
    await tx.wait();
    console.log("✅ Scheduler address set successfully!");
  } catch (error) {
    console.error("Failed to set scheduler address:", error.message);
  }

  // 2. RepaymentScheduler에 소각 권한 부여
  console.log("\nSetting burn authorization for RepaymentScheduler...");
  try {
    const tx = await promissoryNote.addBurnAuthorization(repaymentSchedulerAddress);
    await tx.wait();
    console.log("✅ Burn authorization for RepaymentScheduler set successfully!");
  } catch (error) {
    console.error("Failed to set burn authorization for RepaymentScheduler:", error.message);
  }

  // 3. PromissoryNoteAuction에 소각 권한 부여
  if (promissoryNoteAuctionAddress) {
    console.log("\nSetting burn authorization for PromissoryNoteAuction...");
    try {
      const tx = await promissoryNote.addBurnAuthorization(promissoryNoteAuctionAddress);
      await tx.wait();
      console.log("✅ Burn authorization for PromissoryNoteAuction set successfully!");
    } catch (error) {
      console.error("Failed to set burn authorization for PromissoryNoteAuction:", error.message);
    }
  }

  console.log("\n✅ Setup completed!");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });