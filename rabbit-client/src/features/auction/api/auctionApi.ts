import fetchOption from "@/shared/utils/fetchOption";
import { AuctionListRequest } from "../types/request";
import {
  AuctionListResponse,
  BidHistoryResponse,
  BidListResponse,
  CreateAuctionResponse,
  PNInfoListResponse,
  SubmitAuctionBidResponse,
} from "../types/response";
import { ApiResponse } from "@/shared/type/ApiResponse";
import { NFTEventListResponse } from "@/shared/type/NFTEventList";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const getAuctionListAPI = async (
  params: AuctionListRequest,
): Promise<ApiResponse<AuctionListResponse>> => {
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
  const data = await res.json();
  return data;
};

export const getPNInfoListAPI = async (
  auctionId: number,
): Promise<ApiResponse<PNInfoListResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auctionId}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getBidListAPI = async (
  auctionId: number,
): Promise<ApiResponse<BidListResponse[]>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/${auctionId}`,
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
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/${auctionId}`,
    fetchOption("POST", { bidAmount }),
  );
  const data = await res.json();
  return data;
};

export const createAuctionAPI = async ({
  minimumBid,
  endDate,
  tokenId,
  sellerSign,
}: {
  minimumBid: number;
  endDate: string;
  tokenId: string;
  sellerSign: string;
}): Promise<ApiResponse<CreateAuctionResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions`,
    fetchOption("POST", {
      minimumBid,
      endDate,
      tokenId,
      sellerSign,
    }),
  );
  const data = await res.json();
  return data;
};

export const getBidHistoryAPI = async (): Promise<
  ApiResponse<BidHistoryResponse[]>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auction/my-bids`,
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
