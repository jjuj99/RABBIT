export interface RequestConfirm {
  orderId: string | null;
  amount: string | null;
  paymentKey: string | null;
}
export interface Send1wonRequest {
  email: string;
  accountNumber: string;
}
export interface Verify1wonRequest {
  email: string;
  authCode: string;
}

export interface WithdrawRequest {
  name: string;
  accountNumber: string;
  amount: number;
}
