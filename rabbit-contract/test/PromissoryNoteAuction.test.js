const { expect } = require("chai");
const { ethers } = require("hardhat");

describe("PromissoryNoteAuction", function () {
    let owner, creditor, debtor, bidder;
    let rabbitCoin, promissoryNote, promissoryNoteAuction;
    let tokenId;
    const loanAmount = 1000; // 1000 RAB
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
        promissoryNote = await PromissoryNote.deploy();
        await promissoryNote.waitForDeployment();

        // PromissoryNoteAuction 배포
        const PromissoryNoteAuction = await ethers.getContractFactory("PromissoryNoteAuction");
        promissoryNoteAuction = await PromissoryNoteAuction.deploy(await rabbitCoin.getAddress(), await promissoryNote.getAddress());
        await promissoryNoteAuction.waitForDeployment();

        // PromissoryNote 컨트랙트에 경매 컨트랙트 burn 권한 부여
        await promissoryNote.addBurnAuthorization(await promissoryNoteAuction.getAddress());

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

        // 차용증 NFT 발행
        const tx = await promissoryNote.mint(metadata, creditor.address);
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

            const invalidSignature = await bidder.signTypedData(
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

    describe("입찰 시 RAB 코인 예치", function () {
        let bidAmount;

        beforeEach(async function () {
            // RAB 코인을 입찰자에게 발행하고 승인
            bidAmount = 500;
            await rabbitCoin.mint(bidder.address, bidAmount);
            await rabbitCoin.connect(bidder).approve(await promissoryNoteAuction.getAddress(), bidAmount);
            
            // NFT 예치 준비
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
            
            // 차용증 NFT를 경매 컨트랙트에 예치
            await promissoryNoteAuction.depositNFTWithPermit(
                tokenId,
                creditor.address,
                deadline,
                signature
            );
        });

        it("입찰자가 RAB 코인을 예치할 수 있어야 함", async function () {
            // 1. 초기 상태 확인
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(ethers.ZeroAddress);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(0);

            // 2. 입찰자가 RAB 코인을 예치
            await expect(
                promissoryNoteAuction.depositRAB(tokenId, bidAmount, bidder.address)
            )
            .to.emit(promissoryNoteAuction, "RABDeposited");

            // 3. 예치 후 상태 검증
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(bidder.address);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(bidAmount);

            // 4. RAB 코인이 경매 컨트랙트로 이체되었는지 확인
            const auctionBalance = await rabbitCoin.balanceOf(await promissoryNoteAuction.getAddress());
            expect(auctionBalance).to.equal(bidAmount);
        });

        it("예치되지 않은 NFT에 대해 입찰 실패해야 함", async function () {
            const nonExistentTokenId = 999;

            await expect(
                promissoryNoteAuction.depositRAB(nonExistentTokenId, bidAmount, bidder.address)
            ).to.be.revertedWith("NFT not deposited");
        });

        it("새로운 입찰자가 입찰하면 이전 입찰자의 코인이 환불되어야 함", async function () {
            // 첫 번째 입찰자 입찰
            const firstBidAmount = 300;
            await promissoryNoteAuction.depositRAB(tokenId, firstBidAmount, bidder.address);

            // 첫 번째 입찰자의 상태 확인
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(bidder.address);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(firstBidAmount);

            // 두 번째 입찰자(owner) 준비
            const secondBidAmount = 400;
            await rabbitCoin.mint(owner.address, secondBidAmount);
            await rabbitCoin.connect(owner).approve(await promissoryNoteAuction.getAddress(), secondBidAmount);

            // 첫 번째 입찰자의 초기 잔액 기록
            const bidderInitialBalance = await rabbitCoin.balanceOf(bidder.address);

            // 두 번째 입찰자 입찰
            await promissoryNoteAuction.depositRAB(tokenId, secondBidAmount, owner.address);

            // 입찰 후 상태 검증
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(owner.address);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(secondBidAmount);

            // 첫 번째 입찰자에게 코인이 환불되었는지 확인
            const bidderFinalBalance = await rabbitCoin.balanceOf(bidder.address);
            expect(bidderFinalBalance).to.equal(Number(bidderInitialBalance) + Number(firstBidAmount));

            // 경매 컨트랙트에는 두 번째 입찰 금액만 남아있어야 함
            const auctionBalance = await rabbitCoin.balanceOf(await promissoryNoteAuction.getAddress());
            expect(auctionBalance).to.equal(secondBidAmount);
        });

        it("입찰자가 승인하지 않은 코인으로 입찰 시 실패해야 함", async function () {
            // 새 계정에 RAB 코인 발행하지만 승인은 하지 않음
            const newBidder = (await ethers.getSigners())[4];
            await rabbitCoin.mint(newBidder.address, bidAmount);
            await expect(
                promissoryNoteAuction.depositRAB(tokenId, bidAmount, newBidder.address)
            ).to.be.revertedWith("ERC20: transfer amount exceeds allowance");
        });

        it("RAB 잔액이 부족한 상태에서 입찰 시 실패해야 함", async function () {
            // 잔액보다 큰 금액으로 입찰 시도
            const highBidAmount = 1000; // bidder는 500 RAB만 가지고 있음
            await rabbitCoin.connect(bidder).approve(await promissoryNoteAuction.getAddress(), highBidAmount);

            await expect(
                promissoryNoteAuction.depositRAB(tokenId, highBidAmount, bidder.address)
            ).to.be.reverted;
        });

        it("이전 입찰보다 낮은 금액으로 입찰 시 실패해야 함", async function () {
            // 첫 번째 입찰
            await promissoryNoteAuction.depositRAB(tokenId, bidAmount, bidder.address);
            
            // 더 낮은 금액으로 두 번째 입찰 준비
            const lowerBidAmount = 200;
            await rabbitCoin.mint(owner.address, lowerBidAmount);
            await rabbitCoin.connect(owner).approve(await promissoryNoteAuction.getAddress(), lowerBidAmount);
            
            // 더 낮은 금액으로 입찰 시도 - 실패
            await expect(
                promissoryNoteAuction.depositRAB(tokenId, lowerBidAmount, owner.address)
            ).to.be.revertedWith("New bid amount must be higher than previous bid");
            
            // 상태가 변경되지 않았는지 확인
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(bidder.address);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(bidAmount);
        });

        it("동일한 입찰자가 동일한 금액으로 재입찰 시 실패해야 함", async function () {
            // 첫 번째 입찰
            await promissoryNoteAuction.depositRAB(tokenId, bidAmount, bidder.address);
            
            // 동일 금액으로 재입찰 시도
            await expect(
                promissoryNoteAuction.depositRAB(tokenId, bidAmount, bidder.address)
            ).to.be.revertedWith("New bid amount must be higher than previous bid");
        });

        it("동일한 입찰자가 더 높은 금액으로 입찰 시 차액만 전송해야 함", async function () {
            // 첫 번째 입찰
            const initialBidAmount = 300;
            await promissoryNoteAuction.depositRAB(tokenId, initialBidAmount, bidder.address);
            
            // 추가 코인 발행 및 승인
            const additionalAmount = 200;
            await rabbitCoin.mint(bidder.address, additionalAmount);
            await rabbitCoin.connect(bidder).approve(await promissoryNoteAuction.getAddress(), additionalAmount);
            
            // 경매 컨트랙트의 초기 RAB 잔액 확인
            const initialContractBalance = await rabbitCoin.balanceOf(await promissoryNoteAuction.getAddress());
            
            // 입찰자의 초기 RAB 잔액 확인
            const initialBidderBalance = await rabbitCoin.balanceOf(bidder.address);
            
            // 금액을 증액하여 재입찰
            const newBidAmount = 500;
            await expect(
                promissoryNoteAuction.depositRAB(tokenId, newBidAmount, bidder.address)
            ).to.emit(promissoryNoteAuction, "RABDeposited");
            
            // 상태 검증
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(bidder.address);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(newBidAmount);
            
            // 입찰자 잔액 확인 - 차액만큼만 감소해야 함
            const finalBidderBalance = await rabbitCoin.balanceOf(bidder.address);
            expect(Number(initialBidderBalance) - Number(finalBidderBalance)).to.equal(Number(additionalAmount));

            
            // 경매 컨트랙트 잔액 확인 - 차액만큼만 증가해야 함
            const finalContractBalance = await rabbitCoin.balanceOf(await promissoryNoteAuction.getAddress());
            expect(Number(finalContractBalance) - Number(initialContractBalance)).to.equal(Number(additionalAmount));
        });

        it("NFT 예치자는 자신의 NFT에 입찰할 수 없어야 함", async function () {
            // 예치자(creditor)에게 RAB 코인 발행 및 승인
            const creditorBidAmount = 600;
            await rabbitCoin.mint(creditor.address, creditorBidAmount);
            await rabbitCoin.connect(creditor).approve(await promissoryNoteAuction.getAddress(), creditorBidAmount);
            
            // 예치자가 자신의 NFT에 입찰 시도 - 실패해야 함
            await expect(
                promissoryNoteAuction.depositRAB(tokenId, creditorBidAmount, creditor.address)
            ).to.be.revertedWith("Depositor cannot bid on their own NFT");
            
            // 상태가 변경되지 않았는지 확인
            expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(ethers.ZeroAddress);
            expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(0);
        });
    });

    describe("경매 종료", function () {
        beforeEach(async function () {
            // NFT 예치 준비
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
            
            // 차용증 NFT를 경매 컨트랙트에 예치
            await promissoryNoteAuction.depositNFTWithPermit(
                tokenId,
                creditor.address,
                deadline,
                signature
            );
    
            // 입찰자에게 RAB 코인 발행 및 승인
            const bidAmount = 500;
            await rabbitCoin.mint(bidder.address, bidAmount);
            await rabbitCoin.connect(bidder).approve(await promissoryNoteAuction.getAddress(), bidAmount);
            
            // 입찰 실행
            await promissoryNoteAuction.depositRAB(tokenId, bidAmount, bidder.address);
        });
    
        describe("경매 낙찰 처리", function () {
            it("낙찰 처리가 올바르게 실행되어야 함", async function () {
                // 부속 NFT 메타데이터 준비
                const bidAmount = 500;
                const appendixMetadata = {
                    tokenId: tokenId,
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 낙찰 전 상태 확인
                const sellerInitialBalance = await rabbitCoin.balanceOf(creditor.address);
                
                // 낙찰 처리 실행
                await expect(
                    promissoryNoteAuction.finalizeAuction(
                        tokenId,
                        bidder.address,
                        bidAmount,
                        appendixMetadata
                    )
                ).to.emit(promissoryNoteAuction, "AuctionFinalized");
    
                // 낙찰 후 상태 검증
    
                // 1. NFT 소유권이 입찰자에게 이전되었는지 확인
                expect(await promissoryNote.ownerOf(tokenId)).to.equal(bidder.address);
    
                // 2. 판매자(creditor)에게 입찰 금액이 전송되었는지 확인
                const sellerFinalBalance = await rabbitCoin.balanceOf(creditor.address);
                expect(Number(sellerFinalBalance) - Number(sellerInitialBalance)).to.equal(bidAmount);
    
                // 3. 경매 정보가 초기화되었는지 확인
                expect(await promissoryNoteAuction.getDepositor(tokenId)).to.equal(ethers.ZeroAddress);
                expect(await promissoryNoteAuction.getCurrentBidder(tokenId)).to.equal(ethers.ZeroAddress);
                expect(await promissoryNoteAuction.getBiddingAmount(tokenId)).to.equal(0);
            });
    
            it("부속 NFT가 올바르게 발행되고 번들링되어야 함", async function () {
                const bidAmount = 500;
                const appendixMetadata = {
                    tokenId: tokenId,
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 낙찰 처리 실행
                await promissoryNoteAuction.finalizeAuction(
                    tokenId,
                    bidder.address,
                    bidAmount,
                    appendixMetadata
                );
    
                // 1. 부속 NFT 토큰 ID 배열 조회
                const appendixTokenIds = await promissoryNote.getAppendixTokenIds(tokenId);
                expect(appendixTokenIds.length).to.be.greaterThan(0);
                
                // 2. 최신 부속 NFT ID 조회
                const latestAppendixTokenId = await promissoryNote.getLatestAppendixTokenId(tokenId);
                expect(appendixTokenIds).to.include(latestAppendixTokenId);
                
                // 3. 부속 NFT 메타데이터 조회 및 검증
                const storedMetadata = await promissoryNote.getAppendixMetadata(latestAppendixTokenId);
                expect(storedMetadata.tokenId).to.equal(tokenId);
                expect(storedMetadata.grantorWalletAddress).to.equal(creditor.address);
                expect(storedMetadata.granteeWalletAddress).to.equal(bidder.address);
                expect(storedMetadata.la).to.equal(loanAmount);
            });
    
            it("최신 채권자 주소가 올바르게 업데이트되어야 함", async function () {
                const bidAmount = 500;
                const appendixMetadata = {
                    tokenId: tokenId,
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 낙찰 전 최신 채권자 주소 확인
                expect(await promissoryNote.getLatestCreditorAddress(tokenId)).to.equal(creditor.address);
    
                // 낙찰 처리 실행
                await promissoryNoteAuction.finalizeAuction(
                    tokenId,
                    bidder.address,
                    bidAmount,
                    appendixMetadata
                );
    
                // 낙찰 후 최신 채권자 주소 확인 - 입찰자(bidder)로 변경되어야 함
                expect(await promissoryNote.getLatestCreditorAddress(tokenId)).to.equal(bidder.address);
            });
    
            it("입찰 없이 낙찰 처리 시 실패해야 함", async function () {
                // 입찰 없는 새로운 NFT 발행
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
                }

                const tx = await promissoryNote.mint(metadata, creditor.address);
                const receipt = await tx.wait();
                const event = receipt.logs.find(
                    log => log.fragment && log.fragment.name === 'PromissoryNoteMinted'
                );
                const newTokenId = event.args[0];
    
                // 새 NFT 예치
                const nonce = await promissoryNote.getNonce(creditor.address);
                const permitMessageHash = await promissoryNote.getPermitMessageHash(
                    creditor.address,
                    await promissoryNoteAuction.getAddress(),
                    newTokenId,
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
                        tokenId: newTokenId,
                        nonce: nonce,
                        deadline: deadline
                    }
                );
                
                await promissoryNoteAuction.depositNFTWithPermit(
                    newTokenId,
                    creditor.address,
                    deadline,
                    signature
                );
    
                // 입찰 없이 낙찰 처리 시도
                const appendixMetadata = {
                    tokenId: newTokenId,
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 현재 입찰자가 없으므로 실패해야 함
                await expect(
                    promissoryNoteAuction.finalizeAuction(
                        newTokenId,
                        bidder.address,
                        500,
                        appendixMetadata
                    )
                ).to.be.revertedWith("Buyer is not the current bidder");
            });
    
            it("메타데이터 토큰 ID가 일치하지 않을 경우 실패해야 함", async function () {
                const bidAmount = 500;
                const invalidMetadata = {
                    tokenId: 999, // 잘못된 토큰 ID
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 메타데이터의 토큰 ID가 일치하지 않아 실패해야 함
                await expect(
                    promissoryNoteAuction.finalizeAuction(
                        tokenId,
                        bidder.address,
                        bidAmount,
                        invalidMetadata
                    )
                ).to.be.revertedWith("Metadata tokenId mismatch");
            });
    
            it("입찰 금액이 일치하지 않을 경우 실패해야 함", async function () {
                const correctBidAmount = 500;
                const wrongBidAmount = 600;
                const appendixMetadata = {
                    tokenId: tokenId,
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 잘못된 입찰 금액으로 낙찰 처리 시도
                await expect(
                    promissoryNoteAuction.finalizeAuction(
                        tokenId,
                        bidder.address,
                        wrongBidAmount,
                        appendixMetadata
                    )
                ).to.be.revertedWith("Bid amount does not match");
            });
    
            it("입찰자와 매개변수의 buyer가 일치하지 않을 경우 실패해야 함", async function () {
                const bidAmount = 500;
                const appendixMetadata = {
                    tokenId: tokenId,
                    grantorSign: "GRANTOR_SIGNATURE",
                    grantorName: "John Creditor",
                    grantorWalletAddress: creditor.address,
                    grantorInfoHash: "0x123456789abcdef",
                    granteeSign: "GRANTEE_SIGNATURE",
                    granteeName: "Bob Buyer",
                    granteeWalletAddress: bidder.address,
                    granteeInfoHash: "0x987654321fedcba",
                    la: loanAmount,
                    contractDate: new Date().toISOString().split('T')[0],
                    originalText: "0xoriginal_hash"
                };
    
                // 잘못된 구매자 주소로 낙찰 처리 시도
                await expect(
                    promissoryNoteAuction.finalizeAuction(
                        tokenId,
                        debtor.address, // 실제 입찰자가 아님
                        bidAmount,
                        appendixMetadata
                    )
                ).to.be.revertedWith("Buyer is not the current bidder");
            });
        });
    
        describe("경매 취소", function () {
            it("입찰이 없는 경우 경매 취소가 가능해야 함", async function () {
                // 새 NFT 발행
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
                }
                const tx = await promissoryNote.mint(metadata, creditor.address);
                const receipt = await tx.wait();
                const event = receipt.logs.find(
                    log => log.fragment && log.fragment.name === 'PromissoryNoteMinted'
                );
                const newTokenId = event.args[0];
    
                // 새 NFT 예치
                const nonce = await promissoryNote.getNonce(creditor.address);
                const permitMessageHash = await promissoryNote.getPermitMessageHash(
                    creditor.address,
                    await promissoryNoteAuction.getAddress(),
                    newTokenId,
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
                        tokenId: newTokenId,
                        nonce: nonce,
                        deadline: deadline
                    }
                );
                
                await promissoryNoteAuction.depositNFTWithPermit(
                    newTokenId,
                    creditor.address,
                    deadline,
                    signature
                );
    
                // 취소 전 상태 확인
                expect(await promissoryNote.ownerOf(newTokenId)).to.equal(await promissoryNoteAuction.getAddress());
                expect(await promissoryNoteAuction.getDepositor(newTokenId)).to.equal(creditor.address);
    
                // 경매 취소 실행
                await expect(
                    promissoryNoteAuction.cancelAuction(newTokenId)
                ).to.emit(promissoryNoteAuction, "AuctionCancelled");
    
                // 취소 후 상태 검증
                // NFT가 원래 소유자에게 반환되었는지 확인
                expect(await promissoryNote.ownerOf(newTokenId)).to.equal(creditor.address);
                
                // 예치 정보가 삭제되었는지 확인
                expect(await promissoryNoteAuction.getDepositor(newTokenId)).to.equal(ethers.ZeroAddress);
            });
    
            it("입찰이 있는 경우 경매 취소가 실패해야 함", async function () {
                // 이미 입찰이 있는 경매 취소 시도
                await expect(
                    promissoryNoteAuction.cancelAuction(tokenId)
                ).to.be.revertedWith("Auction has active bids");
            });
    
            it("존재하지 않는 경매 취소 시도 시 실패해야 함", async function () {
                const nonExistentTokenId = 999;
                await expect(
                    promissoryNoteAuction.cancelAuction(nonExistentTokenId)
                ).to.be.revertedWith("Auction does not exist");
            });
        });
    });
});