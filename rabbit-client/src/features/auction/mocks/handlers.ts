import { http, HttpResponse } from "msw";
import { ApiResponse } from "@/shared/type/ApiResponse";
import { AuctionListRequest } from "../types/request";
import {
  AuctionListResponse,
  BidListResponse,
  PNInfoListResponse,
  SubmitAuctionBidResponse,
} from "../types/response";
import { mockAuctionList, mockBidList } from "./data";

const VITE_API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION || "v1";

console.log("MSW API URL:", VITE_API_URL, "Version:", VITE_API_VERSION);

export const handlers = [
  // 경매 상세 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/:auctionId`,
    ({ params }) => {
      const auctionId = Number(params.auctionId);

      const auction = mockAuctionList[auctionId - 1];

      if (!auction) {
        return HttpResponse.json({
          status: "ERROR",
          error: {
            statusCode: 404,
            message: "경매를 찾을 수 없습니다.",
          },
        });
      }

      const response: ApiResponse<PNInfoListResponse> = {
        status: "SUCCESS",
        data: auction,
      };

      return HttpResponse.json(response);
    },
  ),

  // 경매 목록 조회
  http.get(`${VITE_API_URL}/${VITE_API_VERSION}/auctions`, ({ request }) => {
    const url = new URL(request.url);
    const params = Object.fromEntries(url.searchParams) as AuctionListRequest;

    let filteredList = [...mockAuctionList];

    if (params.min_price) {
      filteredList = filteredList.filter(
        (item) => item.price >= Number(params.min_price),
      );
    }

    if (params.max_price) {
      filteredList = filteredList.filter(
        (item) => item.price <= Number(params.max_price),
      );
    }

    if (params.max_ir) {
      filteredList = filteredList.filter(
        (item) => item.ir <= Number(params.max_ir),
      );
    }

    if (params.min_ir) {
      filteredList = filteredList.filter(
        (item) => item.ir >= Number(params.min_ir),
      );
    }

    if (params.repay_type) {
      const repayTypeMap: Record<string, string> = {
        "1": "원리금 균등 상환", // 원리금 균등 상환
        "2": "원금 균등 상환", // 원금 균등 상환
        "3": "만기 일시 상환", // 만기 일시 상환
      };

      filteredList = filteredList.filter((item) => {
        const repayType = params.repay_type;
        if (!repayType) return false;

        return Array.isArray(repayType)
          ? repayType.some((type) => repayTypeMap[type] === item.repay_type)
          : repayTypeMap[repayType] === item.repay_type;
      });
    }

    const response: ApiResponse<AuctionListResponse> = {
      status: "SUCCESS",
      data: {
        content: filteredList,
        pageNumber: 0,
        pageSize: 10,
        totalElements: filteredList.length,
        totalPages: Math.ceil(filteredList.length / 10),
        last: true,
        hasNext: false,
      },
    };

    return HttpResponse.json(response);
  }),

  // 입찰 목록 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/:auctionId`,
    ({ params }) => {
      const auctionId = Number(params.auctionId);
      const auction = mockAuctionList.find(
        (item) => item.auction_id === auctionId,
      );

      if (!auction) {
        return HttpResponse.json({
          status: "ERROR",
          error: {
            statusCode: 404,
            message: "경매를 찾을 수 없습니다.",
          },
        });
      }

      const response: ApiResponse<BidListResponse[]> = {
        status: "SUCCESS",
        data: mockBidList,
      };

      return HttpResponse.json(response);
    },
  ),

  // 입찰 API
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/:auctionId`,
    async ({ params, request }) => {
      const auctionId = Number(params.auctionId);
      const body = (await request.json()) as { bid_amount: number };
      const { bid_amount } = body;

      // 경매 존재 여부 확인
      const auction = mockAuctionList.find(
        (item) => item.auction_id === auctionId,
      );

      if (!auction) {
        return HttpResponse.json(
          {
            status: "ERROR",
            error: {
              statusCode: 404,
              message: "경매를 찾을 수 없습니다.",
            },
          },
          { status: 404 },
        );
      }

      // 입찰 금액이 현재 가격보다 낮은 경우
      if (bid_amount <= auction.price) {
        return HttpResponse.json(
          {
            status: "ERROR",
            error: {
              statusCode: 400,
              message: "입찰 금액은 현재 가격보다 높아야 합니다.",
            },
          },
          { status: 400 },
        );
      }

      // 성공 응답
      const response: ApiResponse<SubmitAuctionBidResponse> = {
        status: "SUCCESS",
        data: {
          message: "입찰이 성공적으로 완료되었습니다.",
        },
      };

      return HttpResponse.json(response, { status: 200 });
    },
  ),
];
