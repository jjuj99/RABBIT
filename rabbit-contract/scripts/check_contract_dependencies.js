// scripts/check_contract_dependencies.js
const hre = require("hardhat");
const fs = require("fs");
const path = require("path");
const chalk = require("chalk"); // 예쁜 콘솔 출력을 위한 패키지 (선택적 설치: npm install chalk)

async function main() {
  console.log(chalk.blue.bold("===== 계약 의존성 확인 스크립트 ====="));
  console.log(chalk.gray("현재 모든 컨트랙트의 의존성 주소를 확인합니다.\n"));

  const [deployer] = await ethers.getSigners();
  console.log(chalk.yellow("실행 계정:"), deployer.address);
  console.log(chalk.yellow("계정 잔액:"), ethers.formatEther(await ethers.provider.getBalance(deployer.address)), "ETH\n");

  // 배포된 주소 가져오기
  let rabbitCoinAddress, promissoryNoteAddress, repaymentSchedulerAddress, promissoryNoteAuctionAddress;
  
  try {
    const deploymentData = JSON.parse(fs.readFileSync(path.join(__dirname, "../deployment-addresses.json"), "utf8"));
    rabbitCoinAddress = deploymentData.rabbitCoinAddress;
    promissoryNoteAddress = deploymentData.promissoryNoteAddress;
    repaymentSchedulerAddress = deploymentData.repaymentSchedulerAddress;
    promissoryNoteAuctionAddress = deploymentData.promissoryNoteAuctionAddress;
    
    console.log(chalk.cyan("배포 주소 정보:"));
    console.log(chalk.gray("- RabbitCoin:"), rabbitCoinAddress);
    console.log(chalk.gray("- PromissoryNote:"), promissoryNoteAddress);
    console.log(chalk.gray("- RepaymentScheduler:"), repaymentSchedulerAddress);
    console.log(chalk.gray("- PromissoryNoteAuction:"), promissoryNoteAuctionAddress, "\n");
  } catch (error) {
    console.error(chalk.red("배포 주소 파일을 읽는데 실패했습니다:"), error.message);
    process.exit(1);
  }

  console.log(chalk.blue.bold("===== 컨트랙트 의존성 검증 시작 =====\n"));

  // 1. RepaymentScheduler 의존성 확인
  if (repaymentSchedulerAddress) {
    console.log(chalk.yellow.bold("1. RepaymentScheduler 의존성 확인"));
    
    try {
      const repaymentScheduler = await ethers.getContractAt("RepaymentScheduler", repaymentSchedulerAddress);
      
      // RabbitCoin 주소 확인
      const schedulerRabbitCoinAddress = await repaymentScheduler.rabbitCoinAddress();
      const isRabbitCoinCorrect = schedulerRabbitCoinAddress.toLowerCase() === rabbitCoinAddress.toLowerCase();
      
      console.log(chalk.gray("현재 설정된 RabbitCoin 주소:"), schedulerRabbitCoinAddress);
      console.log(chalk.gray("예상되는 RabbitCoin 주소:"), rabbitCoinAddress);
      console.log(isRabbitCoinCorrect 
        ? chalk.green("✓ RabbitCoin 주소가 정확히 설정되었습니다.") 
        : chalk.red("✗ RabbitCoin 주소가 일치하지 않습니다!"));
      
      // PromissoryNote 주소 확인
      const schedulerPromissoryNoteAddress = await repaymentScheduler.promissoryNoteAddress();
      const isPromissoryNoteCorrect = schedulerPromissoryNoteAddress.toLowerCase() === promissoryNoteAddress.toLowerCase();
      
      console.log(chalk.gray("현재 설정된 PromissoryNote 주소:"), schedulerPromissoryNoteAddress);
      console.log(chalk.gray("예상되는 PromissoryNote 주소:"), promissoryNoteAddress);
      console.log(isPromissoryNoteCorrect 
        ? chalk.green("✓ PromissoryNote 주소가 정확히 설정되었습니다.") 
        : chalk.red("✗ PromissoryNote 주소가 일치하지 않습니다!"));
      
    } catch (error) {
      console.error(chalk.red("RepaymentScheduler 의존성 확인 중 오류 발생:"), error.message);
    }
    
    console.log();
  }

  // 2. PromissoryNoteAuction 의존성 확인
  if (promissoryNoteAuctionAddress) {
    console.log(chalk.yellow.bold("2. PromissoryNoteAuction 의존성 확인"));
    
    try {
      const promissoryNoteAuction = await ethers.getContractAt("PromissoryNoteAuction", promissoryNoteAuctionAddress);
      
      // RabbitCoin 주소 확인
      const auctionRabbitCoinAddress = await promissoryNoteAuction.rabbitCoinAddress();
      const isRabbitCoinCorrect = auctionRabbitCoinAddress.toLowerCase() === rabbitCoinAddress.toLowerCase();
      
      console.log(chalk.gray("현재 설정된 RabbitCoin 주소:"), auctionRabbitCoinAddress);
      console.log(chalk.gray("예상되는 RabbitCoin 주소:"), rabbitCoinAddress);
      console.log(isRabbitCoinCorrect 
        ? chalk.green("✓ RabbitCoin 주소가 정확히 설정되었습니다.") 
        : chalk.red("✗ RabbitCoin 주소가 일치하지 않습니다!"));
      
      // PromissoryNote 주소 확인
      const auctionPromissoryNoteAddress = await promissoryNoteAuction.promissoryNoteAddress();
      const isPromissoryNoteCorrect = auctionPromissoryNoteAddress.toLowerCase() === promissoryNoteAddress.toLowerCase();
      
      console.log(chalk.gray("현재 설정된 PromissoryNote 주소:"), auctionPromissoryNoteAddress);
      console.log(chalk.gray("예상되는 PromissoryNote 주소:"), promissoryNoteAddress);
      console.log(isPromissoryNoteCorrect 
        ? chalk.green("✓ PromissoryNote 주소가 정확히 설정되었습니다.") 
        : chalk.red("✗ PromissoryNote 주소가 일치하지 않습니다!"));
      
    } catch (error) {
      console.error(chalk.red("PromissoryNoteAuction 의존성 확인 중 오류 발생:"), error.message);
    }
    
    console.log();
  }

  // 3. PromissoryNote 스케줄러 주소 확인
  if (promissoryNoteAddress) {
    console.log(chalk.yellow.bold("3. PromissoryNote 의존성 확인"));
    
    try {
      const promissoryNote = await ethers.getContractAt("PromissoryNote", promissoryNoteAddress);
      
      // 스케줄러 주소 확인
      const noteSchedulerAddress = await promissoryNote.schedulerAddress();
      const isSchedulerCorrect = noteSchedulerAddress.toLowerCase() === repaymentSchedulerAddress.toLowerCase();
      
      console.log(chalk.gray("현재 설정된 스케줄러 주소:"), noteSchedulerAddress);
      console.log(chalk.gray("예상되는 스케줄러 주소:"), repaymentSchedulerAddress);
      console.log(isSchedulerCorrect 
        ? chalk.green("✓ 스케줄러 주소가 정확히 설정되었습니다.") 
        : chalk.red("✗ 스케줄러 주소가 일치하지 않습니다!"));
      
      // 소각 권한 확인
      const schedulerBurnAuth = await promissoryNote.burnAuthorizedAddresses(repaymentSchedulerAddress);
      console.log(schedulerBurnAuth 
        ? chalk.green("✓ 스케줄러에 소각 권한이 부여되었습니다.") 
        : chalk.red("✗ 스케줄러에 소각 권한이 없습니다!"));
      
      if (promissoryNoteAuctionAddress) {
        const auctionBurnAuth = await promissoryNote.burnAuthorizedAddresses(promissoryNoteAuctionAddress);
        console.log(auctionBurnAuth 
          ? chalk.green("✓ 경매 컨트랙트에 소각 권한이 부여되었습니다.") 
          : chalk.red("✗ 경매 컨트랙트에 소각 권한이 없습니다!"));
      }
      
    } catch (error) {
      console.error(chalk.red("PromissoryNote 의존성 확인 중 오류 발생:"), error.message);
    }
    
    console.log();
  }

  // 4. RabbitCoin 토큰 정보 확인
  if (rabbitCoinAddress) {
    console.log(chalk.yellow.bold("4. RabbitCoin 토큰 정보 확인"));
    
    try {
      const rabbitCoin = await ethers.getContractAt("RabbitCoin", rabbitCoinAddress);
      
      // 토큰 이름, 심볼, 총 발행량 확인
      const name = await rabbitCoin.name();
      const symbol = await rabbitCoin.symbol();
      const totalSupply = await rabbitCoin.totalSupply();
      const decimals = await rabbitCoin.decimals();
      
      console.log(chalk.gray("토큰 이름:"), name);
      console.log(chalk.gray("토큰 심볼:"), symbol);
      console.log(chalk.gray("토큰 소수점:"), decimals.toString());
      console.log(chalk.gray("총 발행량:"), ethers.formatUnits(totalSupply, decimals));
      
      console.log(chalk.green("✓ RabbitCoin 토큰 정보가 확인되었습니다."));
      
    } catch (error) {
      console.error(chalk.red("RabbitCoin 정보 확인 중 오류 발생:"), error.message);
    }
    
    console.log();
  }

  console.log(chalk.blue.bold("===== 종합 결과 ====="));
  console.log(chalk.green("모든 컨트랙트 주소 및 의존성이 확인되었습니다."));
  console.log(chalk.yellow("문제가 있는 경우 위 결과를 확인하고 필요한 조치를 취하세요."));
  console.log(chalk.blue("의존성 확인 완료!"));
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(chalk.red("스크립트 실행 중 오류가 발생했습니다:"), error);
    process.exit(1);
  });