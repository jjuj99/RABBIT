const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("PromissoryNoteAuction", function () {
    let owner, creditor, debtor, bidder;
    let rabbitCoin, promissoryNote, promissoryNoteAuction;
    let tokenId;
    const loanAmount = ethers.parseEther("1000"); // 1000 RAB
    const interestRate = 500; // 5%
    const loanTerm = 12; // 12개월
    const deadline = Math.floor(Date.now() / 1000) + 60 * 60; // 1시간 후 만료

    beforeEach(async function () {
        // 계정 설정
        [owner, creditor, debtor, bidder] = await ethers.getSigners();

        // RabbitCoin 배포
        const RabbitCoin = await ethers.getContractFactory("RabbitCoin");
        rabbitCoin = await RabbitCoin.deploy(1000000);
        await rabbitCoin.waitForDeployment();

        // PromissoryNote 배포
        const PromissoryNote = await ethers.getContractFactory("PromissoryNote");
        promissoryNote = await PromissoryNote.deploy(await rabbitCoin.getAddress());
        await promissoryNote.waitForDeployment();

        // PromissoryNoteAuction 배포
        const PromissoryNoteAuction = await ethers.getContractFactory("PromissoryNoteAuction");
        promissoryNoteAuction = await PromissoryNoteAuction.deploy(await promissoryNote.getAddress());
        await promissoryNoteAuction.waitForDeployment();

        // 채권자에게 토큰 발행
        await rabbitCoin.mint(creditor.address, loanAmount);
        await rabbitCoin.connect(creditor).approve(await promissoryNote.getAddress(), loanAmount);

        // 차용증 메타데이터 생성
        const maturityDate = new Date();
        maturityDate.setMonth(maturityDate.getMonth() + loanTerm);
        const maturityDateStr = maturityDate.toISOString().split('T')[0];
        const contractDate = new Date().toISOString().split('T')[0];

        const metadata = {
            nftImage: "ipfs://QmExample",
            crInfo: {
                crSign: "CREDITOR_SIGNATURE",
                crName: "John Creditor",
                crWalletAddress: creditor.address,
                crInfoHash: "0x123456789abcdef"
            },
            drInfo: {
                drSign: "DEBTOR_SIGNATURE",
                drName: "Jane Debtor",
                drWalletAddress: debtor.address,
                drInfoHash: "0x987654321fedcba"
            },
            la: loanAmount,
            ir: interestRate,
            lt: loanTerm,
            repayType: "BP",
            matDt: maturityDateStr,
            mpDt: 25,
            dir: 1800,
            contractDate: contractDate,
            earlyPayFlag: true,
            earlyPayFee: 200,
            accel: 3,
            addTerms: {
                addTerms: "추가 계약 조항",
                addTermsHash: "0xaddtermshash"
            }
        };

        // 채권자 서명 생성 (차용증 발행용)
        const nonce = await promissoryNote.getNonce(creditor.address);
        const transferMessageHash = await promissoryNote.getTransferMessageHash(
            creditor.address,
            debtor.address,
            loanAmount,
            nonce,
            deadline
        );

        const creditorSignature = await creditor.signTypedData(
            {
                name: "PromissoryNote",
                version: "1",
                chainId: (await ethers.provider.getNetwork()).chainId,
                verifyingContract: await promissoryNote.getAddress()
            },
            {
                Transfer: [
                    { name: "from", type: "address" },
                    { name: "to", type: "address" },
                    { name: "amount", type: "uint256" },
                    { name: "nonce", type: "uint256" },
                    { name: "deadline", type: "uint256" }
                ]
            },
            {
                from: creditor.address,
                to: debtor.address,
                amount: loanAmount,
                nonce: nonce,
                deadline: deadline
            }
        );

        // 차용증 NFT 발행
        const tx = await promissoryNote.mintPromissoryNoteWithSignature(
            metadata,
            creditorSignature,
            deadline
        );
        const receipt = await tx.wait();
        const event = receipt.logs.find(
            log => log.fragment && log.fragment.name === 'PromissoryNoteMinted'
        );
        tokenId = event.args[0]; // 발행된 토큰 ID 가져오기
    });

    describe("차용증 NFT 토큰 예치", function () {
        it("차용증 NFT를 경매 컨트랙트에 예치할 수 있어야 함", async function () {
            // 1. 초기 상태 확인
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(creditor.address);

            // 2. permit 서명 생성
            const nonce = await promissoryNote.getNonce(creditor.address);
            const permitMessageHash = await promissoryNote.getPermitMessageHash(
                creditor.address,
                await promissoryNoteAuction.getAddress(),
                tokenId,
                nonce,
                deadline
            );

            const signature = await creditor.signTypedData(
                {
                    name: "PromissoryNote",
                    version: "1",
                    chainId: (await ethers.provider.getNetwork()).chainId,
                    verifyingContract: await promissoryNote.getAddress()
                },
                {
                    Permit: [
                        { name: "owner", type: "address" },
                        { name: "spender", type: "address" },
                        { name: "tokenId", type: "uint256" },
                        { name: "nonce", type: "uint256" },
                        { name: "deadline", type: "uint256" }
                    ]
                },
                {
                    owner: creditor.address,
                    spender: await promissoryNoteAuction.getAddress(),
                    tokenId: tokenId,
                    nonce: nonce,
                    deadline: deadline
                }
            );

            // 3. depositNFTWithPermit 함수 호출
            await expect(
                promissoryNoteAuction.depositNFTWithPermit(
                    tokenId,
                    creditor.address,
                    deadline,
                    signature
                )
            )
            .to.emit(promissoryNoteAuction, "NFTDeposited");

            // 4. 예치 후 상태 검증
            // NFT 소유권이 경매 컨트랙트로 이전되었는지 확인
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(await promissoryNoteAuction.getAddress());

            // 예치자 정보가 올바르게 저장되었는지 확인
            expect(await promissoryNoteAuction.getDepositor(tokenId)).to.equal(creditor.address);
        });

        it("유효하지 않은 서명일 경우 NFT 예치 실패해야 함", async function () {
            // 잘못된 서명자(bidder)로 서명 생성
            const nonce = await promissoryNote.getNonce(creditor.address);
            const permitMessageHash = await promissoryNote.getPermitMessageHash(
                creditor.address,
                await promissoryNoteAuction.getAddress(),
                tokenId,
                nonce,
                deadline
            );

            const invalidSignature = await bidder.signMessage(ethers.getBytes(permitMessageHash));

            // 잘못된 서명으로 depositNFTWithPermit 호출 시 실패해야 함
            await expect(
                promissoryNoteAuction.depositNFTWithPermit(
                    tokenId,
                    creditor.address,
                    deadline,
                    invalidSignature
                )
            ).to.be.reverted;

            // NFT는 여전히 creditor가 소유해야 함
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(creditor.address);
        });

        it("만료 기한을 넘긴 서명일 경우 NFT 예치 실패해야 함", async function () {
            const expiredDeadline = Math.floor(Date.now() / 1000) - 3600; // 1시간 전 만료

            const nonce = await promissoryNote.getNonce(creditor.address);
            const permitMessageHash = await promissoryNote.getPermitMessageHash(
                creditor.address,
                await promissoryNoteAuction.getAddress(),
                tokenId,
                nonce,
                expiredDeadline
            );

            const signature = await creditor.signMessage(ethers.getBytes(permitMessageHash));

            // 만료된 기한으로 depositNFTWithPermit 호출 시 실패해야 함
            await expect(
                promissoryNoteAuction.depositNFTWithPermit(
                    tokenId,
                    creditor.address,
                    expiredDeadline,
                    signature
                )
            ).to.be.revertedWith("Signature expired");

            // NFT는 여전히 creditor가 소유해야 함
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(creditor.address);
        });

        it("토큰 소유자가 아닌 경우 NFT 예치 실패해야 함", async function () {
            const nonce = await promissoryNote.getNonce(debtor.address);
            const permitMessageHash = await promissoryNote.getPermitMessageHash(
                debtor.address,
                await promissoryNoteAuction.getAddress(),
                tokenId,
                nonce,
                deadline
            );

            const signature = await debtor.signMessage(ethers.getBytes(permitMessageHash));

            // debtor는 토큰 소유자가 아니므로 실패해야 함
            await expect(
                promissoryNoteAuction.depositNFTWithPermit(
                    tokenId,
                    debtor.address,
                    deadline,
                    signature
                )
            ).to.be.reverted;

            // NFT는 여전히 creditor가 소유해야 함
            expect(await promissoryNote.ownerOf(tokenId)).to.equal(creditor.address);
        });

        it("이미 예치된 NFT를 다시 예치할 수 없어야 함", async function () {
            const nonce = await promissoryNote.getNonce(creditor.address);
            const permitMessageHash = await promissoryNote.getPermitMessageHash(
                creditor.address,
                await promissoryNoteAuction.getAddress(),
                tokenId,
                nonce,
                deadline
            );
        
            const signature = await creditor.signTypedData(
                {
                    name: "PromissoryNote",
                    version: "1",
                    chainId: (await ethers.provider.getNetwork()).chainId,
                    verifyingContract: await promissoryNote.getAddress()
                },
                {
                    Permit: [
                        { name: "owner", type: "address" },
                        { name: "spender", type: "address" },
                        { name: "tokenId", type: "uint256" },
                        { name: "nonce", type: "uint256" },
                        { name: "deadline", type: "uint256" }
                    ]
                },
                {
                    owner: creditor.address,
                    spender: await promissoryNoteAuction.getAddress(),
                    tokenId: tokenId,
                    nonce: nonce,
                    deadline: deadline
                }
            );
        
            // 1차 예치 성공
            await promissoryNoteAuction.depositNFTWithPermit(tokenId, creditor.address, deadline, signature);
        
            // 2차 예치 실패
            await expect(
                promissoryNoteAuction.depositNFTWithPermit(tokenId, creditor.address, deadline, signature)
            ).to.be.reverted;
        });        
    });
});