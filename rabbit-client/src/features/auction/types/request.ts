export interface AuctionListRequest {
  min_price?: string; // minPrice
  max_price?: string; // maxPrice
  max_ir?: string; // maxIR
  min_ir?: string; // minIR
  max_rate?: string; // maxRate
  repay_type?: string[]; // paymentTypes
  mat_term?: string; // maturity
  mat_start?: string; // startDate
  mat_end?: string; // endDate
}
