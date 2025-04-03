import { ContractStatus } from "../constant/statusConfig";

export interface CreateContractResponse {
  contractId: string;
}
export interface ContractListResponse {
  id: string;
  debtorName: string;
  debtorWallet: string;
  amount: number;
  interestRate: number;
  maturityDate: string;
  monthlyPaymentDate: number;
  loanTerm: number;
  repaymentType: "EPIP" | "EPP" | "BP";
  contractDate: string;
  createdAt: string;
  status: ContractStatus;
}
