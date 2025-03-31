export interface BorrowInfoResponse {
  tokenId: string;
  tokenImage: string;
  isOverdue: boolean;
  creditorName: string;
  creditorWallet: string;
  loanAmount: number;
  interestRate: number;
  endDate: string;
  endDays: number;
  nextDueDate: string;
  overDueAmount: number | null;
  overdueDays: number | null;
}

export interface LentInfoResponse {
  tokenId: string;
  tokenImage: string;
  isOverdue: boolean;
  debtorName: string;
  debtorWallet: string;
  loanAmount: number;
  returnRate: number;
  endDate: string;
  endDays: number;
  nextDueDate: string;
  overDueAmount: number | null;
  overdueDays: number | null;
}
