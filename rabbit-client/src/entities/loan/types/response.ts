export interface LoanInfoResponse {
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
