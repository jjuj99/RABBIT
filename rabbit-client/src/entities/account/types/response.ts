export interface AccountHistoryType {
  type: "deposit" | "withdraw";
  amount: number;
  date: string;
}
