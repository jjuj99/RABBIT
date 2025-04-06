import { ApiResponse } from "@/shared/type/ApiResponse";
import { http, HttpResponse } from "msw";
import {
  CancelContractResponse,
  CompleteContractResponse,
  ContractDetailResponse,
  ContractListResponse,
  CreateContractResponse,
  RejectContractResponse,
} from "../types/response";
import {
  completeContractHandler,
  mockContractDetailList,
  mockContractReceivedList,
  mockContractSentList,
} from "./mockData";
import { CreateContractRequest, RejectContractRequest } from "../types/request";
import { passType } from "@/shared/type/Types";
const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const contractHandler = [
  // 계약 요청 취소
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/:contractId/cancel`,
    async ({ params }) => {
      const { contractId } = params as { contractId: string };
      if (!contractId) {
        const failResponse: ApiResponse<null> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "계약 id가 없습니다.",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const successResponse: ApiResponse<CancelContractResponse> = {
        status: "SUCCESS",
        data: {
          contractId: Number(contractId),
          crId: 1,
          crName: "홍길동",
          drId: 2,
          drName: "오쌤",
          la: 1000000,
          ir: 10,
          contractDt: "2025-01-01",
          matDt: "2025-01-01",
          lt: 1000000,
          repayType: "EPIP",
          repayTypeName: "EPIP",
          addTerms: "추가 조건",
          dir: 1,
          earlypay: true,
          pnTransFlag: true,
          contractStatus: "CANCELED",
          contractStatusName: "취소",
          message: "취소 메시지",
          createdAt: "2025-01-01",
          updatedAt: "2025-01-01",
        },
        error: undefined,
      };
      console.log("계약 요청 취소 성공");
      return HttpResponse.json(successResponse);
    },
  ),
  // 계약 거절
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/:contractId/reject`,
    async ({ params, request }) => {
      const { contractId } = params as { contractId: string };
      if (!contractId) {
        const failResponse: ApiResponse<null> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "계약 id가 없습니다.",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const body = (await request.json()) as RejectContractRequest;
      if (!body) {
        const failResponse: ApiResponse<null> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "계약 거절 정보가 없습니다.",
          },
        };
        return HttpResponse.json(failResponse);
      }
      if (body.isCanceled) {
        console.log("아예거절");
        const rejectResponse: ApiResponse<RejectContractResponse> = {
          status: "SUCCESS",
          data: {
            contractId: Number(contractId),
            crId: 1,
            crName: "홍길동",
            drId: 2,
            drName: "오쌤",
            la: 1000000,
            ir: 10,
            contractStatus: "REJECTED",
            contractStatusName: "거절",
            message: "거절 메시지",
            rejectMessage: body.rejectMessage,
            rejectedAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          },
          error: undefined,
        };
        return HttpResponse.json(rejectResponse);
      } else {
        console.log("수정요청");
        const modifyResponse: ApiResponse<null> = {
          status: "SUCCESS",
          data: null,
          error: undefined,
        };
        return HttpResponse.json(modifyResponse);
      }
    },
  ),
  // 계약 요청 승인
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/:contractId/complete`,
    async ({ params, request }) => {
      const { contractId } = params as { contractId: string };
      if (!contractId) {
        const failResponse: ApiResponse<null> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "계약 id가 없습니다.",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const body = (await request.json()) as passType;
      if (!body) {
        const failResponse: ApiResponse<null> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "패스 정보가 없습니다.",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const successResponse: ApiResponse<CompleteContractResponse> = {
        status: "SUCCESS",
        data: completeContractHandler(contractId),
        error: undefined,
      };
      return HttpResponse.json(successResponse);
    },
  ),

  // 계약 상세 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts/:contractId`,
    async ({ request }) => {
      const url = new URL(request.url);
      const contractId = url.pathname.split("/").pop();

      if (!contractId) {
        const failResponse: ApiResponse<ContractDetailResponse> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "Invalid request",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const contractDetailResponse: ApiResponse<ContractDetailResponse> = {
        status: "SUCCESS",
        data: mockContractDetailList[contractId],
        error: undefined,
      };
      return HttpResponse.json(contractDetailResponse);
    },
  ),
  // 계약 목록 조회
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts`,
    async ({ request }) => {
      const url = new URL(request.url);
      const type = url.searchParams.get("searchCondition.type");
      console.log(type);

      if (type === "sent") {
        const sentResponse: ApiResponse<ContractListResponse[]> = {
          status: "SUCCESS",
          data: mockContractSentList,
          error: undefined,
        };
        return HttpResponse.json(sentResponse);
      } else if (type === "received") {
        const receivedResponse: ApiResponse<ContractListResponse[]> = {
          status: "SUCCESS",
          data: mockContractReceivedList,
          error: undefined,
        };
        return HttpResponse.json(receivedResponse);
      }
    },
  ),
  // 계약 생성
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/contracts`,
    async ({ request }) => {
      const body = (await request.json()) as CreateContractRequest;
      const contractId = "1234567890";
      if (!body) {
        const failResponse: ApiResponse<CreateContractResponse> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "Invalid request",
          },
        };
        return HttpResponse.json(failResponse);
      }
      const successResponse: ApiResponse<CreateContractResponse> = {
        status: "SUCCESS",
        data: {
          contractId,
          ...body,
        },
      };
      return HttpResponse.json(successResponse);
    },
  ),
];
