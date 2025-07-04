// import { LentDetailResponse } from "../types/response";

// export const mockLentDetail: LentDetailResponse[] = [
//   {
//     tokenId: "nft-lent-001",
//     nftImage: "/images/NFT.png",
//     drName: "대출자1",
//     drWallet: "0xd53a89f5d467414090b43f89fb1f1ebb",
//     la: 1771716,
//     totalAmount: 1375933,
//     repayType: "원리금균등",
//     ir: 6.49,
//     dir: 5.72,
//     defCnt: 2,
//     contractDt: "2024-01-05",
//     matDt: "2025-01-04",
//     remainTerms: 21,
//     progressRage: 23,
//     pnStatus: "정상",
//     nextMpDt: "2025-04-13",
//     nextAmount: 125438,
//     aoi: 4.15,
//     aoiDays: null,
//     earlypayFlag: false,
//     earlypayFee: 5696,
//     accel: 1,
//     accelDir: 1,
//     addTerms: ["NFT 보험", "추가 담보"],
//     eventList: [
//       {
//         eventType: "연체",
//         intAmt: 19723,
//         from: null,
//         to: "0xda80e32f3b8e43cdbee1012c053a1f10",
//         timestamp: "2024-08-30T13:27:40.520665",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-002",
//     nftImage: "/images/NFT.png",
//     drName: "대출자2",
//     drWallet: "0x71a9b9e2fce047f0abe49f5787bc742c",
//     la: 1311250,
//     totalAmount: 1441130,
//     repayType: "만기일시상환",
//     ir: 4.79,
//     dir: 3.93,
//     defCnt: 3,
//     contractDt: "2024-01-09",
//     matDt: "2025-01-08",
//     remainTerms: 19,
//     progressRage: 21,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-09",
//     nextAmount: 298734,
//     aoi: null,
//     aoiDays: 22,
//     earlypayFlag: true,
//     earlypayFee: 5461,
//     accel: 1,
//     accelDir: 0,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: 7385,
//         from: "0x995d801748ac4928b67303500642e38f",
//         to: null,
//         timestamp: "2024-08-28T13:27:40.521119",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-003",
//     nftImage: "/images/NFT.png",
//     drName: "대출자3",
//     drWallet: "0x08c691db2d9946549a0a9171f0b2ca89",
//     la: 1004885,
//     totalAmount: 646386,
//     repayType: "원금균등",
//     ir: 3.61,
//     dir: 5.94,
//     defCnt: 0,
//     contractDt: "2024-01-13",
//     matDt: "2025-01-12",
//     remainTerms: 13,
//     progressRage: 2,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-22",
//     nextAmount: 84567,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 4350,
//     accel: 1,
//     accelDir: 1,
//     addTerms: ["추가 담보"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: 18287,
//         from: "0x13aa410f948d40d1a4cc948e84531e66",
//         to: "0xd1c70ceda5cd4fe9a93c327b3fdab284",
//         timestamp: "2024-12-26T13:27:40.521671",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-004",
//     nftImage: "/images/NFT.png",
//     drName: "대출자4",
//     drWallet: "0xfbd15d39b9324d4d80af5a53d6c896fe",
//     la: 1150440,
//     totalAmount: 1988963,
//     repayType: "원금균등",
//     ir: 7.24,
//     dir: 7.07,
//     defCnt: 2,
//     contractDt: "2024-01-17",
//     matDt: "2025-01-16",
//     remainTerms: 4,
//     progressRage: 65,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-05",
//     nextAmount: 211632,
//     aoi: null,
//     aoiDays: 3,
//     earlypayFlag: true,
//     earlypayFee: 9223,
//     accel: 1,
//     accelDir: 0,
//     addTerms: ["NFT 보험"],
//     eventList: [
//       {
//         eventType: "연체",
//         intAmt: null,
//         from: null,
//         to: "0x515295ad996a4ed39c233a72d28d2a0b",
//         timestamp: "2024-06-23T13:27:40.521805",
//       },
//       {
//         eventType: "상환",
//         intAmt: 16147,
//         from: "0x83a8e33570c34f43bc487e38a3e605bd",
//         to: "0xa613d81d6d4e4471a9e5d092836f0dd8",
//         timestamp: "2025-01-17T13:27:40.521841",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-005",
//     nftImage: "/images/NFT.png",
//     drName: "대출자5",
//     drWallet: "0x2b2a463a65b8459d9beb8f5cd03ab634",
//     la: 1936798,
//     totalAmount: 1425124,
//     repayType: "만기일시상환",
//     ir: 7.94,
//     dir: 4.51,
//     defCnt: 2,
//     contractDt: "2024-01-21",
//     matDt: "2025-01-20",
//     remainTerms: 21,
//     progressRage: 65,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-30",
//     nextAmount: 263459,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 7592,
//     accel: 0,
//     accelDir: 1,
//     addTerms: ["NFT 보험", "금리 우대"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: 19455,
//         from: null,
//         to: "0x196fa8cb2b3e48c3bfaad34f7573ecb7",
//         timestamp: "2024-05-30T13:27:40.522021",
//       },
//       {
//         eventType: "상환",
//         intAmt: 13119,
//         from: "0x7a290ca1f983445ab6cbb02f0e0af8a8",
//         to: null,
//         timestamp: "2024-06-17T13:27:40.522045",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-006",
//     nftImage: "/images/NFT.png",
//     drName: "대출자6",
//     drWallet: "0x30d5007b8f3e4ff5a7402cf22e5a9938",
//     la: 760669,
//     totalAmount: 1038259,
//     repayType: "원리금균등",
//     ir: 7.82,
//     dir: 5.21,
//     defCnt: 3,
//     contractDt: "2024-01-25",
//     matDt: "2025-01-24",
//     remainTerms: 4,
//     progressRage: 79,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-17",
//     nextAmount: 62603,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: false,
//     earlypayFee: 8172,
//     accel: 1,
//     accelDir: 0,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: null,
//         from: null,
//         to: "0x468d18ec917c4039b4b261516b87acda",
//         timestamp: "2024-12-02T13:27:40.522137",
//       },
//       {
//         eventType: "연체",
//         intAmt: 14070,
//         from: null,
//         to: null,
//         timestamp: "2024-05-22T13:27:40.522146",
//       },
//       {
//         eventType: "연체",
//         intAmt: 8779,
//         from: null,
//         to: null,
//         timestamp: "2024-10-06T13:27:40.522152",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-007",
//     nftImage: "/images/NFT.png",
//     drName: "대출자7",
//     drWallet: "0xeed924b66d014bbfadafb193713c83af",
//     la: 1389749,
//     totalAmount: 2081467,
//     repayType: "만기일시상환",
//     ir: 7.78,
//     dir: 5.22,
//     defCnt: 2,
//     contractDt: "2024-01-29",
//     matDt: "2025-01-28",
//     remainTerms: 8,
//     progressRage: 32,
//     pnStatus: "정상",
//     nextMpDt: "2025-04-11",
//     nextAmount: 233353,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 1847,
//     accel: 0,
//     accelDir: 1,
//     addTerms: ["추가 담보", "조기상환 옵션", "NFT 보험"],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: 9033,
//         from: "0x6b16b65cf6534c0ab5a436d4bc93011a",
//         to: null,
//         timestamp: "2024-05-07T13:27:40.522225",
//       },
//       {
//         eventType: "연체",
//         intAmt: 13571,
//         from: "0xa559476ef2284deb9d0d4559d67a9d57",
//         to: null,
//         timestamp: "2024-10-20T13:27:40.522236",
//       },
//       {
//         eventType: "양도",
//         intAmt: 7275,
//         from: null,
//         to: "0xf0eceb9185754a0582489f8d156aeb42",
//         timestamp: "2024-04-16T13:27:40.522247",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-008",
//     nftImage: "/images/NFT.png",
//     drName: "대출자8",
//     drWallet: "0x2254f4a9c5a247a69bec0b47e5769eae",
//     la: 629210,
//     totalAmount: 797775,
//     repayType: "원금균등",
//     ir: 7.61,
//     dir: 7.95,
//     defCnt: 2,
//     contractDt: "2024-02-02",
//     matDt: "2025-02-01",
//     remainTerms: 21,
//     progressRage: 88,
//     pnStatus: "정상",
//     nextMpDt: "2025-04-19",
//     nextAmount: 221160,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 1862,
//     accel: 0,
//     accelDir: 0,
//     addTerms: ["추가 담보", "조기상환 옵션"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: null,
//         from: null,
//         to: null,
//         timestamp: "2024-10-06T13:27:40.522415",
//       },
//       {
//         eventType: "상환",
//         intAmt: null,
//         from: "0x00b540f658b54577ad49dac776783651",
//         to: "0x0e6c18d9d8a548b8a4a62cba67f00f90",
//         timestamp: "2024-09-04T13:27:40.522460",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-009",
//     nftImage: "/images/NFT.png",
//     drName: "대출자9",
//     drWallet: "0xb1aba0a7a841478794497880ffd09ea4",
//     la: 831902,
//     totalAmount: 1073359,
//     repayType: "만기일시상환",
//     ir: 5.73,
//     dir: 6.46,
//     defCnt: 1,
//     contractDt: "2024-02-06",
//     matDt: "2025-02-05",
//     remainTerms: 12,
//     progressRage: 50,
//     pnStatus: "정상",
//     nextMpDt: "2025-04-26",
//     nextAmount: 64394,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 3706,
//     accel: 1,
//     accelDir: 1,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "연체",
//         intAmt: null,
//         from: "0x59f51db5d09143b7b536595a91b968d7",
//         to: "0xba5b1e1e307a43908533ca4e73ff7151",
//         timestamp: "2024-05-06T13:27:40.522571",
//       },
//       {
//         eventType: "연체",
//         intAmt: 12724,
//         from: null,
//         to: "0x01594c3e172a489d9154bc9344de8e0c",
//         timestamp: "2024-12-19T13:27:40.522585",
//       },
//       {
//         eventType: "상환",
//         intAmt: 8187,
//         from: "0x0c8cbf2c96c74d2090177980822244da",
//         to: "0xdd7dc095fa674b3ca1ad93d143b2bd92",
//         timestamp: "2024-04-09T13:27:40.522614",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-010",
//     nftImage: "/images/NFT.png",
//     drName: "대출자10",
//     drWallet: "0x120fd77c465449f58356aa26a89dc5a8",
//     la: 1680524,
//     totalAmount: 1615467,
//     repayType: "만기일시상환",
//     ir: 5.69,
//     dir: 7.44,
//     defCnt: 2,
//     contractDt: "2024-02-10",
//     matDt: "2025-02-09",
//     remainTerms: 12,
//     progressRage: 7,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-09",
//     nextAmount: 22867,
//     aoi: 1.26,
//     aoiDays: 5,
//     earlypayFlag: true,
//     earlypayFee: 1311,
//     accel: 1,
//     accelDir: 1,
//     addTerms: ["NFT 보험"],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: 12357,
//         from: null,
//         to: "0xea396a59783146a8aeeec041f5abf196",
//         timestamp: "2024-06-20T13:27:40.522677",
//       },
//       {
//         eventType: "양도",
//         intAmt: 8107,
//         from: "0xa4dd6f28cc1c4919a9067613ed3d2831",
//         to: "0x4c205899e2ad4be2b8e3412003be3303",
//         timestamp: "2024-11-13T13:27:40.522701",
//       },
//       {
//         eventType: "연체",
//         intAmt: 8302,
//         from: "0x9cc974e65e174188a5197a9cb15e34f8",
//         to: "0x8da9856a6b384710824569edd4629ee9",
//         timestamp: "2024-11-09T13:27:40.522716",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-011",
//     nftImage: "/images/NFT.png",
//     drName: "대출자11",
//     drWallet: "0x8325b5b408524913a2d78077b2d58950",
//     la: 1725577,
//     totalAmount: 1759319,
//     repayType: "만기일시상환",
//     ir: 2.99,
//     dir: 3.27,
//     defCnt: 1,
//     contractDt: "2024-02-14",
//     matDt: "2025-02-13",
//     remainTerms: 20,
//     progressRage: 99,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-08",
//     nextAmount: 30060,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: false,
//     earlypayFee: 1293,
//     accel: 0,
//     accelDir: 1,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: 7283,
//         from: "0x162aa0c3a91f42fbbfd823d34cc216fa",
//         to: null,
//         timestamp: "2024-10-06T13:27:40.522780",
//       },
//       {
//         eventType: "양도",
//         intAmt: null,
//         from: "0x13fd29adbf5546c2b7510b5ede1924d6",
//         to: "0x471e291f85da4809a104a75659d75101",
//         timestamp: "2024-07-05T13:27:40.522796",
//       },
//       {
//         eventType: "양도",
//         intAmt: 18340,
//         from: null,
//         to: "0x12351a48e9ac4743b03c0cd115d58f6d",
//         timestamp: "2024-07-08T13:27:40.522807",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-012",
//     nftImage: "/images/NFT.png",
//     drName: "대출자12",
//     drWallet: "0xbbf17a3fde414c8e87aa9cf41793d5f7",
//     la: 915739,
//     totalAmount: 2119703,
//     repayType: "원리금균등",
//     ir: 5.06,
//     dir: 8.09,
//     defCnt: 1,
//     contractDt: "2024-02-18",
//     matDt: "2025-02-17",
//     remainTerms: 19,
//     progressRage: 57,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-22",
//     nextAmount: 118751,
//     aoi: 1.61,
//     aoiDays: 29,
//     earlypayFlag: true,
//     earlypayFee: 5257,
//     accel: 1,
//     accelDir: 1,
//     addTerms: ["추가 담보", "금리 우대"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: 5669,
//         from: "0x5d0f56cebbf24778a4f181a40f48317c",
//         to: "0xd7ced7ec8d3449e78bded499ff0a39b8",
//         timestamp: "2024-05-19T13:27:40.522867",
//       },
//       {
//         eventType: "양도",
//         intAmt: null,
//         from: null,
//         to: "0xa0ab5a3310f0478d91c332a6c999632b",
//         timestamp: "2025-01-01T13:27:40.522878",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-013",
//     nftImage: "/images/NFT.png",
//     drName: "대출자13",
//     drWallet: "0xd37f9f1c6f484b1db1258ae98fb29975",
//     la: 1702419,
//     totalAmount: 1344057,
//     repayType: "원금균등",
//     ir: 6.17,
//     dir: 4.95,
//     defCnt: 2,
//     contractDt: "2024-02-22",
//     matDt: "2025-02-21",
//     remainTerms: 4,
//     progressRage: 8,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-25",
//     nextAmount: 158060,
//     aoi: 3.49,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 3338,
//     accel: 0,
//     accelDir: 0,
//     addTerms: ["NFT 보험", "금리 우대", "조기상환 옵션"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: 14085,
//         from: "0x3a2aa1a2756b4a52bbea63cbee3fb693",
//         to: "0x774b0c7cbcbd4c728917e51772de6ebf",
//         timestamp: "2024-09-11T13:27:40.522958",
//       },
//       {
//         eventType: "연체",
//         intAmt: null,
//         from: null,
//         to: "0x757f4d968a9c43c7a0dadcc1c6ceedc1",
//         timestamp: "2024-05-21T13:27:40.522969",
//       },
//       {
//         eventType: "양도",
//         intAmt: null,
//         from: "0xbf0a970d2b7646b6be4b71057e49d57a",
//         to: null,
//         timestamp: "2024-10-15T13:27:40.522979",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-014",
//     nftImage: "/images/NFT.png",
//     drName: "대출자14",
//     drWallet: "0x1706b96f56b4412fab921be2853d2b51",
//     la: 961920,
//     totalAmount: 1599847,
//     repayType: "만기일시상환",
//     ir: 5.57,
//     dir: 3.45,
//     defCnt: 3,
//     contractDt: "2024-02-26",
//     matDt: "2025-02-25",
//     remainTerms: 20,
//     progressRage: 35,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-30",
//     nextAmount: 28663,
//     aoi: 4.23,
//     aoiDays: 29,
//     earlypayFlag: true,
//     earlypayFee: 1662,
//     accel: 0,
//     accelDir: 1,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: null,
//         from: null,
//         to: null,
//         timestamp: "2025-03-10T13:27:40.523014",
//       },
//       {
//         eventType: "연체",
//         intAmt: null,
//         from: "0x9d7fd226e462483f8aacf84b6e714d02",
//         to: "0x4e3caccc91ee417e96406253709db5cc",
//         timestamp: "2024-09-22T13:27:40.523047",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-015",
//     nftImage: "/images/NFT.png",
//     drName: "대출자15",
//     drWallet: "0x69cd1e48b43f44a3bf83c23f34cfeaf2",
//     la: 545888,
//     totalAmount: 831869,
//     repayType: "만기일시상환",
//     ir: 5.39,
//     dir: 5.54,
//     defCnt: 3,
//     contractDt: "2024-03-01",
//     matDt: "2025-03-01",
//     remainTerms: 1,
//     progressRage: 5,
//     pnStatus: "연체",
//     nextMpDt: "2025-05-03",
//     nextAmount: 21938,
//     aoi: 0.65,
//     aoiDays: 1,
//     earlypayFlag: true,
//     earlypayFee: 8785,
//     accel: 1,
//     accelDir: 0,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "연체",
//         intAmt: null,
//         from: null,
//         to: "0x01ffafe4796f45bbbad9aba573834883",
//         timestamp: "2024-05-30T13:27:40.523099",
//       },
//       {
//         eventType: "상환",
//         intAmt: 9829,
//         from: null,
//         to: null,
//         timestamp: "2025-01-08T13:27:40.523106",
//       },
//       {
//         eventType: "상환",
//         intAmt: 6842,
//         from: "0x049695a7666743358be9f1cc167acca0",
//         to: null,
//         timestamp: "2024-04-19T13:27:40.523126",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-016",
//     nftImage: "/images/NFT.png",
//     drName: "대출자16",
//     drWallet: "0x11e55843cd724e798b72a243b48dd938",
//     la: 948779,
//     totalAmount: 1700040,
//     repayType: "만기일시상환",
//     ir: 7.33,
//     dir: 8.57,
//     defCnt: 3,
//     contractDt: "2024-03-05",
//     matDt: "2025-03-05",
//     remainTerms: 4,
//     progressRage: 73,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-06",
//     nextAmount: 249416,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: false,
//     earlypayFee: 6052,
//     accel: 1,
//     accelDir: 0,
//     addTerms: [],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: null,
//         from: null,
//         to: null,
//         timestamp: "2024-06-24T13:27:40.523162",
//       },
//       {
//         eventType: "양도",
//         intAmt: 7134,
//         from: null,
//         to: "0x2d0f64dfbf214c75a6ff0419a9ea844c",
//         timestamp: "2024-08-30T13:27:40.523184",
//       },
//       {
//         eventType: "양도",
//         intAmt: 7443,
//         from: null,
//         to: "0x87f34fa2cc30453693aaf1819187b4d5",
//         timestamp: "2025-01-10T13:27:40.523196",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-017",
//     nftImage: "/images/NFT.png",
//     drName: "대출자17",
//     drWallet: "0xfa5911f83dc04f49a8d641a528b9c257",
//     la: 1941276,
//     totalAmount: 1142670,
//     repayType: "만기일시상환",
//     ir: 5.54,
//     dir: 5.14,
//     defCnt: 3,
//     contractDt: "2024-03-09",
//     matDt: "2025-03-09",
//     remainTerms: 19,
//     progressRage: 26,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-10",
//     nextAmount: 197487,
//     aoi: null,
//     aoiDays: 23,
//     earlypayFlag: false,
//     earlypayFee: 2392,
//     accel: 0,
//     accelDir: 1,
//     addTerms: ["조기상환 옵션", "추가 담보", "금리 우대"],
//     eventList: [
//       {
//         eventType: "연체",
//         intAmt: null,
//         from: null,
//         to: "0x7d0772a4c69e458c9784c10eb00c8ff7",
//         timestamp: "2024-06-26T13:27:40.523248",
//       },
//       {
//         eventType: "양도",
//         intAmt: 8189,
//         from: null,
//         to: null,
//         timestamp: "2024-05-18T13:27:40.523254",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-018",
//     nftImage: "/images/NFT.png",
//     drName: "대출자18",
//     drWallet: "0x265c30174afb4237907a198b5f8298de",
//     la: 1857034,
//     totalAmount: 1329345,
//     repayType: "원금균등",
//     ir: 2.27,
//     dir: 5.68,
//     defCnt: 1,
//     contractDt: "2024-03-13",
//     matDt: "2025-03-13",
//     remainTerms: 23,
//     progressRage: 87,
//     pnStatus: "연체",
//     nextMpDt: "2025-04-07",
//     nextAmount: 83202,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 8869,
//     accel: 0,
//     accelDir: 1,
//     addTerms: ["금리 우대"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: null,
//         from: null,
//         to: "0x4eb8195ce7aa4e948439de9d919a4d29",
//         timestamp: "2024-07-09T13:27:40.523307",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-019",
//     nftImage: "/images/NFT.png",
//     drName: "대출자19",
//     drWallet: "0xb95e833089ad43ea9afd816da7ea226e",
//     la: 874575,
//     totalAmount: 1403102,
//     repayType: "원리금균등",
//     ir: 4.77,
//     dir: 8.72,
//     defCnt: 3,
//     contractDt: "2024-03-17",
//     matDt: "2025-03-17",
//     remainTerms: 23,
//     progressRage: 66,
//     pnStatus: "연체",
//     nextMpDt: "2025-05-03",
//     nextAmount: 276306,
//     aoi: null,
//     aoiDays: null,
//     earlypayFlag: true,
//     earlypayFee: 7474,
//     accel: 1,
//     accelDir: 1,
//     addTerms: ["NFT 보험", "금리 우대", "추가 담보"],
//     eventList: [
//       {
//         eventType: "양도",
//         intAmt: 9168,
//         from: null,
//         to: null,
//         timestamp: "2025-03-25T13:27:40.523346",
//       },
//       {
//         eventType: "양도",
//         intAmt: 16973,
//         from: "0x0caa5162d6fc4aa986204cafe5ae49bc",
//         to: "0x09c9a1ca25bc477d9dfac2177b10436a",
//         timestamp: "2024-06-17T13:27:40.523381",
//       },
//     ],
//   },
//   {
//     tokenId: "nft-lent-020",
//     nftImage: "/images/NFT.png",
//     drName: "대출자20",
//     drWallet: "0x85438e7b535b4dd997c3da1621a87bc8",
//     la: 1919620,
//     totalAmount: 1796863,
//     repayType: "원리금균등",
//     ir: 6.04,
//     dir: 4.73,
//     defCnt: 1,
//     contractDt: "2024-03-21",
//     matDt: "2025-03-21",
//     remainTerms: 14,
//     progressRage: 83,
//     pnStatus: "정상",
//     nextMpDt: "2025-04-05",
//     nextAmount: 176052,
//     aoi: null,
//     aoiDays: 5,
//     earlypayFlag: true,
//     earlypayFee: 5249,
//     accel: 0,
//     accelDir: 1,
//     addTerms: ["NFT 보험"],
//     eventList: [
//       {
//         eventType: "상환",
//         intAmt: null,
//         from: null,
//         to: null,
//         timestamp: "2024-07-26T13:27:40.523416",
//       },
//       {
//         eventType: "양도",
//         intAmt: 14000,
//         from: null,
//         to: "0x5c7e60454d0144dbb5b2a9b84801b3cb",
//         timestamp: "2024-12-18T13:27:40.523439",
//       },
//     ],
//   },
// ];
