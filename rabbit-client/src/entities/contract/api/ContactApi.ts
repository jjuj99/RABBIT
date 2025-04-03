import fetchOption from "@/shared/utils/fetchOption";

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

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const createContract = async (data: CreateContractRequest) => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts`,
    fetchOption("POST", data),
  );
  return response.json();
};
