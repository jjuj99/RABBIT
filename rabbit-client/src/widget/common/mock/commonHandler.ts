import { ApiResponse } from "@/shared/type/ApiResponse";
import { http, HttpResponse } from "msw";
import { CommonCodeResponse, SearchUserResponse } from "../types/response";
const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

const contractStatus = [
  {
    codeType: "CONTRACT_STATUS",
    code: "REQUESTED",
    codeName: "요청",
    description: "요청",
    displayOrder: 1,
    activeFlag: true,
  },
  {
    codeType: "CONTRACT_STATUS",
    code: "MODIFICATION_REQUESTED",
    codeName: "수정 요청",
    description: "수정 요청",
    displayOrder: 2,
    activeFlag: true,
  },
  {
    codeType: "CONTRACT_STATUS",
    code: "CONTRACTED",
    codeName: "차용증 발행",
    description: "차용증 발행",
    displayOrder: 3,
    activeFlag: true,
  },
  {
    codeType: "CONTRACT_STATUS",
    code: "CANCELED",
    codeName: "취소",
    description: "취소",
    displayOrder: 4,
    activeFlag: true,
  },
  {
    codeType: "CONTRACT_STATUS",
    code: "REJECTED",
    codeName: "거절",
    description: "거절",
    displayOrder: 5,
    activeFlag: true,
  },
];

export const commonHandler = [
  http.get(
    `${VITE_API_URL}/${VITE_API_VERSION}/codes/CONTRACT_STATUS/active`,
    () => {
      const successResponse: ApiResponse<CommonCodeResponse[]> = {
        status: "SUCCESS",
        data: contractStatus,
      };
      return HttpResponse.json(successResponse);
    },
  ),
  http.get(`${VITE_API_URL}/${VITE_API_VERSION}/users`, async ({ request }) => {
    const url = new URL(request.url);
    const searchEmail = url.searchParams.get("searchEmail");

    // searchEmail 파라미터 사용
    if (!searchEmail) {
      const failResponse: ApiResponse<SearchUserResponse> = {
        status: "ERROR",
        error: {
          statusCode: 400,
          message: "이메일을 입력해주세요",
        },
      };
      return HttpResponse.json(failResponse);
    }

    const successResponse: ApiResponse<SearchUserResponse> = {
      status: "SUCCESS",
      data: {
        userId: 1,
        email: "test@test.com",
        userName: "홍길동",
        nickname: "홍길동",
        walletAddress: "0x1234567890",
      },
    };
    return HttpResponse.json(successResponse);
  }),

  http.patch(
    `${VITE_API_URL}/${VITE_API_VERSION}/notifications/:notificationId/read`,
    async () => {
      const successResponse: ApiResponse<null> = {
        status: "SUCCESS",
        data: null,
      };

      return HttpResponse.json(successResponse);
    },
  ),
];
