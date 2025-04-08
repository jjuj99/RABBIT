export interface RequestNonceResponse {
  nonce: string;
}
export interface LoginResponse {
  accessToken: string;
  nickname: string;
  userName: string;
}

export interface LogoutResponse {
  message: string;
}

export interface SignUpResponse {
  message: string;
}

export interface CreatePassResponse {
  pass_id: number;
  message: string;
}

export interface VerifyPassResponse {
  isVerified: boolean;
  message: string;
}
export interface VerifyAccountResponse {
  isVerified: boolean;
  message: string;
}
export interface RefreshTokenResponse {
  accessToken: string;
  nickname: string;
  userName: string;
}
export interface User {
  nickname: string;
  userName: string;
  email: string;
  bankId: number;
  bankName: string;
  refundAccount: string;
}

export interface SignupResponse {
  message: string;
}

export interface CheckNicknameResponse {
  duplicated: boolean;
}
