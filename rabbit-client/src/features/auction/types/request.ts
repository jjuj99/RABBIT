export interface AuctionListRequest {
  minPrice?: number; // minPrice
  maxPrice?: number; // maxPrice
  maxIr?: number; // maxIR
  minIr?: number; // minIR
  maxRate?: number; // maxRate
  repayType?: string[]; // paymentTypes
  matTerm?: number; // maturity
  matStart?: string; // startDate
  matEnd?: string; // endDate
  pageNumber?: number; // pageNumber
  pageSize?: number; // pageSize
}
