import { ContractStatus } from "../constant/statusConfig";

export interface CreateContractResponse {
  contractId: string;
  drPhone: string;
  drName: string;
  drWallet: string;
  crEmail: string;
  crName: string;
  crWallet: string;
  la: number;
  ir: number;
  lt: number;
  repayType: "EPIP" | "EPP" | "BP";
  mpDt: number;
  dir: number;
  contractDt: string;
  defCnt: number;
  pnTransFlag: boolean;
  earlypay: boolean;
  earlypayFee: number;
  addTerms: string | null;
  message: string | null;
}

export interface ContractListResponse {
  id: string;
  name: string;
  walletAddress: string;
  la: number;
  ir: number;
  matDt: string;
  mpDt: number;
  lt: number;
  repayType: "EPIP" | "EPP" | "BP";
  repayTypeName: string;
  contractDt: string;
  createdAt: string;
  contractStatus: ContractStatus;
  contractStatusName: string;
}

export interface ContractDetailResponse {
  contractId: number;
  crId: number;
  crName: string;
  crWallet: string;
  drId: number;
  drName: string;
  drWallet: string;
  la: number;
  ir: number;
  contractDt: string;
  matDt: string;
  lt: number;
  earlypayFee: number;
  repayType: "EPIP" | "EPP" | "BP";
  repayTypeName: string;
  mpDt: number;
  dir: number;
  defCnt: number;
  earlypay: boolean;
  pnTransFlag: boolean;
  addTerms?: string;
  contractStatus: ContractStatus;
  contractStatusName: string;
  createdAt: string;
  updatedAt: string;
  remainingDays: number;
  matAmt: number;
  message?: string;
  rejectMessage?: string;
  rejectedAt?: string;
}

export interface CancelContractResponse {
  contractId: number;
  crId: number;
  crName: string;
  drId: number;
  drName: string;
  la: number;
  ir: number;
  contractDt: string;
  matDt: string;
  lt: number;
  repayType: "EPIP" | "EPP" | "BP";
  repayTypeName: string;
  dir: number;
  earlypay: boolean;
  pnTransFlag: boolean;
  addTerms: string;
  contractStatus: ContractStatus;
  contractStatusName: string;
  message: string;
  createdAt: string;
  updatedAt: string;
}

export interface RejectContractResponse {
  contractId: number;
  crId: number;
  crName: string;
  drId: number;
  drName: string;
  la: number;
  ir: number;
  contractStatus: ContractStatus;
  contractStatusName: string;
  message: string;
  rejectMessage: string;
  rejectedAt: string;
  updatedAt: string;
}
export interface CompleteContractResponse {
  contractId: number;
  crId: number;
  crName: string;
  drId: number;
  drName: string;
  la: number;
  ir: number;
  contractDt: string;
  matDt: string;
  lt: number;
  earlypayFee: number;
  repayType: "EPIP" | "EPP" | "BP";
  repayTypeName: string;
  mpDt: number;
  dir: number;
  defCnt: number;
  earlypay: boolean;
  pnTransFlag: boolean;
  addTerms: string;
  contractStatus: ContractStatus;
  contractStatusName: string;
  message: string;
  rejectMessage: string;
  rejectedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface RejectContractResponse {
  contractId: number;
  crId: number;
  crName: string;
  drId: number;
  drName: string;
  la: number;
  ir: number;
  contractStatus: ContractStatus;
  contractStatusName: string;
  message: string;
  rejectMessage: string;
  rejectedAt: string;
  updatedAt: string;
}
