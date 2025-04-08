export interface AccountHistoryType {
  type: "deposit" | "withdraw";
  amount: number;
  date: string;
}

export interface ResponseConfirm {
  message: string | null;
}
export interface BankList {
  bankId: number;
  bankName: string;
}

export interface Verify1wonResponse {
  email: string;
  authCode: string;
}

export interface Send1wonResponse {
  message: string;
}

export interface AccountHistoryResponse {
  type: "DEPOSIT" | "WITHDRAW";
  amount: number;
  createdAt: string;
}

export interface WithdrawResponse {
  message: string;
}
