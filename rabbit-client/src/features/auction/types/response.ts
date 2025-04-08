import { Pagination } from "@/shared/type/PaginationResponse";
import {
  auctionStatus,
  auctionStatusName,
  bidStatus,
  bidStatusName,
} from "@/shared/type/Types";

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

export interface AuctionDetailResponse {
  auctionId: number;
  auctionStatus: auctionStatus;
  message: string;
}

export interface SubmitAuctionBidResponse {
  message: string;
}
export interface CreateAuctionResponse {
  auctionId: number;
  message: string;
}

export interface BidHistoryResponse {
  auctionId: number;
  bidDate: string;
  auctionStatus: auctionStatus;
  auctionStatusName: auctionStatusName;
  price: number;
  bidAmount: number;
  bidStatus: bidStatus;
  bidStatusName: bidStatusName;
  bidderNum: number;
}

export interface AvailableAuctionsResponse {
  crId: number; //채권자 id
  crName: string; //채권자 이금
  matDt: string; //만기일
  tokenId: string;
  la: number; //원금
  ir: number; //이자율
  totalAmount: number; //만기수취액
  repayType: "원리금 균등 상환" | "원금 균등 상환" | "만기 일시 상환"; //상환 방식
  dir: number; //연체 이자율
  earlypayFlag: boolean; //중도 상환 가능 여부
  earlypayFee: number; //중도 상환 수수료
  defCnt: number; //연체 횟수
  creditScore: number; //신용점수
}

export interface TargetAuction {
  auctionId: number;
  rp: number;
  rd: number;
  rr: number;
  percentile: number;
}

export interface ComparisonAuction {
  auctionId: number;
  rp: number;
  rd: number;
  rr: number;
  percentile: number;
}

export interface AuctionSimilarListResponse {
  targetAuction: TargetAuction;
  comparisonAuctions: ComparisonAuction[];
}
