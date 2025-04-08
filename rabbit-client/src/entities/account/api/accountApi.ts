import { ApiResponse } from "@/shared/type/ApiResponse";
import fetchOption from "@/shared/utils/fetchOption";
import {
  RequestConfirm,
  Send1wonRequest,
  Verify1wonRequest,
  WithdrawRequest,
} from "../types/request";
import {
  AccountHistoryResponse,
  BankList,
  ResponseConfirm,
  Send1wonResponse,
  Verify1wonResponse,
  WithdrawResponse,
} from "../types/response";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const ConfirmAPI = async (
  confirmData: RequestConfirm,
): Promise<ApiResponse<ResponseConfirm>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/coins/confirm`,
    fetchOption("POST", confirmData),
  );
  if (!res.ok) {
    throw new Error("충전 확인에 실패했습니다");
  }
  const data = await res.json();
  return data;
};

export const GetBankListAPI = async (): Promise<ApiResponse<BankList[]>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/bank/auth/list`,
    fetchOption("GET", undefined, "access"),
  );
  if (!res.ok) {
    throw new Error("은행 목록을 불러오는데 실패했습니다");
  }
  const data = await res.json();
  return data;
};

export const Send1wonAPI = async ({
  email,
  accountNumber,
}: Send1wonRequest): Promise<ApiResponse<Send1wonResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/bank/auth/refund-account/send`,
    fetchOption("POST", { email, accountNumber }, "access"),
  );
  if (!res.ok) {
    throw new Error("1원 송금에 실패했습니다");
  }
  const data = await res.json();
  return data;
};

export const Verify1wonAPI = async ({
  email,
  authCode,
}: Verify1wonRequest): Promise<ApiResponse<Verify1wonResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/bank/auth/refund-account/verify`,
    fetchOption("POST", { email, authCode }, "access"),
  );
  if (!res.ok) {
    throw new Error("1원 인증에 실패했습니다");
  }
  const data = await res.json();
  return data;
};

export const WithdrawAPI = async (
  req: WithdrawRequest,
): Promise<ApiResponse<WithdrawResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/coins/withdraw`,
    fetchOption("POST", req, "access"),
  );
  if (!res.ok) {
    throw new Error("출금에 실패했습니다");
  }
  const data = await res.json();
  return data;
};

export const getAccountTransferHistoryAPI = async (): Promise<
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
