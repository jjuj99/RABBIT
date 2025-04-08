import { ApiResponse } from "@/shared/type/ApiResponse";
import {
  BorrowListResponse,
  LentListResponse,
  BorrowSummaryResponse,
  LentSummaryResponse,
  LentDetailResponse,
  BorrowDetailResponse,
  EarlypayResponse,
} from "../types/response";
import fetchOption from "@/shared/utils/fetchOption";
import { PaginationRequest } from "@/shared/type/PaginationRequest";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const getBorrowListAPI = async (
  pagination?: PaginationRequest,
): Promise<ApiResponse<BorrowListResponse>> => {
  const queryParams = pagination
    ? `?pageNumber=${pagination.pageNumber}&pageSize=${pagination.pageSize}`
    : "";

  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/borrow/me${queryParams}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getLentListAPI = async (
  pagination?: PaginationRequest,
): Promise<ApiResponse<LentListResponse>> => {
  const queryParams = pagination
    ? `?pageNumber=${pagination.pageNumber}&pageSize=${pagination.pageSize}`
    : "";

  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/me${queryParams}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getBorrowSummaryAPI = async (): Promise<
  ApiResponse<BorrowSummaryResponse>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/borrow/me/summary`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getLentSummaryAPI = async (): Promise<
  ApiResponse<LentSummaryResponse>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/me/summary`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getBorrowDetailAPI = async (
  contractId: string,
): Promise<ApiResponse<BorrowDetailResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/borrow/${contractId}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const getLentDetailAPI = async (
  contractId: string,
): Promise<ApiResponse<LentDetailResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/loans/lent/${contractId}`,
    fetchOption("GET"),
  );
  const data = await res.json();
  return data;
};

export const earlypayAPI = async (
  contractId: string,
  prepaymentAmount: number,
): Promise<ApiResponse<EarlypayResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/promissory-notes/debts/${contractId}/prepayment`,
    fetchOption("POST", { prepaymentAmount }),
  );
  const data = await res.json();
  return data;
};
