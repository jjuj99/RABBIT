import { http, HttpResponse } from "msw";
import { ApiResponse } from "@/shared/type/ApiResponse";
import { AuctionListRequest } from "../types/request";
import {
  AuctionListResponse,
  AuctionSimilarListResponse,
  BidListResponse,
  PNInfoListResponse,
  SubmitAuctionBidResponse,
} from "../types/response";
import {
  mockAuctionList,
  mockAuctionSimilarList,
  mockBidHistoryData,
  mockBidList,
} from "./data";
import { nftEvents } from "@/entities/common/mocks/data";
import { NFTEventListResponse } from "@/shared/type/NFTEventList";

interface CreateAuctionRequest {
  minimumBid: number;
  endDate: string;
  tokenId: string;
  sellerSign: string;
}

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const handlers = [
  // 경매 상세 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/:auctionId`,
    ({ params }) => {
      const auctionId = Number(params.auctionId);

      const auction = mockAuctionList.content[auctionId - 1];

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

    let filteredList = [...mockAuctionList.content];

    if (params.minPrice && Number(params.minPrice) !== 0) {
      filteredList = filteredList.filter(
        (item) => item.price >= Number(params.minPrice),
      );
    }

    if (params.maxPrice && Number(params.maxPrice) !== 0) {
      filteredList = filteredList.filter(
        (item) => item.price <= Number(params.maxPrice),
      );
    }

    if (params.maxIr && Number(params.maxIr) !== 0) {
      filteredList = filteredList.filter(
        (item) => item.ir <= Number(params.maxIr),
      );
    }

    if (params.minIr && Number(params.minIr) !== 0) {
      filteredList = filteredList.filter(
        (item) => item.ir >= Number(params.minIr),
      );
    }

    if (params.repayType) {
      const repayTypeMap: Record<string, string> = {
        "1": "원리금 균등 상환", // 원리금 균등 상환
        "2": "원금 균등 상환", // 원금 균등 상환
        "3": "만기 일시 상환", // 만기 일시 상환
      };

      filteredList = filteredList.filter((item) => {
        const repayType = params.repayType;
        if (!repayType) return false;

        return Array.isArray(repayType)
          ? repayType.some((type) => repayTypeMap[type] === item.repayType)
          : repayTypeMap[repayType] === item.repayType;
      });
    }

    // 페이지네이션 적용
    const pageNumber = params.pageNumber ? Number(params.pageNumber) : 0;
    const pageSize = params.pageSize ? Number(params.pageSize) : 10;
    const startIndex = pageNumber * pageSize;
    const endIndex = startIndex + pageSize;
    const paginatedList = filteredList.slice(startIndex, endIndex);
    const totalElements = filteredList.length;
    const totalPages = Math.ceil(totalElements / pageSize);
    const isLastPage = pageNumber >= totalPages - 1;

    const response: ApiResponse<AuctionListResponse> = {
      status: "SUCCESS",
      data: {
        content: paginatedList,
        pageNumber: pageNumber,
        pageSize: pageSize,
        totalElements: totalElements,
        totalPages: totalPages,
        last: isLastPage,
        hasNext: !isLastPage,
      },
    };

    return HttpResponse.json(response);
  }),

  // 입찰 목록 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/:auctionId`,
    ({ params }) => {
      const auctionId = Number(params.auctionId);
      const auction = mockAuctionList.content.find(
        (item) => item.auctionId === auctionId,
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
      const body = (await request.json()) as { bidAmount: number };
      const { bidAmount } = body;

      // 경매 존재 여부 확인
      const auction = mockAuctionList.content.find(
        (item) => item.auctionId === auctionId,
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
      if (bidAmount <= auction.price) {
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

  // 경매 생성 API
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions`,
    async ({ request }) => {
      const body = (await request.json()) as CreateAuctionRequest;
      const { minimumBid, endDate, tokenId, sellerSign } = body;

      // 필수 파라미터 검증
      if (!minimumBid || !endDate || !tokenId || !sellerSign) {
        return HttpResponse.json(
          {
            status: "ERROR",
            data: null,
            error: {
              statusCode: 400,
              message: "필수 파라미터가 누락되었습니다.",
            },
          },
          { status: 400 },
        );
      }

      // 최소 입찰가 검증
      if (minimumBid <= 0) {
        return HttpResponse.json(
          {
            status: "ERROR",
            data: null,
            error: {
              statusCode: 400,
              message: "최소 입찰가는 0보다 커야 합니다.",
            },
          },
          { status: 400 },
        );
      }

      // 경매 종료 시간 검증
      const endDateObj = new Date(endDate);
      const now = new Date();
      if (endDateObj <= now) {
        return HttpResponse.json(
          {
            status: "ERROR",
            data: null,
            error: {
              statusCode: 400,
              message: "경매 종료 시간은 현재 시간 이후여야 합니다.",
            },
          },
          { status: 400 },
        );
      }

      // 성공 응답
      return HttpResponse.json(
        {
          status: "SUCCESS",
          data: {
            message: "경매 등록 성공했습니다.",
          },
          error: null,
        },
        { status: 201 },
      );
    },
  ),

  // 입찰 내역 조회
  http.get(`${VITE_API_URL}/${VITE_API_VERSION}/auction/my-bids`, () => {
    const response: ApiResponse<typeof mockBidHistoryData> = {
      status: "SUCCESS",
      data: mockBidHistoryData,
    };
    return HttpResponse.json(response);
  }),

  // 경매 이벤트 목록 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/:auctionId/event`,
    () => {
      const auction = {
        eventList: nftEvents,
      };

      if (!auction) {
        return HttpResponse.json({
          status: "ERROR",
          error: {
            statusCode: 404,
            message: "경매를 찾을 수 없습니다.",
          },
        });
      }

      const response: ApiResponse<NFTEventListResponse> = {
        status: "SUCCESS",
        data: auction,
      };

      return HttpResponse.json(response);
    },
  ),

  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/:auctionId/similar`,
    () => {
      const response: ApiResponse<AuctionSimilarListResponse> = {
        status: "SUCCESS",
        data: mockAuctionSimilarList,
      };

      return HttpResponse.json(response);
    },
  ),
];
