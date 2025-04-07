export interface LoginRequest {
  walletAddress: string;
  signature: string;
  nonce: string;
}
export interface CreatePassRequest {
  name: string;
  phoneNumber: string;
  email: string;
}
export interface VerifyPassRequest {
  passId: number;
  verificationCode: string;
}

export interface VerifyAccountRequest {
  bankId: number;
  username: string;
  account: string;
}
