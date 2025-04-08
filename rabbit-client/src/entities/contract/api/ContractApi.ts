import { ApiResponse } from "@/shared/type/ApiResponse";
import { passType } from "@/shared/type/Types";
import fetchOption from "@/shared/utils/fetchOption";
import {
  CancelContractResponse,
  CompleteContractResponse,
  ContractDetailResponse,
  ContractListResponse,
  CreateContractResponse,
  RejectContractResponse,
} from "../types/response";
import { CreateContractRequest, RejectContractRequest } from "../types/request";
import { Pagination } from "@/shared/type/PaginationResponse";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const createContractAPI = async (
  data: CreateContractRequest,
): Promise<ApiResponse<CreateContractResponse>> => {
  console.log("생성요청 보냄");
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts`,
    fetchOption("POST", data),
  );
  console.log("생성요청 응답 받음");
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error.message || "Failed to create contract");
  }
  return await response.json();
};

export const getContractListAPI = async (
  type: "sent" | "received",
): Promise<ApiResponse<Pagination<ContractListResponse>>> => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts?searchCondition.type=${type}`,
    fetchOption("GET"),
  );
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error.message || "Failed to get contract list");
  }
  return await response.json();
};

export const getContractDetailAPI = async (
  contractId: string,
): Promise<ApiResponse<ContractDetailResponse>> => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/${contractId}`,
    fetchOption("GET"),
  );
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error.message || "Failed to get contract detail");
  }
  return await response.json();
};

export const rejectContractAPI = async (
  data: RejectContractRequest,
): Promise<ApiResponse<RejectContractResponse>> => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/${data.contractId}/reject`,
    fetchOption("POST", {
      rejectMessage: data.rejectMessage,
      isCanceled: data.isCanceled,
    }),
  );
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error.message || "Failed to reject contract");
  }
  return await response.json();
};

export const cancelContractAPI = async (
  contractId: string,
): Promise<ApiResponse<CancelContractResponse>> => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/${contractId}/cancel`,
    fetchOption("POST"),
  );
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error.message || "Failed to cancel contract");
  }
  return await response.json();
};

export const completeContractAPI = async (
  contractId: string,
  pass: passType,
): Promise<ApiResponse<CompleteContractResponse>> => {
  const response = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/${contractId}/complete`,
    fetchOption("POST", pass),
  );
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error.message || "Failed to complete contract");
  }
  return await response.json();
};
