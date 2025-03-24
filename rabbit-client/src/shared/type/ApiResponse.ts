export interface ApiResponse<T> {
  data?: T;
  status: "SUCCESS" | "ERROR";
  error?: {
    statusCode: number;
    message: string;
  };
}
