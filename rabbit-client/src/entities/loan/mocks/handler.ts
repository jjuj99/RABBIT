import { http, HttpResponse } from "msw";
import { ApiResponse } from "@/shared/type/ApiResponse";
import {
  BorrowListResponse,
  LentListResponse,
  BorrowSummaryResponse,
  LentSummaryResponse,
  BorrowDetailResponse,
  LentDetailResponse,
  EarlypayResponse,
} from "../types/response";
import { generateMockLentList } from "./mockLentList";
import { generateMockBorrowList } from "./mockBorrowList";
import { mockBorrowDetail } from "./mockBorrowDetail";
import { mockLentDetail } from "./mockLentDetail";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const handlers = [
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/borrow/me`,
    async ({ request }) => {
      const url = new URL(request.url);
      const pageNumber = parseInt(url.searchParams.get("pageNumber") || "0");
      const pageSize = parseInt(url.searchParams.get("pageSize") || "10");

      // generateMockBorrowList를 사용하여 해당 페이지 데이터를 생성
      const paginatedData = generateMockBorrowList(pageNumber, pageSize);

      return HttpResponse.json<ApiResponse<BorrowListResponse>>({
        status: "SUCCESS",
        data: paginatedData,
      });
    },
  ),

  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/me`,
    async ({ request }) => {
      const url = new URL(request.url);
      const pageNumber = parseInt(url.searchParams.get("pageNumber") || "0");
      const pageSize = parseInt(url.searchParams.get("pageSize") || "10");

      // generateMockLentList를 사용하여 해당 페이지 데이터를 생성
      const paginatedData = generateMockLentList(pageNumber, pageSize);

      return HttpResponse.json<ApiResponse<LentListResponse>>({
        status: "SUCCESS",
        data: paginatedData,
      });
    },
  ),

  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/borrow/me/summary`,
    () => {
      return HttpResponse.json<ApiResponse<BorrowSummaryResponse>>({
        status: "SUCCESS",
        data: {
          totalOutgoingLa: 1000000000,
          monthlyOutgoingLa: 100000000,
          nextOutgoingDt: "2024-05-10",
        },
      });
    },
  ),

  http.get(`${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/me/summary`, () => {
    return HttpResponse.json<ApiResponse<LentSummaryResponse>>({
      status: "SUCCESS",
      data: {
        totalIncomingLa: 2000,
        monthlyIncomingLa: 150,
        nextIncomingDt: "2028-03-20",
      },
    });
  }),

  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/borrow/:contractId`,
    ({ params }) => {
      const contractId = params.contractId;
      return HttpResponse.json<ApiResponse<BorrowDetailResponse>>({
        status: "SUCCESS",
        data: mockBorrowDetail.find(
          (item) => item.contractId.toString() == contractId,
        ),
      });
    },
  ),

  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/:contractId`,
    ({ params }) => {
      const contractId = params.contractId;
      return HttpResponse.json<ApiResponse<LentDetailResponse>>({
        status: "SUCCESS",
        data: mockLentDetail.find(
          (item) => item.contractId.toString() == contractId,
        ),
      });
    },
  ),

  // http.get(
  //   `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/available-auctions`,
  //   async () => {
  //     console.log("[MSW] Entered Available Auctions handler");
  //     console.log("[MSW] Available Auctions Mock Data:", availableAuctionsMock);
  //     const response: ApiResponse<AvailableAuctionsResponse[]> = {
  //       status: "SUCCESS",
  //       data: availableAuctionsMock,
  //     };
  //     console.log("[MSW] Available Auctions Response:", response);
  //     return HttpResponse.json(response);
  //   },
  // ),
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/promissory-notes/debts/:debtId/prepayment`,
    () => {
      return HttpResponse.json<ApiResponse<EarlypayResponse>>({
        status: "SUCCESS",
        data: {
          message: "중도상환이 완료되었습니다.",
        },
      });
    },
  ),
];
