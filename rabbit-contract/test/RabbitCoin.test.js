const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("RabbitCoin", function () {
  let RabbitCoin;
  let rabbitCoin;
  let owner;
  let addr1;
  let addr2;
  let addrs;
  let systemContract;
  
  // 초기 공급량을 1,000,000 RAB로 설정
  const initialSupply = 1000000;

  beforeEach(async function () {
    // 컨트랙트 및 계정 설정
    RabbitCoin = await ethers.getContractFactory("RabbitCoin");
    [owner, addr1, addr2, systemContract, ...addrs] = await ethers.getSigners();
    
    // 컨트랙트 배포
    rabbitCoin = await RabbitCoin.deploy(initialSupply);
  });

  // describe: 테스트 그룹을 정의
  // it: 테스트 그룹 내 개별 테스트 케이스 정의

  describe("배포", function () {
    it("올바른 코인인 이름, 심볼, 소수점 자리수를 설정해야 함", async function () {
      expect(await rabbitCoin.name()).to.equal("RABBIT");
      expect(await rabbitCoin.symbol()).to.equal("RAB");
      expect(await rabbitCoin.decimals()).to.equal(0);
    });

    it("총 공급량이 초기 공급량과 동일해야 함", async function () {
      const expectedSupply = ethers.parseUnits(initialSupply.toString(), 0);
      expect(await rabbitCoin.totalSupply()).to.equal(expectedSupply);
    });

    it("배포자에게 총 공급량이 할당되어야 함", async function () {
      const expectedSupply = ethers.parseUnits(initialSupply.toString(), 0);
      expect(await rabbitCoin.balanceOf(owner.address)).to.equal(expectedSupply);
    });

    it("decimals가 0으로 설정되어 있고, 그에 따라 RAB 단위가 올바르게 적용되어야 함", async function () {
      // 소수점 자리가 없으므로 1 RAB는 1 단위와 같음
      const oneRAB = ethers.parseUnits("1", 0);
      await rabbitCoin.transfer(addr1.address, oneRAB);
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(1);
    });
  });

  describe("트랜잭션", function () {
    it("코인을 한 계정에서 다른 계정으로 전송할 수 있어야 함", async function () {
      // owner에서 addr1로 50 RAB 전송
      const transferAmount = ethers.parseUnits("50", 0);
      await rabbitCoin.transfer(addr1.address, transferAmount);
      
      // addr1의 잔액이 50 RAB이어야 함
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(transferAmount);
    });

    it("잔액이 부족할 때 전송이 실패해야 함", async function () {
      // addr1은 초기에 0 RAB를 가지고 있음
      const initialBalance = await rabbitCoin.balanceOf(addr1.address);
      
      // addr1이 1 RAB를 전송하려 하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).transfer(addr2.address, 1)
      ).to.be.revertedWith("ERC20: transfer amount exceeds balance");
    });

    it("approval과 transferFrom이 올바르게 작동해야 함", async function () {
      // owner가 addr1에게 100 RAB를 사용할 수 있도록 승인
      const approveAmount = ethers.parseUnits("100", 0);
      await rabbitCoin.approve(addr1.address, approveAmount);
      
      // 승인된 금액 확인
      expect(await rabbitCoin.allowance(owner.address, addr1.address)).to.equal(approveAmount);
      
      // addr1이 owner의 50 RAB를 addr2에게 전송
      const transferAmount = ethers.parseUnits("50", 0);
      await rabbitCoin.connect(addr1).transferFrom(owner.address, addr2.address, transferAmount);
      
      // 잔액 확인
      expect(await rabbitCoin.balanceOf(addr2.address)).to.equal(transferAmount);
      
      // 남은 allowance 확인
      expect(await rabbitCoin.allowance(owner.address, addr1.address)).to.equal(approveAmount - transferAmount);
    });

    it("approve 함수를 중복 호출하면 allowance가 덮어써져야 함", async function () {
      await rabbitCoin.approve(addr1.address, 100);
      await rabbitCoin.approve(addr1.address, 200);
      expect(await rabbitCoin.allowance(owner.address, addr1.address)).to.equal(200);
    });

    it("승인된 금액보다 많은 양을 transferFrom으로 전송하면 실패해야 함", async function () {
      await rabbitCoin.approve(addr1.address, 50);
      await expect(
        rabbitCoin.connect(addr1).transferFrom(owner.address, addr2.address, 51)
      ).to.be.revertedWith("ERC20: transfer amount exceeds allowance");
    });

    it("제로 주소로 전송하면 실패해야 함", async function () {
      await expect(
        rabbitCoin.transfer(ethers.ZeroAddress, 100)
      ).to.be.revertedWith("ERC20: transfer to the zero address");
    });
    
    it("제로 주소로 민팅하면 실패해야 함", async function () {
      await expect(
        rabbitCoin.mint(ethers.ZeroAddress, 100)
      ).to.be.revertedWith("ERC20: mint to the zero address");
    });
  });

  describe("시스템 컨트랙트와 충전 기능", function () {
    it("소유자만 시스템 컨트랙트 주소를 설정할 수 있어야 함", async function () {
      await rabbitCoin.setSystemContract(systemContract.address);
      
      // 다른 사용자가 시스템 컨트랙트 주소를 설정하려고 하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).setSystemContract(addr1.address)
      ).to.be.revertedWithCustomError(rabbitCoin, "OwnableUnauthorizedAccount")
      .withArgs(addr1.address);
    });
    
    it("시스템 컨트랙트 주소가 이미 설정된 경우 다시 설정할 수 없어야 함", async function () {
      // 첫 번째 설정은 성공해야 함
      await rabbitCoin.setSystemContract(systemContract.address);
      
      // 두 번째 설정은 실패해야 함
      await expect(
        rabbitCoin.setSystemContract(addr1.address)
      ).to.be.revertedWith("System contract already set");
    });
    
    it("제로 주소를 시스템 컨트랙트로 설정할 수 없어야 함", async function () {
      await expect(
        rabbitCoin.setSystemContract(ethers.ZeroAddress)
      ).to.be.revertedWith("Invalid system contract address");
    });
    
    it("소유자만 RAB를 충전할 수 있어야 함", async function () {
      const chargeAmount = ethers.parseUnits("1000", 0);
      
      // 소유자가 아닌 사용자가 충전을 시도하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).charge(addr2.address, chargeAmount)
      ).to.be.revertedWithCustomError(rabbitCoin, "OwnableUnauthorizedAccount")
      .withArgs(addr1.address);
      
      // 소유자는 충전할 수 있어야 함
      await rabbitCoin.charge(addr1.address, chargeAmount);
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(chargeAmount);
    });
    
    it("충전 시 제로 주소로 충전할 수 없어야 함", async function () {
      await expect(
        rabbitCoin.charge(ethers.ZeroAddress, 100)
      ).to.be.revertedWith("Cannot charge to zero address");
    });
    
    it("충전 시 0 이하의 금액으로 충전할 수 없어야 함", async function () {
      await expect(
        rabbitCoin.charge(addr1.address, 0)
      ).to.be.revertedWith("Amount must be greater than 0");
    });
    
    it("충전 시 Charged 이벤트를 발생시켜야 함", async function () {
      const chargeAmount = ethers.parseUnits("1000", 0);
      
      // Charged 이벤트 확인
      await expect(rabbitCoin.charge(addr1.address, chargeAmount))
        .to.emit(rabbitCoin, "Charged")
        .withArgs(addr1.address, chargeAmount);
    });
    
    it("시스템 컨트랙트 설정 후 충전 시 시스템 컨트랙트에 자동 승인되어야 함", async function () {
      // 시스템 컨트랙트 설정
      await rabbitCoin.setSystemContract(systemContract.address);
      
      const chargeAmount = ethers.parseUnits("1000", 0);
      await rabbitCoin.charge(addr1.address, chargeAmount);
      
      // 시스템 컨트랙트에 충전된 금액이 전부 승인되어 있어야 함
      expect(await rabbitCoin.allowance(addr1.address, systemContract.address)).to.equal(chargeAmount);
      
      // 추가 충전 시 전체 잔액에 대해 승인이 업데이트되어야 함
      const additionalAmount = ethers.parseUnits("500", 0);
      await rabbitCoin.charge(addr1.address, additionalAmount);
      
      expect(await rabbitCoin.allowance(addr1.address, systemContract.address)).to.equal(chargeAmount + additionalAmount);
    });
    
    it("시스템 컨트랙트 설정 전에 충전된 경우 시스템 컨트랙트에 자동 승인되지 않아야 함", async function () {
      // 시스템 컨트랙트 설정 없이 충전
      const chargeAmount = ethers.parseUnits("1000", 0);
      await rabbitCoin.charge(addr1.address, chargeAmount);
      
      // 시스템 컨트랙트 설정
      await rabbitCoin.setSystemContract(systemContract.address);
      
      // 시스템 컨트랙트에 승인된 금액이 없어야 함
      expect(await rabbitCoin.allowance(addr1.address, systemContract.address)).to.equal(0);
    });

    it("시스템 컨트랙트가 승인받은 코인을 다른 사용자에게 전송할 수 있어야 함", async function () {
      // 시스템 컨트랙트 설정
      await rabbitCoin.setSystemContract(systemContract.address);
      
      // addr1에게 코인 충전 (자동으로 시스템 컨트랙트에 승인됨)
      const chargeAmount = ethers.parseUnits("1000", 0);
      await rabbitCoin.charge(addr1.address, chargeAmount);
      
      // 시스템 컨트랙트가 승인받은 코인 중 일부를 addr2에게 전송
      const transferAmount = ethers.parseUnits("500", 0);
      await rabbitCoin.connect(systemContract).transferFrom(
        addr1.address,
        addr2.address,
        transferAmount
      );
      
      // addr1과 addr2의 잔액 확인
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(chargeAmount - transferAmount);
      expect(await rabbitCoin.balanceOf(addr2.address)).to.equal(transferAmount);
      
      // 남은 allowance 확인
      expect(await rabbitCoin.allowance(addr1.address, systemContract.address)).to.equal(chargeAmount - transferAmount);
    });
  });

  describe("민팅과 소각", function () {
    it("소유자가 새 RAB를 발행할 수 있어야 함", async function () {
      const mintAmount = ethers.parseUnits("5000", 0);
      const initialSupplyBN = ethers.parseUnits(initialSupply.toString(), 0);
      
      await rabbitCoin.mint(addr1.address, mintAmount);
      
      // addr1의 잔액이 민팅된 금액과 일치해야 함
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(mintAmount);
      
      // 총 공급량이 증가해야 함
      expect(await rabbitCoin.totalSupply()).to.equal(initialSupplyBN + mintAmount);
    });

    it("소유자가 아닌 계정은 민팅을 할 수 없어야 함", async function () {
      const mintAmount = ethers.parseUnits("5000", 0);
      
      // addr1이 민팅을 시도하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).mint(addr2.address, mintAmount)
      ).to.be.revertedWithCustomError(rabbitCoin, "OwnableUnauthorizedAccount")
       .withArgs(addr1.address);
    });

    it("사용자가 RAB를 소각할 수 있어야 함", async function () {
      // 먼저 addr1에게 RAB 전송 (사용자 주소에서 소각할 수 있도록 RAB 전송)
      const transferAmount = ethers.parseUnits("100", 0);
      await rabbitCoin.transfer(addr1.address, transferAmount);
      
      // addr1이 50 RAB를 소각
      const burnAmount = ethers.parseUnits("50", 0);
      await rabbitCoin.connect(addr1).burn(burnAmount);
      
      // addr1의 잔액이 감소해야 함
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(transferAmount - burnAmount);
      
      // 총 공급량이 감소해야 함
      const initialSupplyBN = ethers.parseUnits(initialSupply.toString(), 0);
      expect(await rabbitCoin.totalSupply()).to.equal(initialSupplyBN - burnAmount);
    });

    it("잔액보다 많은 RAB를 소각할 수 없어야 함", async function () {
      // addr1은 초기에 0 RAB를 가지고 있음
      // 잔액이 없는데 소각을 시도하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).burn(1)
      ).to.be.revertedWith("ERC20: burn amount exceeds balance");
    });
  });

  describe("이벤트", function () {
    it("전송 시 Transfer 이벤트를 발생시켜야 함", async function () {
      const transferAmount = ethers.parseUnits("50", 0);
      
      // Transfer 이벤트 확인
      await expect(rabbitCoin.transfer(addr1.address, transferAmount))
        .to.emit(rabbitCoin, "Transfer")
        .withArgs(owner.address, addr1.address, transferAmount);
    });

    it("승인 시 Approval 이벤트를 발생시켜야 함", async function () {
      const approveAmount = ethers.parseUnits("100", 0);
      
      // Approval 이벤트 확인
      await expect(rabbitCoin.approve(addr1.address, approveAmount))
        .to.emit(rabbitCoin, "Approval")
        .withArgs(owner.address, addr1.address, approveAmount);
    });

    it("민팅 시 Transfer 이벤트를 발생시켜야 함", async function () {
      const mintAmount = ethers.parseUnits("1000", 0);
      
      // 민팅 시 Transfer 이벤트 확인 (from은 0 주소)
      await expect(rabbitCoin.mint(addr1.address, mintAmount))
        .to.emit(rabbitCoin, "Transfer")
        .withArgs(ethers.ZeroAddress, addr1.address, mintAmount);
    });

    it("소각 시 Transfer 이벤트를 발생시켜야 함", async function () {
      // 먼저 addr1에게 RAB를 전송 (사용자 주소에서 소각할 수 있도록 RAB 전송)
      const transferAmount = ethers.parseUnits("100", 0);
      await rabbitCoin.transfer(addr1.address, transferAmount);
      
      const burnAmount = ethers.parseUnits("50", 0);
      
      // 소각 시 Transfer 이벤트 확인 (to는 0 주소)
      await expect(rabbitCoin.connect(addr1).burn(burnAmount))
        .to.emit(rabbitCoin, "Transfer")
        .withArgs(addr1.address, ethers.ZeroAddress, burnAmount);
    });
  });

  describe("소유권", function () {
    it("소유자가 바뀌면 새 소유자만 민팅할 수 있어야 함", async function () {
      // 컨트랙트 소유권 이전
      await rabbitCoin.transferOwnership(addr1.address);
      
      // 기존 소유자는 민팅 불가
      await expect(
        rabbitCoin.mint(addr2.address, 100)
      ).to.be.revertedWithCustomError(rabbitCoin, "OwnableUnauthorizedAccount")
       .withArgs(owner.address);
      
      // 새 소유자는 민팅 가능
      await rabbitCoin.connect(addr1).mint(addr2.address, 100);
      expect(await rabbitCoin.balanceOf(addr2.address)).to.equal(100);
    });
  });

});