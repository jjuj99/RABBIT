const { ethers } = require("hardhat");
const { Interface, AbiCoder } = require("ethers");
const fs = require("fs");

const REPAYMENT_SCHEDULER_ADDRESS = process.env.REPAYMENT_SCHEDULER_ADDRESS;
const TOKEN_IDS = [1, 2, 3];

async function main() {
    console.log("차용증 NFT 상환 정보 조회를 시작합니다...");

    const provider = ethers.provider;

    const iface = new Interface([
        "function getRepaymentInfo(uint256)"
    ]);

    const abiCoder = AbiCoder.defaultAbiCoder();

    const results = [];

    for (const tokenId of TOKEN_IDS) {
        try {
            console.log(`\n토큰 ID ${tokenId} 상환 정보 조회 중...`);

            const calldata = iface.encodeFunctionData("getRepaymentInfo", [tokenId]);

            const raw = await provider.call({
                to: REPAYMENT_SCHEDULER_ADDRESS,
                data: calldata
            });

            const [info] = abiCoder.decode(
                [
                    `tuple(
                        uint256 tokenId,
                        uint256 initialPrincipal,
                        uint256 remainingPrincipal,
                        uint256 ir,
                        uint256 dir,
                        uint256 mpDt,
                        uint256 nextMpDt,
                        uint256 totalPayments,
                        uint256 remainingPayments,
                        uint256 fixedPaymentAmount,
                        string repayType,
                        address drWalletAddress,
                        bool activeFlag,
                        bool overdueFlag,
                        uint256 overdueStartDate,
                        uint256 overdueDays,
                        uint256 aoi,
                        uint256 defCnt,
                        uint256 accel,
                        uint256 currentIr,
                        uint256 totalDefCnt
                    )`
                ],
                raw
            );

            // 출력
            console.log("✅ 조회 성공:");
            console.log(`- tokenId: ${info.tokenId}`);
            console.log(`- initialPrincipal: ${info.initialPrincipal}`);
            console.log(`- remainingPrincipal: ${info.remainingPrincipal}`);
            console.log(`- repayType: ${info.repayType}`);
            console.log(`- drWalletAddress: ${info.drWalletAddress}`);
            console.log(`- activeFlag: ${info.activeFlag}`);
            console.log(`- overdueFlag: ${info.overdueFlag}`);
            console.log(`- totalDefCnt: ${info.totalDefCnt}`);

            results.push({
                tokenId: info.tokenId.toString(),
                initialPrincipal: info.initialPrincipal.toString(),
                remainingPrincipal: info.remainingPrincipal.toString(),
                ir: info.ir.toString(),
                dir: info.dir.toString(),
                mpDt: info.mpDt.toString(),
                nextMpDt: info.nextMpDt.toString(),
                totalPayments: info.totalPayments.toString(),
                remainingPayments: info.remainingPayments.toString(),
                fixedPaymentAmount: info.fixedPaymentAmount.toString(),
                repayType: info.repayType,
                drWalletAddress: info.drWalletAddress,
                activeFlag: info.activeFlag,
                overdueFlag: info.overdueFlag,
                overdueStartDate: info.overdueStartDate.toString(),
                overdueDays: info.overdueDays.toString(),
                aoi: info.aoi.toString(),
                defCnt: info.defCnt.toString(),
                accel: info.accel.toString(),
                currentIr: info.currentIr.toString(),
                totalDefCnt: info.totalDefCnt.toString()
            });

        } catch (error) {
            console.error(`❌ 토큰 ID ${tokenId} 조회 중 오류 발생:`, error);
        }

        // 결과를 JSON 파일로 저장
        const resultsDir = "./tools/results";

        if (!fs.existsSync(resultsDir)) {
            fs.mkdirSync(resultsDir, { recursive: true });
        }

        const resultsFilePath = `${resultsDir}/repayment_info_results.json`;

        fs.writeFileSync(
            resultsFilePath,
            JSON.stringify(results, null, 2)
        );

        console.log("\n모든 상환 정보가 repayment_info_results.json 파일에 저장되었습니다.");
    }
}

main()
    .then(() => process.exit(0))
    .catch((error) => {
        console.error(error);
        process.exit(1);
    });
