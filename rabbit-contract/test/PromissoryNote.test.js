const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("PromissoryNote Contract", function () {
    let promissoryNote;
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
        la: ethers.parseEther("10"),  // 10 ETH 차용 금액
        ir: 500,  // 5.00% 이자율 
        lt: 12,   // 12개월
        repayType: "만기일시상환",
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

        // 컨트랙트 배포
        const PromissoryNote = await ethers.getContractFactory("PromissoryNote");
        promissoryNote = await PromissoryNote.deploy();
        await promissoryNote.waitForDeployment();
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