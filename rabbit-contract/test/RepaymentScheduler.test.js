const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("RepaymentScheduler 컨트랙트", function () {
    let promissoryNote;
    let repaymentScheduler;
    let rabbitCoin;
    let owner;
    let creditor;
    let debtor;
    let addrs;

    // 테스트 데이터 설정 (100만원 차용)
    const mockMetadata = {
        nftImage: "ipfs://QmTest",
        crInfo: {
            crSign: "크레디_서명",
            crName: "김채권",
            crWalletAddress: "",  // 배포 시 설정
            crInfoHash: "0x1234creditorInfoHash",
        },
        drInfo: {
            drSign: "데브터_서명",
            drName: "이채무",
            drWalletAddress: "",  // 배포 시 설정
            drInfoHash: "0x1234debtorInfoHash",
        },
        la: 1000000,  // 100만원 대출 금액
        ir: 500,      // 5.00% 이자율 
        lt: 12,       // 12개월
        repayType: "EPIP",  // 원리금 균등 상환
        matDt: "2025-04-03",
        mpDt: 25,  // 매월 25일
        dir: 1800, // 18.00% 연체 이자율
        contractDate: "2024-04-03",
        earlyPayFlag: true,
        earlyPayFee: 300,   // 3.00% 중도상환 수수료
        accel: 3,           // 3회 연체 시 기한이익상실
        addTerms: {
            addTerms: "추가 조항 내용",
            addTermsHash: "0x1234addTermsHash",
        }
    };

    // 테스트 실행 전 컨트랙트 배포
    beforeEach(async function () {
        [owner, creditor, debtor, ...addrs] = await ethers.getSigners();

        // 테스트 데이터에 실제 주소 설정
        mockMetadata.crInfo.crWalletAddress = creditor.address;
        mockMetadata.drInfo.drWalletAddress = debtor.address;

        // RabbitCoin 컨트랙트 배포
        const RabbitCoin = await ethers.getContractFactory("RabbitCoin");
        rabbitCoin = await RabbitCoin.deploy(1000000);
        await rabbitCoin.waitForDeployment();

        // PromissoryNote 컨트랙트 배포
        const PromissoryNote = await ethers.getContractFactory("PromissoryNote");
        promissoryNote = await PromissoryNote.deploy();
        await promissoryNote.waitForDeployment();

        // RepaymentScheduler 컨트랙트 배포
        const RepaymentScheduler = await ethers.getContractFactory("RepaymentScheduler");
        repaymentScheduler = await RepaymentScheduler.deploy(
            await promissoryNote.getAddress(),
            await rabbitCoin.getAddress()
        );
        await repaymentScheduler.waitForDeployment();

        // PromissoryNote에 스케줄러 주소 설정
        await promissoryNote.setSchedulerAddress(await repaymentScheduler.getAddress());

        // 스케줄러에 소각 권한 부여
        await promissoryNote.addBurnAuthorization(await repaymentScheduler.getAddress());

        // 채무자에게 코인 민팅 및 스케줄러에 대한 승인
        await rabbitCoin.mint(debtor.address, 10000000);  // 1000만원 충전 (충분한 자금)
        await rabbitCoin.connect(debtor).approve(await repaymentScheduler.getAddress(), 10000000);

        // 채권자에게도 약간의 코인 민팅 (잔액 비교 테스트를 위해)
        await rabbitCoin.mint(creditor.address, 1000);
    });

    describe("1. 상환 스케줄 등록 테스트", function () {
        let tokenId;

        beforeEach(async function () {
            // NFT 발행
            const tx = await promissoryNote.mint(mockMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            tokenId = promissoryNote.interface.parseLog(events[0]).args[0];
        });

        it("1.1 상환 스케줄 정보가 올바르게 등록되는지 확인", async function () {
            // 등록된 상환 정보 조회
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);

            // 상세 정보 검증
            expect(repaymentInfo.tokenId).to.equal(tokenId);
            expect(repaymentInfo.initialPrincipal).to.equal(mockMetadata.la);
            expect(repaymentInfo.remainingPrincipal).to.equal(mockMetadata.la);
            expect(repaymentInfo.ir).to.equal(mockMetadata.ir);
            expect(repaymentInfo.dir).to.equal(mockMetadata.dir);
            expect(repaymentInfo.mpDt).to.equal(mockMetadata.mpDt);
            expect(repaymentInfo.totalPayments).to.equal(mockMetadata.lt);
            expect(repaymentInfo.remainingPayments).to.equal(mockMetadata.lt);
            expect(repaymentInfo.repayType).to.equal(mockMetadata.repayType);
            expect(repaymentInfo.drWalletAddress).to.equal(mockMetadata.drInfo.drWalletAddress);
            expect(repaymentInfo.activeFlag).to.be.true;
        });

        it("1.2 활성 상환 목록에 토큰 ID가 추가되는지 확인", async function () {
            const activeRepayments = await repaymentScheduler.getActiveRepayments();
            
            expect(activeRepayments.length).to.equal(1);
            expect(activeRepayments[0]).to.equal(tokenId);
        });

        it("1.3 EPIP 방식에서 고정 납부액이 올바르게 계산되는지 확인", async function () {
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            
            // 고정 납부액이 존재하는지 확인
            expect(repaymentInfo.fixedPaymentAmount).to.be.gt(0);
            
            // 최소값 확인 (원금/총납부횟수보다 커야 함)
            const minExpectedPayment = Math.floor(mockMetadata.la / mockMetadata.lt);
            expect(Number(repaymentInfo.fixedPaymentAmount)).to.be.gt(minExpectedPayment);
        });
    });

    describe("2. 자동 이체 테스트", function () {
        let tokenId;

        beforeEach(async function () {
            // NFT 발행
            const tx = await promissoryNote.mint(mockMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            tokenId = promissoryNote.interface.parseLog(events[0]).args[0];
        });

        it("2.1 상환 처리 실행 시 상환금이 정상 이체되는지 확인", async function () {
            // 채권자 초기 잔액 확인
            const initialCreditorBalance = await rabbitCoin.balanceOf(creditor.address);
            
            // 상환 처리
            await repaymentScheduler.processRepayment(tokenId);
            
            // 채권자 최종 잔액 확인
            const finalCreditorBalance = await rabbitCoin.balanceOf(creditor.address);
            
            // 상환금이 이체되었는지 확인 (BigInt 직접 비교)
            expect(finalCreditorBalance > initialCreditorBalance).to.be.true;
            
            // 상환 정보 업데이트 확인
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            expect(repaymentInfo.remainingPayments).to.equal(mockMetadata.lt - 1);
        });
    });

    describe("3. 연체 처리 테스트", function () {
        let tokenId;

        beforeEach(async function () {
            // NFT 발행
            const tx = await promissoryNote.mint(mockMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            tokenId = promissoryNote.interface.parseLog(events[0]).args[0];
        });

        it("3.1 연체 상태로 수동 업데이트 후 상태 확인", async function () {
            const currentTime = Math.floor(Date.now() / 1000);
            const overdueStartTime = currentTime - 86400 * 5; // 5일 전
            
            // 연체 정보 수동 업데이트
            await repaymentScheduler.updateOverdueInfo(
                tokenId,
                true,                // 연체 상태
                overdueStartTime,    // 연체 시작일
                5,                   // 연체 일수
                5000,                // 누적 연체 이자
                1,                   // 현재 연체 횟수
                mockMetadata.dir,    // 현재 적용 이자율
                1                    // 총 연체 횟수
            );
            
            // 연체 정보 확인
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            expect(repaymentInfo.overdueInfo.overdueFlag).to.be.true;
            expect(repaymentInfo.overdueInfo.overdueStartDate).to.equal(overdueStartTime);
            expect(repaymentInfo.overdueInfo.overdueDays).to.equal(5);
            expect(repaymentInfo.overdueInfo.aoi).to.equal(5000);
            expect(repaymentInfo.overdueInfo.defCnt).to.equal(1);
        });

        it("3.2 연체 상태에서 상환 처리 시 연체 상태가 해제되는지 확인", async function () {
            const currentTime = Math.floor(Date.now() / 1000);
            const overdueStartTime = currentTime - 86400 * 5; // 5일 전
            
            // 연체 정보 수동 업데이트
            await repaymentScheduler.updateOverdueInfo(
                tokenId,
                true,                // 연체 상태
                overdueStartTime,    // 연체 시작일
                5,                   // 연체 일수
                5000,                // 누적 연체 이자
                1,                   // 현재 연체 횟수
                mockMetadata.dir,    // 현재 적용 이자율
                1                    // 총 연체 횟수
            );
            
            // 상환 처리
            await repaymentScheduler.processRepayment(tokenId);
            
            // 연체 상태 해제 확인
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            expect(repaymentInfo.overdueInfo.overdueFlag).to.be.false;
            expect(repaymentInfo.overdueInfo.aoi).to.equal(0);
            expect(repaymentInfo.overdueInfo.defCnt).to.equal(0);
        });

        it("3.3 연체 횟수 초과 시 최대 이자율 적용", async function () {
            // 3회 연체로 업데이트
            await repaymentScheduler.updateOverdueInfo(
                tokenId,
                true,                 // 연체 상태
                Math.floor(Date.now() / 1000) - 86400 * 10, // 10일 전
                10,                  // 연체 일수
                10000,               // 누적 연체 이자
                3,                   // 현재 연체 횟수 (기한이익상실 조건)
                2000,                // 최대 이자율 20%
                3                    // 총 연체 횟수
            );
            
            // 연체 정보 확인
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            expect(repaymentInfo.overdueInfo.defCnt).to.equal(3);
            expect(repaymentInfo.overdueInfo.currentIr).to.equal(2000); // 최대 이자율 20%
        });
    });

    describe("4. 중도 상환 테스트", function () {
        let tokenId;

        beforeEach(async function () {
            // NFT 발행
            const tx = await promissoryNote.mint(mockMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            tokenId = promissoryNote.interface.parseLog(events[0]).args[0];
        });

        it("4.1 중도 상환 수수료 계산 정확성", async function () {
            const partialPaymentAmount = 500000; // 50만원 상환
            const feeAmount = await repaymentScheduler.getEarlyRepaymentFee(tokenId, partialPaymentAmount);
        
            // 3% 수수료 계산 (500000 * 300 / 10000 = 15000)
            const expectedFee = Math.floor((partialPaymentAmount * mockMetadata.earlyPayFee) / 10000);
            expect(Number(feeAmount)).to.equal(expectedFee);
        });

        it("4.2 부분 중도 상환 처리", async function () {
            const partialPaymentAmount = 500000; // 50만원 상환
            const feeAmount = await repaymentScheduler.getEarlyRepaymentFee(tokenId, partialPaymentAmount);
        
            // 중도 상환 처리 전 채권자 잔액 확인
            const initialCreditorBalance = Number(await rabbitCoin.balanceOf(creditor.address));
            
            // 중도 상환 처리
            await repaymentScheduler.processEarlyRepayment(tokenId, partialPaymentAmount, feeAmount);
        
            // 상환 정보 확인
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            const expectedRemainingPrincipal = mockMetadata.la - partialPaymentAmount;
            
            expect(Number(repaymentInfo.remainingPrincipal)).to.be.closeTo(expectedRemainingPrincipal, 1);
            expect(repaymentInfo.activeFlag).to.be.true;
            
            // 채권자 잔액 증가 확인
            const finalCreditorBalance = Number(await rabbitCoin.balanceOf(creditor.address));
            expect(finalCreditorBalance).to.be.gt(initialCreditorBalance);
        });

        it("4.4 연체 상태에서는 중도 상환이 불가능한지 확인", async function () {
            // 연체 상태로 설정
            await repaymentScheduler.updateOverdueInfo(
                tokenId,
                true,                 // 연체 상태
                Math.floor(Date.now() / 1000) - 86400 * 5, // 5일 전
                5,                    // 연체 일수
                5000,                 // 누적 연체 이자
                1,                    // 현재 연체 횟수
                mockMetadata.dir,     // 현재 적용 이자율
                1                     // 총 연체 횟수
            );
            
            const partialPaymentAmount = 500000; // 50만원 상환
            const feeAmount = await repaymentScheduler.getEarlyRepaymentFee(tokenId, partialPaymentAmount);
            
            // 연체 중 중도 상환 시도 시 실패 확인
            await expect(
                repaymentScheduler.processEarlyRepayment(tokenId, partialPaymentAmount, feeAmount)
            ).to.be.revertedWith("Cannot process early repayment while loan is overdue");
        });
    });

    describe("5. 잔액 부족 시 예외 처리 테스트", function () {
        let tokenId;
        let emptyDebtor;

        beforeEach(async function () {
            // 빈 지갑의 채무자 생성
            emptyDebtor = addrs[0];
            
            // 빈 지갑 채무자로 메타데이터 수정
            const emptyDebtorMetadata = {...mockMetadata};
            emptyDebtorMetadata.drInfo.drWalletAddress = emptyDebtor.address;
            
            // NFT 발행
            const tx = await promissoryNote.mint(emptyDebtorMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            tokenId = promissoryNote.interface.parseLog(events[0]).args[0];
            
            // 빈 지갑이지만 approve는 설정 (실행 가능하도록)
            await rabbitCoin.mint(emptyDebtor.address, 1); // 최소 금액만 민팅
            await rabbitCoin.connect(emptyDebtor).approve(await repaymentScheduler.getAddress(), 10000000);
        });

        it("5.1 상환 처리 시 잔액 부족일 경우 이벤트 발생 확인", async function () {
            // 상환 처리 시도
            const tx = await repaymentScheduler.processRepayment(tokenId);
            const receipt = await tx.wait();
            
            // 모든 이벤트를 확인하고 InsufficientBalance 이벤트를 찾음
            let foundInsufficientBalanceEvent = false;
            
            for (const log of receipt.logs) {
                try {
                    const parsedLog = repaymentScheduler.interface.parseLog(log);
                    if (parsedLog && parsedLog.name === "InsufficientBalance") {
                        foundInsufficientBalanceEvent = true;
                        expect(parsedLog.args[1]).to.equal(emptyDebtor.address);
                        break;
                    }
                } catch (e) {
                    // 파싱 오류 무시
                }
            }
            
            expect(foundInsufficientBalanceEvent).to.be.true;
        });

        it("5.2 중도 상환 시 잔액 부족일 경우 이벤트 발생 확인", async function () {
            // 연체 상태 해제 (중도 상환을 위해)
            await repaymentScheduler.updateOverdueInfo(
                tokenId,
                false,   // 연체 상태
                0,       // 연체 시작일
                0,       // 연체 일수
                0,       // 누적 연체 이자
                0,       // 현재 연체 횟수
                mockMetadata.ir, // 현재 적용 이자율
                0        // 총 연체 횟수
            );
            
            const partialPaymentAmount = 500000; // 50만원 상환
            const feeAmount = await repaymentScheduler.getEarlyRepaymentFee(tokenId, partialPaymentAmount);
            
            // 중도 상환 처리 시도
            const tx = await repaymentScheduler.processEarlyRepayment(tokenId, partialPaymentAmount, feeAmount);
            const receipt = await tx.wait();
            
            // InsufficientBalance 이벤트 발생 확인
            const insufficientEvents = receipt.logs.filter(log => {
                try {
                    const parsedLog = repaymentScheduler.interface.parseLog(log);
                    return parsedLog && parsedLog.name === "InsufficientBalance";
                } catch (e) {
                    return false;
                }
            });
            
            // 이벤트가 발생했는지 확인
            expect(insufficientEvents.length).to.be.at.least(1);
            
            if (insufficientEvents.length > 0) {
                const parsedEvent = repaymentScheduler.interface.parseLog(insufficientEvents[0]);
                expect(parsedEvent.args[0]).to.equal(tokenId);
                expect(parsedEvent.args[1]).to.equal(emptyDebtor.address);
            }
        });
    });

    describe("6. 상환 유형별 테스트", function () {
        async function setupLoan(repayType) {
            const modifiedMetadata = { ...mockMetadata, repayType };
            
            // NFT 발행
            const tx = await promissoryNote.mint(modifiedMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            return promissoryNote.interface.parseLog(events[0]).args[0];
        }

        it("6.1 원리금 균등 상환(EPIP) 로직 정상 작동", async function () {
            const tokenId = await setupLoan("EPIP");
            
            // 초기 고정 납부액 확인
            const initialInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            
            // 고정 납부액이 설정되어 있는지 확인 (문자열로 변환 후 비교)
            const fixedPaymentBigInt = BigInt(initialInfo.fixedPaymentAmount.toString());
            expect(fixedPaymentBigInt > 0n).to.be.true;
            
            // 1회 상환 확인
            await repaymentScheduler.processRepayment(tokenId);
            const updatedInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            
            // 납부 횟수는 정수 비교
            const remainingPaymentsBefore = Number(initialInfo.remainingPayments);
            const remainingPaymentsAfter = Number(updatedInfo.remainingPayments);
            expect(remainingPaymentsAfter).to.equal(remainingPaymentsBefore - 1);
            
            // 원금은 BigInt 비교
            const principalBefore = BigInt(initialInfo.remainingPrincipal.toString());
            const principalAfter = BigInt(updatedInfo.remainingPrincipal.toString());
            expect(principalAfter < principalBefore).to.be.true;
        });

        it("6.2 원금 균등 상환(EPP) 로직 정상 작동", async function () {
            const tokenId = await setupLoan("EPP");
            
            // 초기 정보 확인
            const initialInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            const initialPrincipal = Number(initialInfo.remainingPrincipal);
            
            // 1회 상환 확인
            await repaymentScheduler.processRepayment(tokenId);
            const updatedInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            
            // 원금이 균등하게 감소했는지 확인 (원금 / 납부 횟수만큼 감소)
            const expectedPrincipalReduction = Math.floor(initialPrincipal / mockMetadata.lt);
            const actualPrincipalReduction = initialPrincipal - Number(updatedInfo.remainingPrincipal);
            
            // 오차 범위 내에서 확인 (이자 계산 시 소수점 처리로 인한 차이 허용)
            expect(actualPrincipalReduction).to.be.closeTo(expectedPrincipalReduction, 10);
        });

        it("6.3 만기 일시 상환(BP) 로직 정상 작동", async function () {
            const tokenId = await setupLoan("BP");
            
            // 초기 정보 확인
            const initialInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            
            // 1회 상환 확인 (이자만 상환)
            await repaymentScheduler.processRepayment(tokenId);
            const updatedInfo = await repaymentScheduler.getRepaymentInfo(tokenId);
            
            // 원금이 그대로인지 확인 (이자만 납부) - 문자열로 변환하여 비교
            const initialPrincipal = initialInfo.remainingPrincipal.toString();
            const updatedPrincipal = updatedInfo.remainingPrincipal.toString();
            expect(updatedPrincipal).to.equal(initialPrincipal);
            
            // 납부 횟수 비교
            const remainingPaymentsBefore = Number(initialInfo.remainingPayments);
            const remainingPaymentsAfter = Number(updatedInfo.remainingPayments);
            expect(remainingPaymentsAfter).to.equal(remainingPaymentsBefore - 1);
        });
    });
});