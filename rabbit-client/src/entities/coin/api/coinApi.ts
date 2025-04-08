import { ApiResponse } from "@/shared/type/ApiResponse";
import fetchOption from "@/shared/utils/fetchOption";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export interface AccountHistoryResponse {
  type: "DEPOSIT" | "WITHDRAW";
  amount: number;
  createdAt: string;
}

export const getCoinTransferHistoryAPI = async (): Promise<
  ApiResponse<AccountHistoryResponse[]>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/coins/transactions`,
    fetchOption("GET"),
  );
  if (!res.ok) {
    throw new Error("코인 이체 내역 조회 실패");
  }
  const data = await res.json();
  return data;
};
