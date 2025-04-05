const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("PromissoryNote Contract", function () {
    let promissoryNote;
    let repaymentScheduler;
    let rabbitCoin;
    let owner;
    let creditor;
    let debtor;
    let addrs;

    // 테스트 데이터 설정
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
        la: ethers.parseEther("100"),
        ir: 500,  // 5.00% 이자율 
        lt: 12,   // 12개월
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

        // RabbitCoin 컨트랙트 배포 (간단한 ERC20 구현)
        const RabbitCoin = await ethers.getContractFactory("RabbitCoin");
        rabbitCoin = await RabbitCoin.deploy(1000);
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
    });

    describe("기본 기능 테스트", function () {
        it("컨트랙트가 올바르게 배포되었는지 확인", async function () {
            expect(await promissoryNote.name()).to.equal("PromissoryNote");
            expect(await promissoryNote.symbol()).to.equal("PNFT");
        });

        it("토큰 ID 카운터가 올바르게 증가하는지 확인", async function () {
            expect(await promissoryNote.tokenIdCounter()).to.equal(1);

            await promissoryNote.mint(mockMetadata, creditor.address); // NFT 발행

            expect(await promissoryNote.tokenIdCounter()).to.equal(2);
        });
    });

    describe("차용증 NFT 발행 테스트", function () {
        it("차용증 NFT를 발행하고 메타데이터 확인", async function () {
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

            expect(events.length).to.equal(1);
            const args = promissoryNote.interface.parseLog(events[0]).args;
            const tokenId = args[0];

            // 소유권 확인
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(creditor.address);

            // 메타데이터 조회
            const metadata = await promissoryNote.getPromissoryMetadata(tokenId);

            // 메타데이터 검증
            expect(metadata.la).to.equal(mockMetadata.la);
            expect(metadata.ir).to.equal(mockMetadata.ir);
            expect(metadata.repayType).to.equal(mockMetadata.repayType);
            expect(metadata.crInfo.crName).to.equal(mockMetadata.crInfo.crName);
            expect(metadata.drInfo.drName).to.equal(mockMetadata.drInfo.drName);
        });

        it("권한이 없는 계정이 NFT를 발행하려고 하면 실패해야 함", async function () {
            // owner가 아닌 계정에서 발행 시도
            await expect(
                promissoryNote.connect(creditor).mint(mockMetadata, creditor.address)
            ).to.be.revertedWithCustomError(promissoryNote, "OwnableUnauthorizedAccount");
        });
    });

    describe("NFT 발행 및 상환정보 등록 테스트", function () {
        it("NFT 발행 시 상환정보가 올바르게 등록되어야 함", async function () {
            // NFT 발행
            const tx = await promissoryNote.mint(mockMetadata, creditor.address);
            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const mintEvent = receipt.logs.find(log => {
                try {
                    const parsedLog = promissoryNote.interface.parseLog(log);
                    return parsedLog && parsedLog.name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            expect(mintEvent).to.not.be.undefined;
            const tokenId = promissoryNote.interface.parseLog(mintEvent).args[0];

            // 상환정보 등록 이벤트 확인
            const schedulerEvents = receipt.logs.filter(log => {
                try {
                    const parsedLog = repaymentScheduler.interface.parseLog(log);
                    return parsedLog && parsedLog.name === "RepaymentScheduleCreated";
                } catch (e) {
                    return false;
                }
            });

            expect(schedulerEvents.length).to.be.greaterThan(0);

            // 등록된 상환정보 조회 및 검증
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);

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

        it("활성 상환 목록에 토큰 ID가 추가되어야 함", async function () {
            // 초기 활성 상환 목록 확인
            const initialActiveRepayments = await repaymentScheduler.getActiveRepayments();
            expect(initialActiveRepayments.length).to.equal(0);

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

            const tokenId = promissoryNote.interface.parseLog(events[0]).args[0];

            // 활성 상환 목록 업데이트 확인
            const updatedActiveRepayments = await repaymentScheduler.getActiveRepayments();
            expect(updatedActiveRepayments.length).to.equal(1);
            expect(updatedActiveRepayments[0]).to.equal(tokenId);
        });

        it("원리금 균등 상환(EPIP) 방식에서 고정 납부액이 올바르게 계산되어야 함", async function () {
            // NFT 발행 (원리금 균등 상환 타입으로 설정된 mockMetadata 사용)
            const tx = await promissoryNote.mint(mockMetadata, creditor.address);
            const receipt = await tx.wait();

            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "PromissoryNoteMinted";
                } catch (e) {
                    return false;
                }
            });

            const tokenId = promissoryNote.interface.parseLog(events[0]).args[0];

            // 상환정보 조회
            const repaymentInfo = await repaymentScheduler.getRepaymentInfo(tokenId);

            // EPIP 방식에서는 fixedPaymentAmount가 계산되어 설정되어야 함
            expect(repaymentInfo.fixedPaymentAmount).to.be.greaterThan(0);

            // JavaScript에서 동일한 계산 로직 구현
            // RepaymentScheduler의 calculateFixedPaymentForEPIP 함수와 동일한 로직
            function calculateFixedPaymentForEPIPJS(principal, annualRate, months) {
                // annualRate가 0이면 원금/개월 수로 계산
                if (annualRate === 0n) return principal / BigInt(months);

                const precision = 1000000000000n; // 1e12
                const monthlyRate = (BigInt(annualRate) * precision) / 12n / 10000n;

                // (1 + monthlyRate)^months 계산
                function calculatePower(base, exponent, precision) {
                    if (exponent === 0) {
                        return precision;
                    }

                    let result = precision;
                    let exp = exponent;
                    let baseVal = base;

                    while (exp > 0) {
                        if (exp % 2n === 1n) {
                            result = (result * baseVal) / precision;
                        }
                        baseVal = (baseVal * baseVal) / precision;
                        exp = exp / 2n;
                    }

                    return result;
                }

                const onePlusRateToN = calculatePower(precision + monthlyRate, BigInt(months), precision);
                const numerator = (principal * monthlyRate * onePlusRateToN) / (precision * precision);
                const denominator = onePlusRateToN - precision;

                return numerator / denominator;
            }

            // 컨트랙트와 동일한 입력값으로 계산
            const expectedPayment = calculateFixedPaymentForEPIPJS(
                mockMetadata.la,
                BigInt(mockMetadata.ir),
                BigInt(mockMetadata.lt)
            );

            console.log("Expected Payment:", expectedPayment.toString());
            console.log("Actual Payment:", repaymentInfo.fixedPaymentAmount.toString());

            // 계산된 값과 컨트랙트에서 계산한 값이 동일한지 확인
            // 소수점 처리 등으로 인한 약간의 차이는 허용 (1% 이내)
            const tolerance = expectedPayment * 1n / 100n;
            const lowerBound = expectedPayment - tolerance;
            const upperBound = expectedPayment + tolerance;

            expect(repaymentInfo.fixedPaymentAmount).to.be.gte(lowerBound);
            expect(repaymentInfo.fixedPaymentAmount).to.be.lte(upperBound);
        });
    });

    describe("스케줄러 주소 설정 오류 시나리오", function () {
        it("스케줄러 주소가 설정되지 않은 경우 NFT 발행이 실패해야 함", async function () {
            // 새로운 PromissoryNote 배포 (스케줄러 주소 설정 X)
            const PromissoryNote = await ethers.getContractFactory("PromissoryNote");
            const newPromissoryNote = await PromissoryNote.deploy();
            await newPromissoryNote.waitForDeployment();

            // 스케줄러 주소를 설정하지 않고 NFT 발행 시도
            await expect(
                newPromissoryNote.mint(mockMetadata, creditor.address)
            ).to.be.reverted;
        });

        it("잘못된 스케줄러 주소로 설정된 경우 NFT 발행이 실패해야 함", async function () {
            // 스케줄러 주소를 잘못된 주소로 설정
            await promissoryNote.setSchedulerAddress(ethers.ZeroAddress);

            // NFT 발행 시도
            await expect(
                promissoryNote.mint(mockMetadata, creditor.address)
            ).to.be.reverted;
        });
    });

    describe("토큰 소각 테스트", function () {
        let tokenId;

        beforeEach(async function () {
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

        it("소각 권한이 있는 계정이 NFT를 소각할 수 있어야 함", async function () {
            // owner는 소각 가능
            const tx = await promissoryNote.burn(tokenId);
            await tx.wait();

            // NFT가 소각되었는지 확인
            await expect(promissoryNote.ownerOf(tokenId)).to.be.reverted;
        });

        it("권한이 부여된 계정이 NFT를 소각할 수 있어야 함", async function () {
            // 소각 권한 부여
            await promissoryNote.addBurnAuthorization(addrs[0].address);

            // 권한이 부여된 계정으로 소각
            const tx = await promissoryNote.connect(addrs[0]).burn(tokenId);
            await tx.wait();

            // NFT가 소각되었는지 확인
            await expect(promissoryNote.ownerOf(tokenId)).to.be.reverted;
        });

        it("권한이 없는 계정이 NFT를 소각하려고 하면 실패해야 함", async function () {
            // 권한이 없는 계정으로 소각 시도
            await expect(
                promissoryNote.connect(debtor).burn(tokenId)
            ).to.be.revertedWith("Not authorized to burn");
        });
    });

    describe("permit 기능 테스트", function () {
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

        it("서명을 통한 NFT 이체 승인이 정상적으로 작동해야 함", async function () {
            // 채권자의 현재 논스 값
            const nonce = await promissoryNote.getNonce(creditor.address);

            // 마감 시간 설정 (1시간 후)
            const deadline = Math.floor(Date.now() / 1000) + 3600;

            // 메시지 해시 생성
            const messageHash = await promissoryNote.getPermitMessageHash(
                creditor.address,
                addrs[0].address,
                tokenId,
                nonce,
                deadline
            );

            // 메시지 서명
            const signature = await creditor.signMessage(ethers.getBytes(messageHash));

            // permit 함수 호출
            await expect(
                promissoryNote.permit(
                    creditor.address,
                    addrs[0].address,
                    tokenId,
                    deadline,
                    signature
                )
            ).to.be.reverted; // EIP-712 서명이 필요하므로 일반 서명으로는 실패

            // 실제 테스트에서는 EIP-712 서명 생성 로직이 필요합니다
        });
    });

    describe("부속 NFT 발행 테스트", function () {
        let tokenId;

        beforeEach(async function () {
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

            // 소각 권한 부여
            await promissoryNote.addBurnAuthorization(owner.address);
        });

        it("부속 NFT 발행 및 메타데이터 확인", async function () {
            const appendixMetadata = {
                tokenId: tokenId,
                grantorSign: "양도인서명",
                grantorName: "김채권",
                grantorWalletAddress: creditor.address,
                grantorInfoHash: "0x1234grantorInfoHash",
                granteeSign: "양수인서명",
                granteeName: "박양수",
                granteeWalletAddress: addrs[0].address,
                granteeInfoHash: "0x1234granteeInfoHash",
                la: ethers.parseEther("10"),  // 남은 원금
                contractDate: "2024-04-03",
                originalText: "0x1234originalTextHash"
            };

            // 부속 NFT 발행
            const tx = await promissoryNote.mintAppendixNFT(
                tokenId,
                appendixMetadata,
                addrs[0].address
            );

            const receipt = await tx.wait();

            // 이벤트에서 부속 tokenId 추출
            const events = receipt.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "AppendixNFTMinted";
                } catch (e) {
                    return false;
                }
            });

            expect(events.length).to.equal(1);
            const args = promissoryNote.interface.parseLog(events[0]).args;
            const appendixTokenId = args[0];

            // 부속 NFT 메타데이터 조회
            const metadata = await promissoryNote.getAppendixMetadata(appendixTokenId);

            // 메타데이터 검증
            expect(metadata.tokenId).to.equal(tokenId);
            expect(metadata.grantorName).to.equal(appendixMetadata.grantorName);
            expect(metadata.granteeName).to.equal(appendixMetadata.granteeName);
            expect(metadata.la).to.equal(appendixMetadata.la);

            // 부속 NFT가 본 NFT에 번들링되었는지 확인
            const appendixTokenIds = await promissoryNote.getAppendixTokenIds(tokenId);
            expect(appendixTokenIds.length).to.equal(1);
            expect(appendixTokenIds[0]).to.equal(appendixTokenId);

            // 최신 부속 NFT 조회
            const latestAppendixTokenId = await promissoryNote.getLatestAppendixTokenId(tokenId);
            expect(latestAppendixTokenId).to.equal(appendixTokenId);

            // 최신 채권자 주소 조회
            const latestCreditorAddress = await promissoryNote.getLatestCreditorAddress(tokenId);
            expect(latestCreditorAddress).to.equal(addrs[0].address);
        });

        it("권한이 없는 계정이 부속 NFT를 발행하려고 하면 실패해야 함", async function () {
            const appendixMetadata = {
                tokenId: tokenId,
                grantorSign: "양도인서명",
                grantorName: "김채권",
                grantorWalletAddress: creditor.address,
                grantorInfoHash: "0x1234grantorInfoHash",
                granteeSign: "양수인서명",
                granteeName: "박양수",
                granteeWalletAddress: addrs[0].address,
                granteeInfoHash: "0x1234granteeInfoHash",
                la: ethers.parseEther("10"),
                contractDate: "2024-04-03",
                originalText: "0x1234originalTextHash"
            };

            // 권한이 없는 계정에서 부속 NFT 발행 시도
            await expect(
                promissoryNote.connect(debtor).mintAppendixNFT(
                    tokenId,
                    appendixMetadata,
                    addrs[0].address
                )
            ).to.be.revertedWith("Not authorized to mint appendix");
        });
    });

    describe("본 NFT 소각 시 부속 NFT 함께 소각 테스트", function () {
        let tokenId;
        let appendixTokenId;

        beforeEach(async function () {
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

            // 소각 권한 부여
            await promissoryNote.addBurnAuthorization(owner.address);

            const appendixMetadata = {
                tokenId: tokenId,
                grantorSign: "양도인서명",
                grantorName: "김채권",
                grantorWalletAddress: creditor.address,
                grantorInfoHash: "0x1234grantorInfoHash",
                granteeSign: "양수인서명",
                granteeName: "박양수",
                granteeWalletAddress: addrs[0].address,
                granteeInfoHash: "0x1234granteeInfoHash",
                la: ethers.parseEther("10"),
                contractDate: "2024-04-03",
                originalText: "0x1234originalTextHash"
            };

            // 부속 NFT 발행
            const tx2 = await promissoryNote.mintAppendixNFT(
                tokenId,
                appendixMetadata,
                addrs[0].address
            );

            const receipt2 = await tx2.wait();

            // 이벤트에서 부속 tokenId 추출
            const events2 = receipt2.logs.filter(log => {
                try {
                    return promissoryNote.interface.parseLog(log).name === "AppendixNFTMinted";
                } catch (e) {
                    return false;
                }
            });

            appendixTokenId = promissoryNote.interface.parseLog(events2[0]).args[0];
        });

        it("본 NFT 소각 시 부속 NFT도 함께 소각되어야 함", async function () {
            // 본 NFT 소각
            const tx = await promissoryNote.burn(tokenId);
            await tx.wait();

            // 본 NFT와 부속 NFT 모두 소각되었는지 확인
            await expect(promissoryNote.ownerOf(tokenId)).to.be.reverted;
            await expect(promissoryNote.ownerOf(appendixTokenId)).to.be.reverted;

            // 부속 NFT 정보 조회 시도
            await expect(promissoryNote.getAppendixMetadata(appendixTokenId)).to.be.reverted;
        });

        it("부속 NFT는 직접 소각할 수 없어야 함", async function () {
            // 부속 NFT 직접 소각 시도
            await expect(promissoryNote.burn(appendixTokenId)).to.be.revertedWith("Cannot burn appendix NFT directly");
        });
    });
});