import fetchOption from "@/shared/utils/fetchOption";
import { AuctionListRequest } from "../types/request";
import {
  AuctionListResponse,
  BidListResponse,
  CreateAuctionResponse,
  PNInfoListResponse,
  SubmitAuctionBidResponse,
} from "../types/response";
import { ApiResponse } from "@/shared/type/ApiResponse";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const getAuctionListAPI = async (
  params: AuctionListRequest,
): Promise<ApiResponse<AuctionListResponse>> => {
  const queryParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      if (key === "repay_type" && Array.isArray(value)) {
        value.forEach((type) => queryParams.append(key, type));
      } else {
        queryParams.append(key, value.toString());
      }
    }
  });

  const queryString = queryParams.toString();
  const url = `${VITE_API_URL}/${VITE_API_VERSION}/auctions${queryString ? `?${queryString}` : ""}`;
  const res = await fetch(url, fetchOption("GET", {}, "access"));
  const data = await res.json();
  return data;
};

export const getPNInfoListAPI = async (
  auction_id: number,
): Promise<ApiResponse<PNInfoListResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions/${auction_id}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getBidListAPI = async (
  auction_id: number,
): Promise<ApiResponse<BidListResponse[]>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/${auction_id}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const SubmitAuctionBidAPI = async (
  auction_id: number,
  bid_amount: number,
): Promise<ApiResponse<SubmitAuctionBidResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/bids/auction/${auction_id}`,
    fetchOption("POST", { bid_amount }),
  );
  const data = await res.json();
  return data;
};

export const createAuctionAPI = async ({
  minimum_bid,
  end_date,
  token_id,
  seller_sign,
}: {
  minimum_bid: number;
  end_date: string;
  token_id: string;
  seller_sign: string;
}): Promise<ApiResponse<CreateAuctionResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auctions`,
    fetchOption("POST", {
      minimum_bid,
      end_date,
      token_id,
      seller_sign,
    }),
  );
  const data = await res.json();
  return data;
};
