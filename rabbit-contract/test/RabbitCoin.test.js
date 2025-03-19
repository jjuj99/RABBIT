const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("RabbitCoin", function () {
  let RabbitCoin;
  let rabbitCoin;
  let owner;
  let addr1;
  let addr2;
  let addrs;
  
  // 초기 공급량을 1,000,000 토큰으로 설정
  const initialSupply = 1000000;

  beforeEach(async function () {
    // 컨트랙트 및 계정 설정
    RabbitCoin = await ethers.getContractFactory("RabbitCoin");
    [owner, addr1, addr2, ...addrs] = await ethers.getSigners();
    
    // 컨트랙트 배포
    rabbitCoin = await RabbitCoin.deploy(initialSupply);
  });

  describe("배포", function () {
    it("올바른 토큰 이름, 심볼, 소수점 자리수를 설정해야 함", async function () {
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
  });

  describe("트랜잭션", function () {
    it("토큰을 한 계정에서 다른 계정으로 전송할 수 있어야 함", async function () {
      // owner에서 addr1로 50 토큰 전송
      const transferAmount = ethers.parseUnits("50", 0);
      await rabbitCoin.transfer(addr1.address, transferAmount);
      
      // addr1의 잔액이 50이어야 함
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(transferAmount);
    });

    it("잔액이 부족할 때 전송이 실패해야 함", async function () {
      // addr1은 초기에 0 토큰을 가지고 있음
      const initialBalance = await rabbitCoin.balanceOf(addr1.address);
      
      // addr1이 1 토큰을 전송하려 하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).transfer(addr2.address, 1)
      ).to.be.revertedWith("ERC20: transfer amount exceeds balance");
    });

    it("approval과 transferFrom이 올바르게 작동해야 함", async function () {
      // owner가 addr1에게 100 토큰을 사용할 수 있도록 승인
      const approveAmount = ethers.parseUnits("100", 0);
      await rabbitCoin.approve(addr1.address, approveAmount);
      
      // 승인된 금액 확인
      expect(await rabbitCoin.allowance(owner.address, addr1.address)).to.equal(approveAmount);
      
      // addr1이 owner의 토큰을 addr2에게 전송
      const transferAmount = ethers.parseUnits("50", 0);
      await rabbitCoin.connect(addr1).transferFrom(owner.address, addr2.address, transferAmount);
      
      // 잔액 확인
      expect(await rabbitCoin.balanceOf(addr2.address)).to.equal(transferAmount);
      
      // 남은a allowance 확인
      expect(await rabbitCoin.allowance(owner.address, addr1.address)).to.equal(approveAmount - transferAmount);
    });
  });

  describe("민팅과 소각", function () {
    it("소유자가 새 토큰을 발행할 수 있어야 함", async function () {
      const mintAmount = ethers.parseUnits("1000", 0);
      const initialSupplyBN = ethers.parseUnits(initialSupply.toString(), 0);
      
      await rabbitCoin.mint(addr1.address, mintAmount);
      
      // addr1의 잔액이 민팅된 금액과 일치해야 함
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(mintAmount);
      
      // 총 공급량이 증가해야 함
      expect(await rabbitCoin.totalSupply()).to.equal(initialSupplyBN + mintAmount);
    });

    it("소유자가 아닌 계정은 민팅을 할 수 없어야 함", async function () {
      const mintAmount = ethers.parseUnits("1000", 0);
      
      // addr1이 민팅을 시도하면 실패해야 함
      await expect(
        rabbitCoin.connect(addr1).mint(addr2.address, mintAmount)
      ).to.be.reverted; // "Ownable: caller is not the owner"와 같은 에러 메시지
    });

    it("사용자가 토큰을 소각할 수 있어야 함", async function () {
      // 먼저 addr1에게 토큰 전송
      const transferAmount = ethers.parseUnits("100", 0);
      await rabbitCoin.transfer(addr1.address, transferAmount);
      
      // addr1이 50 토큰을 소각
      const burnAmount = ethers.parseUnits("50", 0);
      await rabbitCoin.connect(addr1).burn(burnAmount);
      
      // addr1의 잔액이 감소해야 함
      expect(await rabbitCoin.balanceOf(addr1.address)).to.equal(transferAmount - burnAmount);
      
      // 총 공급량이 감소해야 함
      const initialSupplyBN = ethers.parseUnits(initialSupply.toString(), 0);
      expect(await rabbitCoin.totalSupply()).to.equal(initialSupplyBN - burnAmount);
    });

    it("잔액보다 많은 토큰을 소각할 수 없어야 함", async function () {
      // addr1은 초기에 0 토큰을 가지고 있음
      
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
      // 먼저 addr1에게 토큰 전송
      const transferAmount = ethers.parseUnits("100", 0);
      await rabbitCoin.transfer(addr1.address, transferAmount);
      
      const burnAmount = ethers.parseUnits("50", 0);
      
      // 소각 시 Transfer 이벤트 확인 (to는 0 주소)
      await expect(rabbitCoin.connect(addr1).burn(burnAmount))
        .to.emit(rabbitCoin, "Transfer")
        .withArgs(addr1.address, ethers.ZeroAddress, burnAmount);
    });
  });
});