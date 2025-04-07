import fetchOption from "@/shared/utils/fetchOption";
import {
  CreatePassRequest,
  LoginRequest,
  VerifyAccountRequest,
  VerifyPassRequest,
} from "../types/request";
import {
  CheckNicknameResponse,
  CreatePassResponse,
  LoginResponse,
  LogoutResponse,
  RefreshTokenResponse,
  RequestNonceResponse,
  SignUpResponse,
  User,
  VerifyAccountResponse,
  VerifyPassResponse,
} from "../types/response";
import { ApiResponse } from "@/shared/type/ApiResponse";
import { SignUpRequest } from "../types/schema";

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const GetUserAPI = async (): Promise<ApiResponse<User>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/user/me`,
    fetchOption("GET", undefined, "access"),
  );
  const data = await res.json();
  return data;
};
export const GetNonceAPI = async (
  walletAddress: string,
): Promise<ApiResponse<RequestNonceResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/nonce`,
    fetchOption("POST", { walletAddress }, "access"),
  );
  const data = await res.json();
  return data;
};
export const LoginAPI = async ({
  walletAddress,
  signature,
  nonce,
}: LoginRequest): Promise<ApiResponse<LoginResponse | null>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/login`,
    fetchOption("POST", { walletAddress, signature, nonce }, "refresh"),
  );
  const data = await res.json();
  return data;
};

export const LogoutAPI = async (): Promise<ApiResponse<LogoutResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/logout`,
    fetchOption("POST", undefined, "access"),
  );
  const data = await res.json();
  return data;
};

export const SignupAPI = async (
  userData: SignUpRequest,
): Promise<ApiResponse<SignUpResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/sign-up`,
    fetchOption("POST", userData),
  );
  if (!res.ok) {
    throw new Error("회원가입에 실패했습니다");
  }
  const data = await res.json();
  return data;
};

export const SendPassAPI = async (
  userData: CreatePassRequest,
): Promise<ApiResponse<CreatePassResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/pass/store`,
    fetchOption<CreatePassRequest>("POST", userData),
  );
  const data = await res.json();
  return data;
};

export const VerifyPassAPI = async (
  userData: VerifyPassRequest,
): Promise<ApiResponse<VerifyPassResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/pass/verify`,
    fetchOption<VerifyPassRequest>("POST", userData),
  );
  const data = await res.json();
  return data;
};

export const VerifyAccountAPI = async (
  userData: VerifyAccountRequest,
): Promise<ApiResponse<VerifyAccountResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/verify-account`,
    fetchOption("POST", userData),
  );
  const data = await res.json();
  return data;
};

export const RefreshTokenAPI = async (): Promise<
  ApiResponse<RefreshTokenResponse>
> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/refresh`,
    fetchOption("POST", undefined, "refresh"),
  );
  const data = await res.json();
  return data;
};

export const CheckNicknameAPI = async (
  nickname: string,
): Promise<ApiResponse<CheckNicknameResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/check-nickname?nickname=${nickname}`,
    fetchOption("GET", undefined, "access"),
  );
  if (!res.ok) {
    throw new Error("닉네임 중복 확인에 실패했습니다");
  }
  const data = await res.json();
  return data;
};
