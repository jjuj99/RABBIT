import { ApiResponse } from "@/shared/type/ApiResponse";
import { http, HttpResponse } from "msw";
import { SearchUserResponse } from "../types/response";
const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;
export const commonHandler = [
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
];
