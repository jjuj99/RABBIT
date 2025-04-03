// mocks/mockLentList.ts
import { LentListResponse, LentInfoResponse } from "../types/response";

const TOTAL_ELEMENTS = 150; // 전체 항목 수
const PAGE_SIZE = 10; // 한 페이지에 보여줄 항목 수

// 모든 항목을 생성하는 함수
const generateMockLentListData = (): LentInfoResponse[] => {
  return Array.from({ length: TOTAL_ELEMENTS }, (_, i) => {
    const id = (i + 1).toString();
    return {
      contractId: id,
      tokenId: id,
      nftImage: "/images/NFT.png",
      drName: `채권자${id}`,
      drWallet: `0x${(1000 + i).toString()}...${(2000 + i).toString()}`,
      la: 1000000 + i * 100000,
      ir: 5 + (i % 3),
      matDt: "2024-12-31",
      remainTerms: 30,
      pnStatus: i % 3 === 0 ? "연체" : "정상",
      nextMpDt: "2024-04-30",
      nextAmount: 100000 + i * 10000,
      aoi: i % 3 === 0 ? 50000 + i * 1000 : null,
      aoiDays: i % 3 === 0 ? 1 + (i % 10) : null,
    };
  });
};

// 페이지 번호에 따른 데이터를 생성하는 함수
export const generateMockLentList = (
  pageNumber: number,
  pageSize: number = PAGE_SIZE,
): LentListResponse => {
  const allData = generateMockLentListData();
  const totalElements = allData.length;
  const totalPages = Math.ceil(totalElements / pageSize);
  const start = pageNumber * pageSize;
  const end = start + pageSize;
  const content = allData.slice(start, end);

  return {
    content,
    pageNumber: pageNumber,
    pageSize,
    totalElements,
    totalPages,
    last: pageNumber === totalPages - 1,
    hasNext: pageNumber < totalPages - 1,
  };
};

// 기본적으로 첫 페이지의 데이터를 export
export const mockLentList = generateMockLentList(0);
