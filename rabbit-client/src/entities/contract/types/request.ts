export interface CreateContractRequest {
  crEmail: string;
  crName: string;
  crWallet: string;
  defCnt: number;
  dir: number;
  drName: string;
  drPhone: string;
  drWallet: string;
  earlypay: boolean;
  earlypayFee: number;
  ir: number;
  la: number;
  lt: number;
  mpDt: number;
  pnTransFlag: boolean;
  repayType: "EPIP" | "EPP" | "BP";
  addTerms: string | null;
  message: string | null;
  passAuthToken: string;
  txId: string;
  authResultCode: string;
  contractDt: string;
}

export interface RejectContractRequest {
  contractId: string;
  rejectMessage: string;
  isCanceled: boolean;
}
