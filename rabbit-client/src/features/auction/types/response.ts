import { Pagination } from "@/shared/type/PaginationResponse";

//경매 리스트
export type AuctionListResponse = Pagination<PNInfoListResponse>;

export interface PNInfoListResponse {
  //경매 정보..
  auctionId: number;
  price: number;
  endDate: string;
  ir: number; //
  createdAt: string;
  repayType: string; //
  totalAmount: number; //
  matDt: string; //
  dir: number; //
  la: number; //
  earlypayFlag: boolean; //
  earlypayFee: number; //
  creditScore: number; //
  defCnt: number; //
}

export interface BidListResponse {
  bidId: number;
  bidAmount: number;
  createdAt: string;
}

export interface SubmitAuctionBidResponse {
  message: string;
}
export interface CreateAuctionResponse {
  message: string;
}

export interface BidHistoryResponse {
  auctionId: number;
  bidDate: string;
  auctionStatus: "COMPLETED" | "IN_PROGRESS";
  price: number;
  bidAmount: number;
  bidStatus: "WON" | "LOST" | "PENDING";
  bidStatusName: "입찰중" | "낙찰" | "낙찰 실패";
  bidderNum: number;
}
