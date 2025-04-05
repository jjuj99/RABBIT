export interface SearchUserResponse {
  userId: number;
  email: string;
  userName: string;
  nickname: string;
  walletAddress: string;
}

export interface CommonCodeResponse {
  codeType: string;
  code: string;
  codeName: string;
  description: string;
  displayOrder: number;
  activeFlag: boolean;
}
