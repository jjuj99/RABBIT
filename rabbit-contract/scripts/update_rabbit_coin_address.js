// scripts/update_rabbit_coin_address.js
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");

async function main() {
  console.log("Updating RabbitCoin address in dependent contracts...");

  const [deployer] = await ethers.getSigners();
  console.log("Account:", deployer.address);
  console.log("Account balance:", (await ethers.provider.getBalance(deployer.address)).toString());

  // 배포된 주소 가져오기
  let rabbitCoinAddress, promissoryNoteAddress, repaymentSchedulerAddress, promissoryNoteAuctionAddress;
  
  try {
    const deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    rabbitCoinAddress = deploymentData.rabbitCoinAddress;
    promissoryNoteAddress = deploymentData.promissoryNoteAddress;
    repaymentSchedulerAddress = deploymentData.repaymentSchedulerAddress;
    promissoryNoteAuctionAddress = deploymentData.promissoryNoteAuctionAddress;
    
    console.log("Loaded deployment addresses:");
    console.log("- RabbitCoin:", rabbitCoinAddress);
    console.log("- PromissoryNote:", promissoryNoteAddress);
    console.log("- RepaymentScheduler:", repaymentSchedulerAddress);
    console.log("- PromissoryNoteAuction:", promissoryNoteAuctionAddress);
  } catch (error) {
    console.error("Failed to read deployment addresses:", error.message);
    process.exit(1);
  }

  // RepaymentScheduler 컨트랙트에 새 RabbitCoin 주소 설정
  if (repaymentSchedulerAddress) {
    console.log("\nUpdating RabbitCoin address in RepaymentScheduler...");
    
    try {
      const repaymentScheduler = await ethers.getContractAt("RepaymentScheduler", repaymentSchedulerAddress);
      
      // updateContractAddresses 함수 호출
      const tx = await repaymentScheduler.updateContractAddresses(
        promissoryNoteAddress, 
        rabbitCoinAddress
      );
      
      await tx.wait();
      console.log("✅ Successfully updated RabbitCoin address in RepaymentScheduler!");
    } catch (error) {
      console.error("Failed to update RabbitCoin address in RepaymentScheduler:", error.message);
    }
  } else {
    console.log("⚠️ RepaymentScheduler address not found. Skipping update.");
  }

  // PromissoryNoteAuction 컨트랙트에 새 RabbitCoin 주소 설정
  if (promissoryNoteAuctionAddress) {
    console.log("\nUpdating RabbitCoin address in PromissoryNoteAuction...");
    
    try {
      const promissoryNoteAuction = await ethers.getContractAt("PromissoryNoteAuction", promissoryNoteAuctionAddress);
      
      // updateRabbitCoinAddress 함수 호출
      const tx = await promissoryNoteAuction.updateRabbitCoinAddress(rabbitCoinAddress);
      
      await tx.wait();
      console.log("✅ Successfully updated RabbitCoin address in PromissoryNoteAuction!");
    } catch (error) {
      console.error("Failed to update RabbitCoin address in PromissoryNoteAuction:", error.message);
    }
  } else {
    console.log("⚠️ PromissoryNoteAuction address not found. Skipping update.");
  }

  console.log("\n✅ RabbitCoin address update completed in all dependent contracts!");
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });