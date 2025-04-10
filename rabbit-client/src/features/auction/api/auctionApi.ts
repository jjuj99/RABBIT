import fetchOption from "@/shared/utils/fetchOption";
import { AuctionListRequest } from "../types/request";
import {
  AuctionDetailResponse,
  AuctionForceEndResponse,
  AuctionListResponse,
  AuctionSimilarListResponse,
  AvailableAuctionsResponse,
  BidHistoryResponse,
  BidListResponse,
  CreateAuctionResponse,
  MyAuctionListResponse,
  PNInfoListResponse,
  SubmitAuctionBidResponse,
} from "../types/response";
import { ApiResponse } from "@/shared/type/ApiResponse";
import { NFTEventListResponse } from "@/shared/type/NFTEventList";
import { Pagination } from "@/shared/type/PaginationResponse";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const getAuctionListAPI = async (
  params: AuctionListRequest,
): Promise<AuctionListResponse> => {
  const queryParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      if (key === "repayType" && Array.isArray(value)) {
        value.forEach((type) => queryParams.append(key, type));
      } else {
        queryParams.append(key, value.toString());
      }
    }
  });

  const queryString = queryParams.toString();
  const url = `${VITE_API_URL}/${VITE_API_VERSION}/auctions${queryString ? `?${queryString}` : ""}`;
  const res = await fetch(url, fetchOption("GET"));
  if (!res.ok) {
    throw new Error("Failed to fetch auction list");
  }
  const { data } = await res.json();
  return data;
};

export const getPNInfoListAPI = async (
  auctionId: number,
): Promise<ApiResponse<PNInfoListResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}/info`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getBidListAPI = async (
  auctionId: number,
): Promise<ApiResponse<BidListResponse[]>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}/bids/list

`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const SubmitAuctionBidAPI = async (
  auctionId: number,
  bidAmount: number,
): Promise<ApiResponse<SubmitAuctionBidResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}/bids`,
    fetchOption("POST", { bidAmount }),
  );
  const data = await res.json();
  return data;
};

export const createAuctionAPI = async ({
  minimumBid,
  endDate,
  tokenId,
}: {
  minimumBid: number;
  endDate: string;
  tokenId: string;
}): Promise<ApiResponse<CreateAuctionResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/add`,
    fetchOption("POST", {
      minimumBid,
      endDate,
      tokenId,
    }),
  );
  const data = await res.json();
  return data;
};

export const deleteAuctionAPI = async (
  auctionId: number,
): Promise<ApiResponse<AuctionDetailResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/internal/${auctionId}`,

    fetchOption("DELETE"),
  );
  const data = await res.json();
  return data;
};

export const getBidHistoryAPI = async (): Promise<
  ApiResponse<Pagination<BidHistoryResponse>>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/my-bids`,
    fetchOption("GET", undefined, "access"),
  );
  const data = await res.json();
  return data;
};

export const getNFTEventListAPI = async (
  auctionId: number,
): Promise<ApiResponse<NFTEventListResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}/event`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getAuctionSimilarListAPI = async (
  auctionId: number,
): Promise<ApiResponse<AuctionSimilarListResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}/similar`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getAvailableAuctionsAPI = async (): Promise<
  ApiResponse<Pagination<AvailableAuctionsResponse>>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/available-auctions`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const AuctionForceEndAPI = async ({
  auctionId,
}: {
  auctionId: number;
}): Promise<ApiResponse<AuctionForceEndResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}/force-end`,
    fetchOption("POST"),
  );
  const data = await res.json();
  return data;
};

export const getMyAuctionListAPI = async (): Promise<
  ApiResponse<MyAuctionListResponse>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/my-auctions`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};
