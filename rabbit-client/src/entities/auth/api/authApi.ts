import fetchOption from "@/shared/utils/fetchOption";
import {
  CreatePassRequest,
  LoginRequest,
  SignUpRequest,
  VerifyAccountRequest,
  VerifyPassRequest,
} from "../types/request";
import {
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

const VITE_API_URL = import.meta.env.VITE_API_URL;
const VITE_API_VERSION = import.meta.env.VITE_API_VERSION;

export const GetUserAPI = async (): Promise<ApiResponse<User>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/user/me`,
    fetchOption("GET"),
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
  return await res.json();
};
export const LoginAPI = async ({
  walletAddress,
  signature,
  nonce,
}: LoginRequest): Promise<ApiResponse<LoginResponse | null>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/login`,
    fetchOption("POST", { walletAddress, signature, nonce }),
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

export const SignUpAPI = async (
  userData: SignUpRequest,
): Promise<ApiResponse<SignUpResponse>> => {
  const res = await fetch(
    `${VITE_API_URL}/${VITE_API_VERSION}/auth/sign-up`,
    fetchOption<SignUpRequest>("POST", userData),
  );
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
