import { Pagination } from "@/shared/type/PaginationResponse";
import { NFTEvent } from "@/shared/type/NFTEventList";
//페이지네이션
export type BorrowListResponse = Pagination<BorrowInfoResponse>;
export type LentListResponse = Pagination<LentInfoResponse>;

//채무 정보
export interface BorrowInfoResponse {
  tokenId: string;
  nftImage: string;
  drName: string;
  drWallet: string;
  la: number;
  ir: number;
  matDt: string;
  remainTerms: number;
  pnStatus: string;
  nextMpDt: string;
  nextAmount: number;
  aoi: number | null;
  aoiDays: number | null;
}

//채권 정보
export interface LentInfoResponse {
  tokenId: string;
  nftImage: string;
  drName: string;
  drWallet: string;
  la: number;
  ir: number;
  matDt: string;
  remainTerms: number;
  pnStatus: string;
  nextMpDt: string;
  nextAmount: number;
  aoi: number | null;
  aoiDays: number | null;
}

//채권 요약
export interface LentSummaryResponse {
  totalIncomingLa: number;
  monthlyIncomingLa: number;
  nextIncomingDt: string;
}

//채무 요약
export interface BorrowSummaryResponse {
  totalOutgoingLa: number;
  monthlyOutgoingLa: number;
  nextOutgoingDt: string;
}

//채무 상세
export interface BorrowDetailResponse {
  tokenId: string;
  nftImage: string;
  crName: string;
  crWallet: string;
  la: number;
  totalAmount: number;
  repayType: string;
  ir: number;
  dir: number;
  defCnt: number;
  contractDt: string;
  matDt: string;
  remainTerms: number;
  progressRage: number;
  pnStatus: string;
  nextMpDt: string;
  nextAmount: number;
  aoi: number | null;
  aoiDays: number | null;
  earlypayFlag: boolean;
  earlypayFee: number;
  accel: number;
  accelDir: number;
  addTerms?: string[];
  eventList: NFTEvent[];
}

//채권 상세
export interface LentDetailResponse {
  tokenId: string;
  nftImage: string;
  drName: string;
  drWallet: string;
  la: number;
  totalAmount: number;
  repayType: string;
  ir: number;
  dir: number;
  defCnt: number;
  contractDt: string;
  matDt: string;
  remainTerms: number;
  progressRage: number;
  pnStatus: string;
  nextMpDt: string;
  nextAmount: number;
  aoi: number | null;
  aoiDays: number | null;
  earlypayFlag: boolean;
  earlypayFee: number;
  accel: number;
  accelDir: number;
  addTerms?: string[];
  eventList: NFTEvent[];
}
