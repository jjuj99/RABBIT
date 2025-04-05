export type repayType =
  | "원리금 균등 상환"
  | "원금 균등 상환"
  | "이자 균등 상환";

export type pnStatus = "정상" | "연체";

export type auctionStatus = "ING" | "COMPLETED" | "FAILED" | "CANCELED";

export type auctionStatusName = "진행중" | "완료" | "유찰" | "취소";

export type bidStatus = "WON" | "LOST" | "PENDING";

export type bidStatusName = "입찰중" | "낙찰" | "낙찰 실패";

export interface passType {
  passAuthToken: string;
  txId: string;
  authResultCode: "SUCCESS" | "FAIL";
  phoneNumber: string;
  name: string;
}
