// API 응답 타입

export interface AuctionListResponse {
  content: PNInfoListResponse[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  hasNext: boolean;
}

export interface PNInfoListResponse {
  auction_id: number;
  price: number;
  end_date: string;
  ir: number; //
  created_at: string;
  repay_type: string; //
  total_amount: number; //
  mat_dt: string; //
  dir: number; //
  la: number; //
  earlypay_flag: boolean; //
  earlypay_fee: number; //
  credit_score: number; //
  def_cnt: number; //
}

export interface BidListResponse {
  bid_id: number;
  bid_amount: number;
  created_at: string;
}

export interface SubmitAuctionBidResponse {
  message: string;
}
export interface CreateAuctionResponse {
  message: string;
}
