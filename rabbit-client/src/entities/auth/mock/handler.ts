import { http, HttpResponse } from "msw";
import { LoginRequest } from "../types/request";
import { ApiResponse } from "@/shared/type/ApiResponse";
import {
  LoginResponse,
  RefreshTokenResponse,
  RequestNonceResponse,
  User,
} from "../types/response";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

// localStorage를 사용하여 상태 관리
const getIsLoggedIn = () => localStorage.getItem("msw_isLoggedIn") === "true";
const setIsLoggedIn = (value: boolean) =>
  localStorage.setItem("msw_isLoggedIn", String(value));

export const authHandlers = [
  // 난수생성 mock api
  http.post(`${VITE_API_URL}/${VITE_API_VERSION}/auth/nonce`, async () => {
    const response: ApiResponse<RequestNonceResponse> = {
      status: "SUCCESS",
      data: {
        nonce: "1234567890",
      },
    };
    return HttpResponse.json(response);
  }),
  // 로그인 mock api
  http.post(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/login`,
    async ({ request }) => {
      const body = (await request.json()) as LoginRequest;
      if (
        body.walletAddress == "0x35107a4c13d49bae2850c76d259e52808df0fa3c" ||
        body.walletAddress == "0x8c30721af30c6f7508303055c20afe73fca5dd26" ||
        body.walletAddress == "0xe7aca373766503357a1a8e84b1c3f71706e4d4f6"
      ) {
        const response: ApiResponse<LoginResponse> = {
          status: "SUCCESS",
          data: {
            accessToken: "1234567890",
            user: {
              nickname: "test",
            },
          },
        };

        setIsLoggedIn(true);
        console.log(getIsLoggedIn());
        return HttpResponse.json(response);
      } else {
        const response: ApiResponse<null> = {
          status: "ERROR",
          error: {
            statusCode: 400,
            message: "로그인 실패",
          },
        };

        return HttpResponse.json(response);
      }
    },
  ),
  // 리프레시 토큰 api
  http.post(`${VITE_API_URL}/${VITE_API_VERSION}/auth/refresh`, async () => {
    if (!getIsLoggedIn()) {
      const errorResponse: ApiResponse<{ message: string }> = {
        status: "ERROR",
        data: {
          message: "리프레시토큰이없습니다",
        },
      };
      return HttpResponse.json(errorResponse);
    }

    const successResponse: ApiResponse<RefreshTokenResponse> = {
      status: "SUCCESS",
      data: {
        accessToken: "1234567890",
        user: {
          nickname: "test",
        },
      },
    };
    return HttpResponse.json(successResponse);
  }),
  // 사용자 정보 조회 mock api
  http.get(`${VITE_API_URL}/${VITE_API_VERSION}/user/me`, async () => {
    const response: ApiResponse<User> = {
      status: "SUCCESS",
      data: { nickname: "test" },
    };
    return HttpResponse.json(response);
  }),
  // 로그아웃 mock api
  http.post(`${VITE_API_URL}/${VITE_API_VERSION}/auth/logout`, async () => {
    setIsLoggedIn(false);
    return HttpResponse.json({ status: "SUCCESS" });
  }),
];

// 테스트를 위한 헬퍼 함수들
export const mockAuthHelpers = {
  reset: () => {
    setIsLoggedIn(false);
  },
  setLoggedIn: (value: boolean) => {
    setIsLoggedIn(value);
  },
};
