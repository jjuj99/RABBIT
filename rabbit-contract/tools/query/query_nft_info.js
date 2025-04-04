const { ethers } = require("hardhat");
const fs = require("fs");

const PROMISSORY_NOTE_ADDRESS = process.env.PROMISSORY_NOTE_ADDRESS;
const TOKEN_IDS = [3, 4, 5]; // 조회할 토큰 ID

async function main() {
    console.log("차용증 NFT 정보 조회를 시작합니다...");

    // 컨트랙트 인스턴스 생성
    const promissoryNote = await ethers.getContractAt(
        "PromissoryNote",
        PROMISSORY_NOTE_ADDRESS
    );

    // 결과 저장을 위한 배열
    const results = [];

    // 각 토큰 ID에 대해 정보 조회
    for (const tokenId of TOKEN_IDS) {
        try {
            console.log(`\n토큰 ID ${tokenId} 정보 조회 중...`);

            // 토큰 존재 여부 확인 (소유자 조회)
            let owner;
            try {
                owner = await promissoryNote.ownerOf(tokenId);
                console.log(`소유자: ${owner}`);
            } catch (error) {
                console.log(`토큰 ID ${tokenId}는 존재하지 않습니다.`);
                continue;
            }

            // 차용증 메타데이터 조회
            const metadata = await promissoryNote.getPromissoryMetadata(tokenId);
            console.log("차용증 메타데이터:");
            console.log(`- 채권자: ${metadata.crInfo.crName} (${metadata.crInfo.crWalletAddress})`);
            console.log(`- 채무자: ${metadata.drInfo.drName} (${metadata.drInfo.drWalletAddress})`);
            console.log(`- 차용 금액: ${BigInt(metadata.la)} RAB`);
            console.log(`- 이자율: ${(Number(metadata.ir) / 100).toString()}%`);
            console.log(`- 대출 기간: ${metadata.lt.toString()}개월`);
            console.log(`- 상환 방식: ${metadata.repayType}`);
            console.log(`- 만기일: ${metadata.matDt}`);
            console.log(`- 계약일: ${metadata.contractDate}`);

            // 부속 NFT ID 목록 조회
            const appendixIds = await promissoryNote.getAppendixTokenIds(tokenId);
            console.log(`- 부속 NFT 수: ${appendixIds.length.toString()}`);

            // 부속 NFT가 있는 경우 상세 정보 조회
            const appendixDetails = [];
            for (const appendixId of appendixIds) {
                const appendixMetadata = await promissoryNote.getAppendixMetadata(appendixId);
                console.log(`\n부속 NFT ID ${appendixId} 정보:`);
                console.log(`- 양도인: ${appendixMetadata.grantorName} (${appendixMetadata.grantorWalletAddress})`);
                console.log(`- 양수인: ${appendixMetadata.granteeName} (${appendixMetadata.granteeWalletAddress})`);
                console.log(`- 계약일: ${appendixMetadata.contractDate}`);
                console.log(`- 남은 원금: ${BigInt(appendixMetadata.la)} RAB`);

                appendixDetails.push({
                    appendixId: appendixId.toString(),
                    grantor: {
                        name: appendixMetadata.grantorName,
                        address: appendixMetadata.grantorWalletAddress
                    },
                    grantee: {
                        name: appendixMetadata.granteeName,
                        address: appendixMetadata.granteeWalletAddress
                    },
                    remainingPrincipal: appendixMetadata.la.toString(),
                    contractDate: appendixMetadata.contractDate
                });
            }

            // 최신 채권자 주소 조회
            const latestCreditor = await promissoryNote.getLatestCreditorAddress(tokenId);
            console.log(`\n현재 채권자 주소: ${latestCreditor}`);

            // 결과를 객체로 저장
            results.push({
                tokenId: tokenId.toString(),
                owner,
                crInfo: {
                    crName: metadata.crInfo.crName,
                    crWalletAddress: metadata.crInfo.crWalletAddress,
                    latestAddress: latestCreditor
                },
                drInfo: {
                    drName: metadata.drInfo.drName,
                    drWalletAddress: metadata.drInfo.drWalletAddress
                },
                la: metadata.la.toString(),
                ir: (Number(metadata.ir) / 100).toString(),
                lt: metadata.lt.toString(),
                repayType: metadata.repayType,
                matDt: metadata.matDt,
                contractDate: metadata.contractDate,
                earlyPayFlag: metadata.earlyPayFlag,
                earlyPayFee: metadata.earlyPayFee.toString(),
                addTerms: {
                    addTerms: metadata.addTerms.addTerms
                },
                appendixCount: appendixIds.length.toString(),
                appendixDetails
            });

        } catch (error) {
            console.error(`토큰 ID ${tokenId} 조회 중 오류 발생:`, error);
        }
    }

    // 결과를 JSON 파일로 저장
    const resultsDir = "./tools/results";

    // 결과 폴더가 없으면 생성
    if (!fs.existsSync(resultsDir)) {
        fs.mkdirSync(resultsDir, { recursive: true });
    }

    const resultsFilePath = `${resultsDir}/nft_info_results.json`;

    fs.writeFileSync(
        resultsFilePath,
        JSON.stringify(results, null, 2)
    );

    console.log("\n모든 정보가 nft_info_results.json 파일에 저장되었습니다.");
}

// 실행 함수
main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });