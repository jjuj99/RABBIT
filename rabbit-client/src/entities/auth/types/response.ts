export interface RequestNonceResponse {
  nonce: string;
}
export interface LoginResponse {
  accessToken: string;
  user: User;
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
  user: User;
}
export interface User {
  nickname: string;
}
