// mocks/mockBorrowList.ts
import { BorrowListResponse, BorrowInfoResponse } from "../types/response";

const TOTAL_ELEMENTS = 120; // 전체 항목 수
const PAGE_SIZE = 10; // 한 페이지에 보여줄 항목 수

// 모든 차입 내역 항목을 생성하는 함수
const generateMockBorrowListData = (): BorrowInfoResponse[] => {
  return Array.from({ length: TOTAL_ELEMENTS }, (_, i) => {
    const id = (i + 1).toString();
    return {
      tokenId: id,
      nftImage: "/images/NFT.png", // Lent와 구분되는 이미지 경로
      drName: `차입자${id}`, // 차입자 이름으로 설정
      drWallet: `0x${(5000 + i).toString()}...${(6000 + i).toString()}`, // 차입자 지갑 주소
      la: 500000 + i * 50000, // 빌린 금액
      ir: 3 + (i % 4), // 이자율 (Lent보다 낮은 이자율)
      matDt: "2025-06-30", // 만기일
      remainTerms: 45, // 남은 기간 (예시: 45일)
      pnStatus: i % 4 === 0 ? "연체" : "정상", // 일부 항목은 연체 상태로 설정
      nextMpDt: "2025-05-15", // 다음 상환일
      nextAmount: 50000 + i * 5000, // 다음 상환금액
      aoi: i % 4 === 0 ? 30000 + i * 500 : null, // 연체 시 벌금
      aoiDays: i % 4 === 0 ? 2 + (i % 5) : null, // 연체 일수
    };
  });
};

// 페이지 번호에 따른 데이터를 생성하는 함수
export const generateMockBorrowList = (
  pageNumber: number,
  pageSize: number = PAGE_SIZE,
): BorrowListResponse => {
  const allData = generateMockBorrowListData();
  const totalElements = allData.length;
  const totalPages = Math.ceil(totalElements / pageSize);
  const start = pageNumber * pageSize;
  const end = start + pageSize;
  const content = allData.slice(start, end);

  return {
    content,
    pageNumber,
    pageSize,
    totalElements,
    totalPages,
    last: pageNumber === totalPages - 1,
    hasNext: pageNumber < totalPages - 1,
  };
};

// 기본적으로 첫 페이지의 데이터를 export
export const mockBorrowList = generateMockBorrowList(0);
