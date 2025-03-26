const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("PromissoryNote", function () {
    let rabbitCoin;
    let promissoryNote;
    let owner;
    let creditor;
    let debtor;

    // 상환 유형 상수
    const REPAY_TYPE = {
        EPIP: "EPIP", // 원리금 균등 상환
        EPP: "EPP",   // 원금 균등 상환
        BP: "BP"      // 만기 일시 상환
    };

    // 테스트용 메타데이터 생성 함수
    function createTestMetadata(creditorAddress, debtorAddress) {
        return {
            crInfo: {
                crSign: "creditor-signature-hash",
                crName: "채권자이름",
                crWalletAddress: creditorAddress,
                crInfoHash: "creditor-info-hash"
            },
            drInfo: {
                drSign: "debtor-signature-hash",
                drName: "채무자이름",
                drWalletAddress: debtorAddress,
                drInfoHash: "debtor-info-hash"
            },
            nftImage: "data:image/svg+xml;base64,testimage",
            la: 1000,  // 1000 RAB 토큰
            ir: 500,  // 연 5% 이자율 (100 = 1%)
            lt: 12,   // 12개월 (대출 기간)
            repayType: REPAY_TYPE.EPIP, // 원리금 균등상환
            matDt: "2025-03-25",  // 만기일
            mpDt: 25,             // 이자 납부일 (매월 25일)
            dir: 0,               // 연체 이자율 0% (필요시 설정)
            contractDate: "2024-03-25", // 계약일
            earlyPayFlag: true,   // 조기 상환 가능
            earlyPayFee: 100,     // 조기 상환 수수료 1% (100 = 1%)
            defCnt: 0,            // 연체 횟수
            addTerms: {
                addTerms: "추가 약정 사항이 없습니다.",
                addTermsHash: "additional-terms-hash"
            }
        };
    }

    // 서명 생성 함수
    async function signTransferMessage(signer, from, to, amount, nonce, deadline, verifyingContract) {
        const domain = {
            name: "PromissoryNote",
            version: "1",
            chainId: (await ethers.provider.getNetwork()).chainId,
            verifyingContract: verifyingContract,
        };
    
        const types = {
            Transfer: [
                { name: "from", type: "address" },
                { name: "to", type: "address" },
                { name: "amount", type: "uint256" },
                { name: "nonce", type: "uint256" },
                { name: "deadline", type: "uint256" },
            ],
        };
    
        const value = {
            from,
            to,
            amount,
            nonce,
            deadline,
        };
    
        return await signer.signTypedData(domain, types, value);
    }

    beforeEach(async function () {
        // 계정 설정
        [owner, creditor, debtor] = await ethers.getSigners();

        // RAB 토큰 컨트랙트 배포
        const RabbitCoin = await ethers.getContractFactory("RabbitCoin");
        rabbitCoin = await RabbitCoin.deploy(1000000);
        await rabbitCoin.waitForDeployment();

        const rabbitCoinAddress = await rabbitCoin.getAddress();
        console.log("RabbitCoin address:", rabbitCoinAddress);

        // 테스트를 위해 creditor에게 토큰 민팅
        await rabbitCoin.mint(creditor.address, 10000);

        // PromissoryNote 컨트랙트 배포
        const PromissoryNote = await ethers.getContractFactory("PromissoryNote");
        promissoryNote = await PromissoryNote.deploy(rabbitCoinAddress);
        await promissoryNote.waitForDeployment();

        const promissoryNoteAddress = await promissoryNote.getAddress();
        console.log("PromissoryNote address:", promissoryNoteAddress);

        // creditor가 컨트랙트에 토큰 사용 권한 부여
        await rabbitCoin.connect(creditor).approve(
            promissoryNoteAddress,
            10000
        );
    });

    describe("배포", function () {
        it("올바른 컨트랙트 이름과 심볼이 설정되어야 함", async function () {
            expect(await promissoryNote.name()).to.equal("PromissoryNote");
            expect(await promissoryNote.symbol()).to.equal("PNFT");
        });

        it("올바른 RAB 토큰 주소가 설정되어야 함", async function () {
            expect(await promissoryNote.rabbitCoinAddress()).to.equal(await rabbitCoin.getAddress());
        });

        it("토큰 ID 카운터가 1로 초기화되어야 함", async function () {
            expect(await promissoryNote.tokenIdCounter()).to.equal(1);
        });
    });

    describe("NFT 발행", function () {
        it("원리금 균등 상환 방식으로 NFT 발행 및 코인 이체 테스트", async function () {
            // 현재 시간 + 1시간을 deadline으로 설정
            const deadline = Math.floor(Date.now() / 1000) + 3600;

            // 메타데이터 생성 (명시적으로 원리금 균등 상환 방식 표시)
            const metadata = createTestMetadata(creditor.address, debtor.address);
            metadata.repayType = REPAY_TYPE.EPIP; // 원리금 균등 상환임을 명시

            // creditor의 현재 nonce 조회
            const nonce = await promissoryNote.getNonce(creditor.address);

            // creditor가 전송 메시지에 서명
            const signature = await signTransferMessage(
                creditor,
                creditor.address,
                debtor.address,
                metadata.la,
                nonce,
                deadline,
                await promissoryNote.getAddress()
            );
            
            // 발행 전 잔액 확인
            const creditorBalanceBefore = await rabbitCoin.balanceOf(creditor.address);
            const debtorBalanceBefore = await rabbitCoin.balanceOf(debtor.address);

            // 차용증 NFT 발행
            const tx = await promissoryNote.mintPromissoryNoteWithSignature(
                metadata,
                signature,
                deadline
            );

            const receipt = await tx.wait();

            // 이벤트에서 tokenId 추출
            const [event] = await promissoryNote.queryFilter(
                promissoryNote.filters.PromissoryNoteMinted(),
                receipt.blockNumber,
                receipt.blockNumber
            );
            const tokenId = event.args.tokenId;

            // NFT가 채권자에게 발행되었는지 확인
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(creditor.address);

            // 토큰이 채무자에게 이체되었는지 확인
            const creditorBalanceAfter = await rabbitCoin.balanceOf(creditor.address);
            const debtorBalanceAfter = await rabbitCoin.balanceOf(debtor.address);

            expect(creditorBalanceBefore - creditorBalanceAfter).to.equal(BigInt(metadata.la));
            expect(debtorBalanceAfter - debtorBalanceBefore).to.equal(BigInt(metadata.la));

            // 메타데이터가 정확히 저장되었는지 확인
            const storedMetadata = await promissoryNote.getPromissoryMetadata(tokenId);
            expect(storedMetadata.crInfo.crName).to.equal(metadata.crInfo.crName);
            expect(storedMetadata.drInfo.drName).to.equal(metadata.drInfo.drName);
            expect(storedMetadata.la.toString()).to.equal(metadata.la.toString());

            // tokenURI가 올바르게 생성되는지 확인
            const tokenURI = await promissoryNote.tokenURI(tokenId);
            expect(tokenURI).to.include("data:application/json;base64,");
        });

        it("원금 균등 상환 방식으로 NFT 발행 테스트", async function () {
            const deadline = Math.floor(Date.now() / 1000) + 3600;

            // 메타데이터 생성 (원금 균등 상환 방식)
            const metadata = createTestMetadata(creditor.address, debtor.address);
            metadata.repayType = REPAY_TYPE.EPP; // 원금 균등 상환

            const nonce = await promissoryNote.getNonce(creditor.address);
            const signature = await signTransferMessage(
                creditor,
                creditor.address,
                debtor.address,
                metadata.la,
                nonce,
                deadline,
                await promissoryNote.getAddress()
            );

            // NFT 발행
            const tx = await promissoryNote.mintPromissoryNoteWithSignature(
                metadata,
                signature,
                deadline
            );

            const receipt = await tx.wait();

            const [event] = await promissoryNote.queryFilter(
                promissoryNote.filters.PromissoryNoteMinted(),
                receipt.blockNumber,
                receipt.blockNumber
            );
            const tokenId = event.args.tokenId;

            // 저장된 메타데이터의 상환 방식 확인
            const storedMetadata = await promissoryNote.getPromissoryMetadata(tokenId);
            expect(storedMetadata.repayType).to.equal(REPAY_TYPE.EPP);
        });

        it("만기 일시 상환 방식으로 NFT 발행 테스트", async function () {
            const deadline = Math.floor(Date.now() / 1000) + 3600;

            // 메타데이터 생성 (만기 일시 상환 방식)
            const metadata = createTestMetadata(creditor.address, debtor.address);
            metadata.repayType = REPAY_TYPE.BP; // 만기 일시 상환

            const nonce = await promissoryNote.getNonce(creditor.address);
            const signature = await signTransferMessage(
                creditor,
                creditor.address,
                debtor.address,
                metadata.la,
                nonce,
                deadline,
                await promissoryNote.getAddress()
            );
            
            // NFT 발행
            const tx = await promissoryNote.mintPromissoryNoteWithSignature(
                metadata,
                signature,
                deadline
            );

            const receipt = await tx.wait();

            const [event] = await promissoryNote.queryFilter(
                promissoryNote.filters.PromissoryNoteMinted(),
                receipt.blockNumber,
                receipt.blockNumber
            );
            const tokenId = event.args.tokenId;

            // 저장된 메타데이터의 상환 방식 확인
            const storedMetadata = await promissoryNote.getPromissoryMetadata(tokenId);
            expect(storedMetadata.repayType).to.equal(REPAY_TYPE.BP);
        });
    });

    describe("서명 검증", function () {
        it("잘못된 서명으로 차용증 NFT 발행 시도", async function () {
            const deadline = Math.floor(Date.now() / 1000) + 3600;
            const metadata = createTestMetadata(creditor.address, debtor.address);

            // 잘못된 서명 사용 (다른 계정으로 서명)
            const wrongSignature = await signTransferMessage(
                debtor, // 채권자가 아닌 채무자가 서명
                creditor.address,
                debtor.address,
                metadata.la,
                await promissoryNote.getNonce(creditor.address),
                deadline,
                await promissoryNote.getAddress()
            );
            

            // 트랜잭션이 실패해야 함
            await expect(
                promissoryNote.mintPromissoryNoteWithSignature(
                    metadata,
                    wrongSignature,
                    deadline
                )
            ).to.be.revertedWith("Invalid creditor signature");
        });

        it("만료된 서명으로 차용증 NFT 발행 시도", async function () {
            // 이미 만료된 deadline 설정
            const expiredDeadline = Math.floor(Date.now() / 1000) - 3600; // 1시간 전

            const metadata = createTestMetadata(creditor.address, debtor.address);
            const signature = await signTransferMessage(
                creditor,
                creditor.address,
                debtor.address,
                metadata.la,
                await promissoryNote.getNonce(creditor.address),
                expiredDeadline,
                await promissoryNote.getAddress()
            );            

            // 트랜잭션이 실패해야 함
            await expect(
                promissoryNote.mintPromissoryNoteWithSignature(
                    metadata,
                    signature,
                    expiredDeadline
                )
            ).to.be.revertedWith("Signature expired");
        });
    });
});